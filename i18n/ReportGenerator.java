package info.kgeorgiy.ja.grigorev.i18n;

import info.kgeorgiy.ja.grigorev.i18n.statistics.Statistic;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Stream;

public class ReportGenerator {
    private final Locale locale;
    private final ResourceBundle bundle;

    public ReportGenerator(final Locale locale) {
        this.locale = locale;
        Locale.setDefault(locale);
        this.bundle = PropertyResourceBundle.getBundle("info.kgeorgiy.ja.grigorev.i18n.bundles.Report");  // :NOTE: to const?
    }

    private String getUniqueNumberKey(final int num) {
        final int div10 = num % 10;
        final int div100 = num % 100;
        if (div10 == 1 && div100 != 11) {
            return "unique_single";
        }
        return "unique_multiple";
    }

    public String generateReport(final Map<String, Statistic<?>> stats, final String inputFile) {
        /*
        return format("Analyzing file \"{0}\".", inputFile) + "\n\n" +
                // Summary statistics
                "Summary Statistics\n" +
                format("    Number of sentences: {0}.", stats.get("sentences").getCount()) + "\n" +
                format("    Number of words: {0}.", stats.get("words").getCount()) + "\n" +
                format("    Number of numbers: {0}.", stats.get("numbers").getCount()) + "\n" +
                format("    Number of currency values: {0}.", stats.get("currency").getCount()) +
                format("    Number of dates: {0}.", stats.get("dates").getCount()) + "\n" +

                // Detailed statistics
                "\nDetailed Statistics\n" +
                generateDetailedStats("Sentences", stats.get("sentences")) +
                generateDetailedStats("Words", stats.get("words")) +
                generateDetailedStats("Numbers", stats.get("numbers")) +
                generateDetailedStats("Currency Values", stats.get("currency")) +
                generateDetailedStats("Dates", stats.get("dates"));
         */

        // :NOTE: \n
        // :NOTE: MessageFormat
        final String report = format(bundle.getString("analyzing_file"), inputFile) + "\n\n" +

                // Summary statistics
                bundle.getString("summary_statistics") + "\n" +
                format(bundle.getString("number_of_sentences"), stats.get("sentences").getCount()) + "\n" +
                format(bundle.getString("number_of_words"), stats.get("words").getCount()) + "\n" +
                format(bundle.getString("number_of_numbers"), stats.get("numbers").getCount()) + "\n" +
                format(bundle.getString("number_of_currency"), stats.get("currency").getCount()) + "\n" +
                format(bundle.getString("number_of_dates"), stats.get("dates").getCount()) + "\n" +

                // Detailed statistics
                "\n" + bundle.getString("detailed_statistics") + "\n" +
                generateDetailedStats("category_sentences", stats.get("sentences")) +
                generateDetailedStats("category_words", stats.get("words")) +
                generateDetailedStats("category_numbers", stats.get("numbers")) +
                generateDetailedStats("category_currency", stats.get("currency")) +
                generateDetailedStats("category_dates", stats.get("dates"));

        return report;
    }

    private boolean getFullStats(final String category) {
        return Objects.equals(category, "category_words") || Objects.equals(category, "category_sentences");
    }


    private Object getAdditions(final String category, final Object str) {
        if (Objects.equals(category, "category_words") || Objects.equals(category, "category_sentences")) {
            return "\"" + str + "\"";
        } else if (Objects.equals(category, "category_currency")) {
            final NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
            return currency.format(str);
        }
        return str;
    }

    private String generateDetailedStats(final String category, final Statistic<?> stats) {
        final Object min = (stats.getCount() > 0) ? getAdditions(category, stats.getMinimum()) : bundle.getString("not_found");
        final Object max = (stats.getCount() > 0) ? getAdditions(category, stats.getMaximum()) : bundle.getString("not_found");
        final Object avg = (stats.getCount() > 0) ? getAdditions(category, stats.getAverage()) : bundle.getString("not_found");

        final String uniqKey = getUniqueNumberKey(stats.getUniqueCount());
        final List<Object> args = List.of(bundle.getString(category), stats.getCount(), stats.getUniqueCount(), bundle.getString(uniqKey), min, max, avg);
        final List<Object> args_length = List.of(stats.getMinLength(), stats.getMaxLength(), stats.getAverageLength());

        if (getFullStats(category)) {
            return format(bundle.getString("category_stats"), Stream.concat(args.stream(), args_length.stream())
                    .toArray());
        }
        return format(bundle.getString("category_stats_short"), args.toArray());
    }

    private String format(final String pattern, final Object... arguments) {
        return new MessageFormat(pattern, locale).format(arguments);
    }
}
