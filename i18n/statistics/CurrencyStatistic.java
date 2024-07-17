package info.kgeorgiy.ja.grigorev.i18n.statistics;

import java.util.*;

public class CurrencyStatistic implements Statistic<Number> {
    private final int count;

    private final List<Number> sortedList;
    private final Set<Number> uniqueValues;

    public CurrencyStatistic(final List<Number> words, final Locale locale) {
        this.sortedList = getSortedList(words, locale);
        this.count = sortedList.size();
        this.uniqueValues = new LinkedHashSet<>(sortedList);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getUniqueCount() {
        return uniqueValues.size();
    }

    @Override
    public Number getMinimum() {
        if (sortedList.isEmpty()) return null;
        return sortedList.getFirst().doubleValue();
    }

    @Override
    public Number getMaximum() {
        if (sortedList.isEmpty()) return null;
        return sortedList.getLast().doubleValue();
    }

    @Override
    public Number getAverage() {
        if (sortedList.isEmpty()) return null;
        return (getMinimum().doubleValue() + getMaximum().doubleValue()) / 2.0;
    }

    @Override
    public int getMinLength() {
        return 0;
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public double getAverageLength() {
        return 0;
    }

    @Override
    public List<Number> getSortedList(final List<Number> words, final Locale locale) {
        return words.stream().sorted(Comparator.comparingDouble(Number::doubleValue)).toList();
    }
}
