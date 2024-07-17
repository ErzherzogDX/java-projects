package info.kgeorgiy.ja.grigorev.i18n;

import info.kgeorgiy.ja.grigorev.i18n.statistics.Statistic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.util.*;

public class TextStatistics {
    public static void main(final String[] args) {
        // :NOTE: args = null
        if (args.length != 4) {
            // :NOTE: localize usage
            System.err.println("Usage: TextStatistics <text_locale> <output_locale> <input_file> <report_file>");
            System.exit(1);
        }

        // :NOTE: args[i] = null
        final Locale textLocale = Locale.forLanguageTag(args[0]);
        final Locale outputLocale = Locale.forLanguageTag(args[1]);

        final String inputFile = getRootPath() + args[2];
        final String reportFile = getRootPath() + args[3];

        try {
            final String text = readFile(inputFile);
            final TextAnalyzer analyzer = new TextAnalyzer(textLocale, outputLocale);
            final Map<String, Statistic<?>> stats = analyzer.analyze(text);

            final ReportGenerator generator = new ReportGenerator(outputLocale);
            final String report = generator.generateReport(stats, inputFile);

            writeFile(reportFile, report);
            analyzer.closeAnalyzer();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRootPath() {
        return Paths.get("").toAbsolutePath() + File.separator;
    }

    private static String readFile(final String fileName) throws IOException {
        final Path s = Paths.get(fileName);
        return Files.readString(s);
    }

    private static void writeFile(final String fileName, final String content) throws IOException {
        Files.writeString(Paths.get(fileName), content);
    }
}
