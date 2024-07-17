package info.kgeorgiy.ja.grigorev.i18n;

import info.kgeorgiy.ja.grigorev.i18n.statistics.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

// :NOTE: javadoc?
public class TextAnalyzer {
    private final Locale locale;
    private final Locale outputLocale; // :NOTE: unused
    private final List<DateFormat> dateFormatList;
    private final List<Integer> dataFormatStylesList;
    private final int NUM_OF_CATEGORIES = 5;

    private final List<Modes> modesList = List.of(
            Modes.SENTENCES,
            Modes.WORDS,
            Modes.NUMBERS,
            Modes.CURRENCY,
            Modes.DATES
    );
    private final List<String> stringList = List.of("sentences", "words", "numbers", "currency", "dates");

    private final ExecutorService cathegoryService;

    public TextAnalyzer(final Locale locale, final Locale outputLocale) {
        this.locale = locale;
        this.outputLocale = outputLocale;
        this.dataFormatStylesList = List.of(DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT);
        this.dateFormatList = List.of(DateFormat.getDateInstance(dataFormatStylesList.get(0), locale),
                DateFormat.getDateInstance(dataFormatStylesList.get(1), locale),
                DateFormat.getDateInstance(dataFormatStylesList.get(2), locale),
                DateFormat.getDateInstance(dataFormatStylesList.get(3), locale));
        this.cathegoryService = Executors.newFixedThreadPool(NUM_OF_CATEGORIES); // :NOTE: modesList.length()
    }

    public Map<String, Statistic<?>> analyze(final String text) {
        final Map<String, Statistic<?>> stats = new ConcurrentHashMap<>();
        final List<Future<Statistic<?>>> futures = new ArrayList<>();

        for (int i = 0; i < modesList.size(); i++) {
            futures.add(cathegoryService.submit(new getStatisticCat(text, i)));
        }
        for (int i = 0; i < futures.size(); i++) {
            try {
                final Statistic<?> res = futures.get(i).get();
                stats.put(stringList.get(i), res);
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return stats;
    }

    private Date checkDate(final String text, final ParsePosition parsePos) {
        for (final DateFormat dateFormat : dateFormatList) {
            final Date currentDate = dateFormat.parse(text, parsePos);
            if (currentDate != null) {
                return currentDate;
            }
        }
        return null;
    }

    private Number checkNumber(final String text, final ParsePosition parsePos) {
        final NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.parse(text, parsePos);
    }

    private Number checkCurrency(final String text, final ParsePosition parsePos) {
        final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        return nf.parse(text, parsePos);
    }

    private String checkSubstring(final String text, final ParsePosition parsePos) {
        String result = null;
        if (parsePos.getErrorIndex() != -1) {
            result = text.substring(parsePos.getIndex(), parsePos.getErrorIndex()).trim();
        }
        return result;
    }


    private <T> List<T> getList(final String text, final Modes mode, final BiFunction<String, ParsePosition, T> function) {
        final BreakIterator iterator;
        switch (mode) {
            case SENTENCES -> iterator = BreakIterator.getSentenceInstance(locale);
            case WORDS, NUMBERS, DATES, CURRENCY -> iterator = BreakIterator.getWordInstance(locale);
            case null, default -> iterator = BreakIterator.getCharacterInstance(locale);
        }
        iterator.setText(text);
        final List<T> nfounded = new ArrayList<>();
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            final String substring = text.substring(start, end).trim();
            final ParsePosition pss = new ParsePosition(start);
            pss.setErrorIndex(end);

            final T currentNum;
            if (!substring.isEmpty()) {
                switch (mode) {
                    case WORDS, SENTENCES -> currentNum = function.apply(text, pss);
                    case DATES, NUMBERS, CURRENCY -> currentNum = function.apply(text, new ParsePosition(start));
                    case null, default -> currentNum = null;
                }
                if (currentNum != null) {
                    nfounded.add(currentNum);
                }
            }
        }
        return nfounded;
    }

    public void closeAnalyzer() {
        cathegoryService.close();
    }

    private Statistic<?> analyzeText(final String text, final Modes mode) {
        final Statistic<?> result;
        switch (mode) {
            case WORDS -> result = new WordStatistic(getList(text, mode, this::checkSubstring), locale);
            case SENTENCES -> result = new SentenceStatistic(getList(text, mode, this::checkSubstring), locale);
            case DATES -> result = new DateStatistic(getList(text, mode, this::checkDate), locale);
            case NUMBERS -> result = new NumberStatistic(getList(text, mode, this::checkNumber), locale);
            case CURRENCY -> result = new CurrencyStatistic(getList(text, mode, this::checkCurrency), locale);
            case null, default -> result = null;
        }
        return result;
    }

    private class getStatisticCat implements Callable<Statistic<?>> {
        String text;
        int index;

        public getStatisticCat(final String text, final int index) {
            this.text = text;
            this.index = index;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public Statistic<?> call() throws Exception {
            return analyzeText(text, modesList.get(index));
        }
    }
}
