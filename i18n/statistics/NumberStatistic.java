package info.kgeorgiy.ja.grigorev.i18n.statistics;

import java.util.*;

public class NumberStatistic implements Statistic<Number> {
    private final int count;

    private final List<Number> sortedList;
    private final Set<Number> uniqueValues;

    public NumberStatistic(final List<Number> words, final Locale locale) {
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
        return sortedList.getFirst().doubleValue();
    }

    @Override
    public Number getMaximum() {
        return sortedList.getLast().doubleValue();
    }

    @Override
    public Number getAverage() {
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
