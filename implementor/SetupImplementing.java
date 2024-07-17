package info.kgeorgiy.ja.grigorev.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * The class that runs the {@link Implementor}
 * @author Dmitrii Grigorev
 */
public class SetupImplementing {

    /**
     * Default constructor for {@link SetupImplementing}.
     */
    public SetupImplementing() {
    }

    /**
     * Main method for executing the {@link Implementor}. Setup implementing given class in two modes - output may be
     * in  java-file or jar-file
     *
     * @param args command line arguments
     * @throws ImplerException if an error occurs during implementation
     * @throws ClassNotFoundException if the class specified in the arguments is not found
     * @throws IllegalArgumentException if the number of arguments is invalid
     */
    public static void main(String[] args) throws ImplerException, ClassNotFoundException {
        if (args.length != 1 && args.length != 3) {
            throw new IllegalArgumentException("You should put in input only one (path to implemented method, Implementor" +
                    "will generate .java-file) or three (-jar flag, name of given class, path to implementing) arguments");
        }
        Implementor implementor = new Implementor();
        if (args.length == 1) {
            Class<?> clazz = Class.forName(args[0]);
            implementor.implement(clazz, Paths.get(""));
        } else{
            Class<?> clazz = Class.forName(args[1]);
            implementor.implementJar(clazz, Path.of(args[2]));
        }
    }
}
