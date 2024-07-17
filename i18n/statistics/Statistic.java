package info.kgeorgiy.ja.grigorev.i18n.statistics;

import java.util.List;
import java.util.Locale;

public interface Statistic<T> {
    // :NOTE: no access modif in interface
    public int getCount();

    public int getUniqueCount();

    public T getMinimum();

    public T getMaximum();

    public T getAverage();


    public int getMinLength();
    public int getMaxLength();

    public double getAverageLength();

    List<T> getSortedList(List<T> words, Locale locale);
}
