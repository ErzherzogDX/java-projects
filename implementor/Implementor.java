package info.kgeorgiy.ja.grigorev.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import static java.lang.Character.isUnicodeIdentifierPart;

/**
 * Implementation {@link JarImpler} interfaces.
 * Generates a file with the class by the {@link Class} token.
 * Has two modes: {@link  #implement(Class, Path)} (generates <var>.java</var>-file)
 * and {@link #implementJar(Class, Path)} (generates <var>.jar</var>-file).
 *
 * @author Dmitrii Grigorev
 * @see info.kgeorgiy.java.advanced.implementor.Impler
 * @see info.kgeorgiy.java.advanced.implementor.JarImpler
 */

public class Implementor implements JarImpler {

    /**
     * Default constructor for {@link Implementor}.
     */
    public Implementor() {
    }

    /**
     * This method realizing algorithm of getting list of methods of given class {@code toImplement}:
     * <ol>
     *     <li>
     *        Adding all available, regardless of their access modifiers, methods of current class via {@link Class#getDeclaredMethods()}
     *     </li>
     *     <li>
     *        Iterating over all of implemented interfaces from {@link Class#getInterfaces()} and calling recursively
     *        this method from available interfaces.
     *     </li>
     *     <li>
     *        If given {@code toImplement} is extends from another class (and it was found by {@link Class#getSuperclass()}),
     *        this method will be called recursively for superclass.
     *     </li>
     * </ol>
     *
     * @param toImplement given {@link Class} to get list of methods.
     * @return {@link List} of methods from given {@link Class} {@code toImplement} and methods from all his interfaces
     * and super-methods.
     */
    private List<Method> getListOfImplementingMethods(Class<?> toImplement) {
        List<Method> allMethods = new ArrayList<>();
        Method[] declaredMethods = toImplement.getDeclaredMethods();
        Class<?>[] interfaces = toImplement.getInterfaces();

        Collections.addAll(allMethods, declaredMethods);
        allMethods.addAll(Arrays.stream(interfaces)
                .flatMap(anInterface -> getListOfImplementingMethods(anInterface).stream())
                .toList());

        Class<?> superClass = toImplement.getSuperclass();
        if (superClass != null) {
            allMethods.addAll(getListOfImplementingMethods(superClass));
        }
        return allMethods;
    }

    /**
     * Retrieves the file path for the specified class implementation and extension within the specified root directory.
     *
     * @param root      the root directory
     * @param clazz     the class for which the file path is to be retrieved
     * @param extension the file extension
     * @return the file path for the specified class and extension within the root directory
     */
    private Path getFile(final Path root, final Class<?> clazz, final String extension) {
        return root.resolve((clazz.getPackageName() + "." + clazz.getSimpleName() + "Impl")
                .replace(".", File.separator) + "." + extension);
    }


    /**
     * Checking given class {@code check} for possibility of implementing.
     *
     * @param check checked {@link Class} for implementation
     * @throws ImplerException if given class is restricted for implementing
     */
    private void checkClass(final Class<?> check) throws ImplerException {
        if (Modifier.isPrivate(check.getModifiers())) {
            throw new ImplerException("Provided class must PUBLIC.");
        }
        if (Modifier.isFinal(check.getModifiers())) {
            throw new ImplerException("Provided class must be a non-final class.");
        }
        if (check.isEnum() || check == Enum.class) {
            throw new ImplerException("Provided class should be not ENUM.");
        }
        if (check.isRecord() || check == Record.class) {
            throw new ImplerException("Provided class should be not RECORD.");
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param aClass type token to create implementation for.
     * @param path   root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     *                                                                 generated.
     */
    @Override
    public void implement(Class<?> aClass, Path path) throws ImplerException {
        checkClass(aClass);
        String implementationName = aClass.getSimpleName() + "Impl";
        final Path filePath = getFile(path, aClass, "java").getParent();
        String implementationCode = generateCode(aClass, implementationName);
        saveToFile(implementationCode, implementationName, filePath);
    }


    /**
     * Retrieves the default value for the return type of a method.
     *
     * @param method the method whose return type's default value is to be retrieved
     * @return the default value for the return type of the method, or {@code null} if the return type is a reference type
     */
    private static String getDefaultValue(Method method) {
        Class<?> defaultValue = method.getReturnType();
        if (defaultValue == int.class || defaultValue == long.class ||
                defaultValue == short.class || defaultValue == float.class ||
                defaultValue == double.class || defaultValue == char.class ||
                defaultValue == byte.class
        ) {
            return "0";
        } else if (defaultValue == boolean.class) {
            return "false";
        } else {
            return null;
        }
    }


    /**
     * Checks if the parameters of two methods are identical.
     *
     * @param method1 the first method
     * @param method2 the second method
     * @return {@code true} if the parameters of both methods are identical; {@code false} otherwise
     */
    private static boolean checkParameters(Method method1, Method method2) {
        Parameter[] parameters1 = method1.getParameters();
        Parameter[] parameters2 = method2.getParameters();
        if (parameters1.length != parameters2.length) {
            return false;
        }
        return IntStream.range(0, parameters1.length)
                .allMatch(i -> parameters1[i].getType().equals(parameters2[i].getType()));
    }


    /**
     * Writes header of the implementing class, which contains information about package, name and
     * <ol>
     *     <li>
     *        If given class is an interface, implementation should {@code implements} it.
     *     </li>
     *     <li>
     *        If given class is an class, implementation should {@code extends} it.
     *     </li>
     * </ol>
     * <p>
     * to the given {@code code}.
     *
     * @param code        {@link StringBuilder}, which contains code of implementation.
     * @param toImplement given {@link Class} to implementation
     * @param implName    name of given implementation
     */

    private void writeHeader(StringBuilder code, Class<?> toImplement, String implName) {
        code.append("package ").append(toImplement.getPackageName()).append("; \n");
        code.append("public class ").append(implName);
        code.append((toImplement.isInterface()) ? " implements " : " extends ");
        code.append(toImplement.getCanonicalName()).append(" {\n");
    }

    /**
     * Retrieves a set of constructors for the specified class to be implemented.
     *
     * @param toImplement the class for which constructors are to be retrieved
     * @return a set of constructors for the specified class
     */
    private Set<Constructor<?>> getConstructorsSet(Class<?> toImplement) {
        return Stream.concat(Arrays.stream(toImplement.getConstructors()),
                        Arrays.stream(toImplement.getDeclaredConstructors()))
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .collect(Collectors.toSet());
    }


    /**
     * Checks the conditions for a method to be overridden.
     *
     * @param method the method to be checked
     * @return {@code true} if the method satisfies the conditions for overriding; {@code false} otherwise
     */
    private boolean methodConditions(Method method) {
        return !Modifier.isFinal(method.getModifiers()) &&
                !Modifier.isStatic(method.getModifiers()) &&
                !Modifier.isPrivate(method.getModifiers()) &&
                Modifier.isAbstract(method.getModifiers());
    }

    /**
     * Writing to the given {@code code} constructor {@code constructor} with parameters pack {@code params}.
     * Constructor in output implementation will have empty body and will call {@code super()} to his ancestors.
     *
     * @param code        {@link StringBuilder}, which contains code of implementation.
     * @param constructor {@link Constructor}, which implementing in this method
     * @param implName    name of given implementation
     */
    private void writeConstructor(StringBuilder code, Constructor<?> constructor, String implName) {
        Parameter[] params = constructor.getParameters();

        code.append("public ").append(implName).append(" ");
        writeParameters(code, params, true);
        writeExceptions(code, constructor);

        code.append("{\nsuper");
        writeParameters(code, params, false);
        code.append(";\n}\n");
    }

    /**
     * Writing to the given {@code code} method {@code method} with parameters pack {@code params}.
     * Method in output implementation will have empty body and, if it is not void, method will return
     * default value of given method's type.
     *
     * @param code   code to write method
     * @param method {@link Method} to write
     * @param params arrays of parameters to write
     */
    private void writeMethod(StringBuilder code, Method method, Parameter[] params) {
        code.append("public ").append(method.getReturnType().getCanonicalName()).append(" ");

        code.append(method.getName());
        writeParameters(code, params, true);
        writeExceptions(code, method);

        code.append("{\n");
        if (method.getReturnType() != void.class) {
            code.append("    return ").append(getDefaultValue(method)).append(";\n");
        }
        code.append("}\n");
    }


    /**
     * Generates the implementation code for a specified class to the {@link String}.
     * Algorithm:
     * <ol>
     *     <li>
     *        Generates constructors from {@link Implementor#getConstructorsSet(Class)}, if given class
     *        doesn't interface, and writing it via {@link Implementor#writeConstructor(StringBuilder, Constructor, String)}
     *     </li>
     *     <li>
     *        Getting {@link Implementor#getListOfImplementingMethods(Class)} and writing given methods via {@link Implementor#writeMethod(StringBuilder, Method, Parameter[])},
     *        taking into account repetitions and excluding them
     *     </li>
     * </ol>
     *
     * @param toImplement the class to be implemented
     * @param implName    the name of the implementation class
     * @return the generated implementation code, presented by {@link String}
     * @throws ImplerException if an error occurs during code generation
     */
    private String generateCode(Class<?> toImplement, String implName) throws ImplerException {
        StringBuilder code = new StringBuilder();
        writeHeader(code, toImplement, implName);
        Set<Constructor<?>> allConstructors = getConstructorsSet(toImplement);

        if (!toImplement.isInterface()) {
            if (allConstructors.isEmpty()) {
                throw new ImplerException("Implementing class hasn't any constructor");
            }
            for (Constructor<?> constructor : allConstructors) {
                writeConstructor(code, constructor, implName);
            }
        }

        ArrayList<Method> newMethodList = new ArrayList<>(getListOfImplementingMethods(toImplement));
        ArrayList<Method> usedMethods = new ArrayList<>();
        for (Method method : newMethodList) {
            Parameter[] params = method.getParameters();
            boolean used = usedMethods.stream().anyMatch(usedMethod -> usedMethod.getName().equals(method.getName())
                    && checkParameters(method, usedMethod));
            if (Modifier.isPrivate(method.getReturnType().getModifiers())) {
                throw new ImplerException("Return type of method cannot be private");
            }
            if (methodConditions(method) && !used) {
                checkMethodArguments(params);
                writeMethod(code, method, params);
            }
            usedMethods.add(method);
        }
        code.append("}");
        return code.toString();
    }

    /**
     * Writing to the given {@code code} names of parameters from {@link Parameter} array {@code params}. If {@code writeTypes} is true,
     * then method will write parameters name with their type, otherwise it will write just the names, separated by comma.
     * If method hasn't possible throwable exceptions, this method do nothing.
     * <p>
     * Names of parameters are presented by {@link Parameter#getName()}, types are represented by canonical name of
     * {@link Parameter#getType()}
     *
     * @param code       {@link StringBuilder}, which contains code of implementation.
     * @param params     array of parameters
     * @param writeTypes write types of parameters or not
     */
    private void writeParameters(StringBuilder code, Parameter[] params, boolean writeTypes) {
        code.append("(")
                .append(Arrays.stream(params)
                        .map(x -> (writeTypes ? x.getType().getCanonicalName() : "") + " " + x.getName())
                        .collect(Collectors.joining(", "))).append(")");

    }

    /**
     * Writing to the given {@code code} possible exceptions from given {@code mth} - constructor or general method.
     * If method hasn't possible throwable exceptions, this method do nothing.
     *
     * @param code array of method parameters
     * @param mth  method or constructor
     */
    private void writeExceptions(StringBuilder code, Executable mth) {
        Class<?>[] exceptions = mth.getExceptionTypes();
        if (exceptions.length > 0) {
            code.append("throws ")
                    .append(Arrays.stream(exceptions)
                            .map(Class::getCanonicalName)
                            .collect(Collectors.joining(", ")));
        }
    }

    /**
     * Checking parameters pack {@code params} for presence of private parameters.
     *
     * @param params array of method parameters
     * @throws ImplerException if private parameter was found.
     */
    private void checkMethodArguments(Parameter[] params) throws ImplerException {
        for (Parameter parameter : params) {
            if (Modifier.isPrivate(parameter.getType().getModifiers())) {
                throw new ImplerException("Parameters in the implementing method CANNOT BE PRIVATE");
            }
        }
    }

    /**
     * Creating the directory from given {@code path}, new java-file with name {@code classname}, which contains
     * generated code from {@code code}.
     *
     * @param code      generated code of implementing class
     * @param className name of implementing class
     * @param path      path to directory, which will contain java-class
     * @throws ImplerException if writing to java-file was failed.
     */
    private void saveToFile(String code, String className, Path path) throws ImplerException {
        try {
            Files.createDirectories(path);
            Path filePath = path.resolve(className + ".java");
            Files.writeString(filePath, unicodeConvertation(code));
        } catch (IOException e) {
            throw new ImplerException("Failed to write file", e);
        }
    }


    /**
     * Converts characters in the code text to equivalent ones according to the standard unicode.
     *
     * @param code source code
     * @return the converted code
     */
    private String unicodeConvertation(String code) {
        StringBuilder purifiedCode = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (isUnicodeIdentifierPart(c)) {
                purifiedCode.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
            } else {
                purifiedCode.append(c);
            }
        }
        return purifiedCode.toString();
    }


    /**
     * Retrieves the classpath of the specified class.
     *
     * @param clazz the class whose classpath is to be retrieved
     * @return the classpath of the specified class
     * @throws ImplerException if an error occurs while retrieving the classpath
     */
    private String getClassPath(Class<?> clazz) throws ImplerException {
        try {
            return Path.of(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final InvalidPathException e) {
            throw new ImplerException("Invalid class path was given");
        } catch (final URISyntaxException e) {
            throw new ImplerException("URI syntax error was found");
        }
    }

    /**
     * Compiles the implemented class using the specified compiler and classpath.
     *
     * @param clazz the class to be compiled
     * @param path  the path where the compiled file will be stored
     * @throws ImplerException if an error occurs during compilation
     */

    private void compileImplemented(Class<?> clazz, Path path) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Java compiler is not detected.");
        }
        String[] args = new String[]{
                "-encoding",
                StandardCharsets.UTF_8.name(),
                "-cp",
                getClassPath(clazz),
                getFile(path, clazz, "java").toString()};
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Exit code from compiler should be 0, but actual is - " + exitCode);
        }
    }

    /**
     * Constructs a JAR file containing the specified class implementation.
     *
     * @param dir    the directory where supporting files are located
     * @param path   the path where the JAR file will be stored
     * @param aClass the class whose implementation is to be included in the JAR file
     * @throws ImplerException if an error occurs during JAR file construction
     */

    private void constructJar(Path dir, Path path, Class<?> aClass) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR, "Dmitrii Grigoriev");

        try (JarOutputStream jarOutputStream =
                     new JarOutputStream(new BufferedOutputStream(Files.newOutputStream(path)), manifest)) {
            jarOutputStream.putNextEntry(new ZipEntry(aClass.getPackageName()
                    .replace(".", "/")
                    + String.format("/%sImpl.class", aClass.getSimpleName())));

            Files.copy(getFile(dir, aClass, "class"), jarOutputStream);
        } catch (IOException e) {
            throw new ImplerException("Error during a jar file writing " + e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param aClass type token to create implementation for.
     * @param path   target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> aClass, Path path) throws ImplerException {
        Path dir;
        try {
            dir = Files.createTempDirectory(path.toAbsolutePath().getParent(), "implementor");
        } catch (IOException e) {
            throw new ImplerException("Can't create temporary dir: " + e.getMessage());
        }

        implement(aClass, dir);
        compileImplemented(aClass, dir);
        constructJar(dir, path, aClass);
    }


}
