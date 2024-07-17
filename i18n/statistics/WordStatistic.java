package info.kgeorgiy.ja.grigorev.i18n.statistics;

import java.text.Collator;
import java.util.*;

public class WordStatistic implements Statistic<String> {
    private final int count;
    private final List<String> sortedList;
    private final Set<String> uniqueValues;

    public WordStatistic(final List<String> words, final Locale locale) {
        this.sortedList = getSortedList(words, locale);
        this.count = sortedList.size();
        this.uniqueValues = new LinkedHashSet<>(sortedList);
    }


    public List<String> getSortedList(final List<String> words, final Locale locale) {
        final Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        return words.stream().filter(word -> word.codePoints().anyMatch(Character::isLetter)).sorted(collator).toList();

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
    public String getMinimum() {
        return sortedList.getFirst();
    }

    @Override
    public String getMaximum() {
        return sortedList.getLast();
    }

    @Override
    public String getAverage() {
        return sortedList.get(getCount() / 2);
    }

    @Override
    public int getMinLength() {
        return sortedList.stream().map(Object::toString).mapToInt(String::length).min().orElse(0);
    }

    @Override
    public int getMaxLength() {
        return sortedList.stream().map(Object::toString).mapToInt(String::length).max().orElse(0);
    }

    @Override
    public double getAverageLength() {
        return (double) (getMinLength() + getMaxLength()) / 2;
    }

}
