package info.kgeorgiy.ja.grigorev.i18n.statistics;

import java.util.*;

public class DateStatistic implements Statistic<Date> {
    private final int count;
    private final List<Date> sortedList;
    private final Set<Date> uniqueValues;

    public DateStatistic(List<Date> words, Locale locale) {
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
    public Date getMinimum() {
        if(sortedList.isEmpty()) return null;
        return sortedList.getFirst();
    }

    @Override
    public Date getMaximum() {
        if(sortedList.isEmpty()) return null;
        return sortedList.getLast();
    }

    @Override
    public Date getAverage() {
        if(sortedList.isEmpty()) return null;
        return new Date(sortedList.stream().mapToLong(Date::getTime).sum() / sortedList.size());
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
    public List<Date> getSortedList(List<Date> words, Locale locale) {
        return words.stream().sorted().toList();
    }

}
