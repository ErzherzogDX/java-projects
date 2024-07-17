package info.kgeorgiy.ja.grigorev.i18n;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.*;

import info.kgeorgiy.ja.grigorev.i18n.statistics.Statistic;
import org.junit.jupiter.api.*;

import static java.nio.file.Files.newBufferedReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextAnalyzerTest {
    private static TextAnalyzer textAnalyzerEnglish;
    private static TextAnalyzer textAnalyzerRussian;
    private static TextAnalyzer textAnalyzerSpanish;
    private static TextAnalyzer textAnalyzerArabic;
    private static TextAnalyzer textAnalyzerJapanese;
    private static TextAnalyzer textAnalyzerHindi;


    private static List<TextAnalyzer> analyzers;

    @BeforeAll
    static void beforeAll() {
        analyzers = new ArrayList<>();
        analyzers = (List.of(textAnalyzerEnglish = new TextAnalyzer(Locale.forLanguageTag("en-US"), Locale.forLanguageTag("en-US")),
                textAnalyzerRussian = new TextAnalyzer(Locale.forLanguageTag("ru-RU"), Locale.forLanguageTag("en-US")),
                textAnalyzerSpanish = new TextAnalyzer(Locale.forLanguageTag("es"), Locale.forLanguageTag("en-US")),
                textAnalyzerArabic = new TextAnalyzer(Locale.forLanguageTag("ar"), Locale.forLanguageTag("en-US")),
                textAnalyzerJapanese = new TextAnalyzer(Locale.forLanguageTag("ja"), Locale.forLanguageTag("en-US")),
                textAnalyzerHindi = new TextAnalyzer(Locale.forLanguageTag("hi"), Locale.forLanguageTag("en-US"))));
    }

    private final List<String> modes = List.of("sentences", "words", "numbers", "currency", "dates");
    private final List<String> shortTexts = List.of("This is the JAVA ADVANCED course. Welcome to the HW13. Enjoy!",
            "Это курс JAVA ADVANCED. Добро пожаловать в ДЗ13. Наслаждайтесь!",
            "Este es un curso de JAVA AVANZADO. Bienvenido a la tarea 13. ¡Disfrútalo!",
            "هذه دورة جافا المتقدمة. مرحبًا بك في الواجب المنزلي 13. استمتع!",
            "これはJAVA ADVANCEDコースです。 宿題 13 へようこそ。お楽しみください。",
            "यह एक जावा एडवांस्ड कोर्स है। होमवर्क 13 में आपका स्वागत है। आनंद लें!");

    private void testStatistic(final Statistic<?> statistic, final int count, final int uniqueCount, final Object minimum, final Object average, final Object maximum, final int minLength, final double avgLength, final int maxLength) {
        assertEquals(count, statistic.getCount());
        assertEquals(uniqueCount, statistic.getUniqueCount());
        assertEquals(minimum, statistic.getMinimum());
        assertEquals(average, statistic.getAverage());
        assertEquals(maximum, statistic.getMaximum());
        assertEquals(minLength, statistic.getMinLength());
        assertEquals(avgLength, statistic.getAverageLength());
        assertEquals(maxLength, statistic.getMaxLength());
    }

    @Test
    public void testEmpty() {
        final String text = "";
        for (final TextAnalyzer analyzer : analyzers) {
            testStatistic(analyzer.analyze(text).get("sentences"), 0, 0, null, null, null, 0, 0, 0);
        }
    }

    @Test
    public void testAnalyzeSentences() {
        final List<Statistic<?>> statistics = new ArrayList<>();
        for (int i = 0; i < analyzers.size(); i++) {
            statistics.add(analyzers.get(i).analyze(shortTexts.get(i)).get("sentences"));
        }
        final int count = 3;
        testStatistic(statistics.get(0), count, count, "Enjoy!", "This is the JAVA ADVANCED course.", "Welcome to the HW13.", 6, 19.5, 33);
        testStatistic(statistics.get(1), count, count, "Добро пожаловать в ДЗ13.", "Наслаждайтесь!", "Это курс JAVA ADVANCED.", 14, 19.0, 24);
        testStatistic(statistics.get(2), count, count, "¡Disfrútalo!", "Bienvenido a la tarea 13.", "Este es un curso de JAVA AVANZADO.", 12, 23.0, 34);
        testStatistic(statistics.get(3), count, count, "استمتع!", "مرحبًا بك في الواجب المنزلي 13.", "هذه دورة جافا المتقدمة.", 7, 19.0, 31);
        testStatistic(statistics.get(4), count, count, "お楽しみください。", "これはJAVA ADVANCEDコースです。", "宿題 13 へようこそ。", 9, 15.5, 22);
        testStatistic(statistics.get(5), count, count, "आनंद लें!", "यह एक जावा एडवांस्ड कोर्स है।", "होमवर्क 13 में आपका स्वागत है।", 9, 19.5, 30);
    }

    @Test
    public void testAnalyzeWords() {
        final List<Statistic<?>> statistics = new ArrayList<>();
        for (int i = 0; i < analyzers.size(); i++) {
            statistics.add(analyzers.get(i).analyze(shortTexts.get(i)).get("words"));
        }

//        for(int i = 0; i < analyzers.size(); i++){
//            System.out.println(statistics.get(i).getCount() + ", " + statistics.get(i).getUniqueCount() + ", \"" + statistics.get(i).getMinimum() + "\", \"" + statistics.get(i).getAverage()+ "\", \"" + statistics.get(i).getMaximum() + "\", " + statistics.get(i).getMinLength() + ", " + statistics.get(i).getAverageLength()  + ", " +  statistics.get(i).getMaxLength());
//        }
        testStatistic(statistics.get(0), 11, 10, "ADVANCED", "JAVA", "Welcome", 2, 5.0, 8);
        testStatistic(statistics.get(1), 9, 9, "ADVANCED", "Добро", "Это", 1, 7.0, 13);
        testStatistic(statistics.get(2), 12, 12, "a", "es", "un", 1, 5.5, 10);
        testStatistic(statistics.get(3), 10, 10, "استمتع", "جافا", "هذه", 2, 5.0, 8);
        testStatistic(statistics.get(4), 10, 10, "ADVANCED", "しみください", "宿題", 1, 4.5, 8);
        testStatistic(statistics.get(5), 13, 12, "आनंद", "में", "होमवर्क", 2, 5.0, 8);
    }

    @Test
    public void testNumeric() {
        final String text = " 23 мая 2024 г. Думайте. The numbers - 1, 2, 3, 42, 232, 3232.0, 232, 1 1000!  123. 444 ₽ 1000$ May 23, 2024  15 августа 1096 г.";
        final Map<String, Statistic<?>> x = textAnalyzerRussian.analyze(text);
        final Statistic<?> numbersStats = textAnalyzerRussian.analyze(text).get("numbers");
        final Statistic<?> currencyStats = textAnalyzerRussian.analyze(text).get("currency");
        final Statistic<?> dateStats = textAnalyzerRussian.analyze(text).get("dates");

        final Date currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("ru-RU")).parse("23 мая 2024 г.", new ParsePosition(0));
        final Date pastDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("ru-RU")).parse("15 августа 1096 г.", new ParsePosition(0));
        final GregorianCalendar calendar = new GregorianCalendar(1560, Calendar.JUNE, 27, 12, 0, 0);
        final Date medDate = calendar.getTime();

        testStatistic(numbersStats, 18, 13, 1.0, 1616.5, 3232.0, 0, 0, 0);
        testStatistic(currencyStats, 1, 1, 444.0, 444.0, 444.0, 0, 0, 0);
        testStatistic(dateStats, 2, 2, pastDate, medDate, currentDate, 0, 0, 0);
    }

    @Test
    public void testRussianText() {
        final String input = Paths.get("").toAbsolutePath() + File.separator + Path.of("solutions\\java-solutions\\info\\kgeorgiy\\ja\\grigorev\\i18n\\tests\\test.txt");
        final Path s = Paths.get(input);


        String text = null;
        try {
            text = Files.readString(s);
            final Map<String, Statistic<?>> x = textAnalyzerRussian.analyze(text);
            final Statistic<?> wordsStats = textAnalyzerRussian.analyze(text).get("words");
            final Statistic<?> sentencesStats = textAnalyzerRussian.analyze(text).get("sentences");
            final Statistic<?> numbersStats = textAnalyzerRussian.analyze(text).get("numbers");
            final Statistic<?> currencyStats = textAnalyzerRussian.analyze(text).get("currency");
            final Statistic<?> dateStats = textAnalyzerRussian.analyze(text).get("dates");

            final Date currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("ru-RU")).parse("09 мая 2025 г.", new ParsePosition(0));

            final String minSen = "В дальнейшем, 22 ноября 2007 года в городскую черту было передано 5,2715 га (0,05 км²) территории Минского района[20].";
            final String maxSen = "Является ядром Минской агломерации.";
            final String avgSen = "Мінск) — столица и крупнейший город Беларуси, административный центр Минской области и Минского района, в состав которых не входит, поскольку является самостоятельной административно-территориальной единицей с особым (столичным) статусом.";
            final String minWord = "а";
            final String avgWord = "ядром";
            final String maxWord = "Минской";
            testStatistic(sentencesStats, 21, 21, minSen, avgSen, maxSen, 13, 190.5, 368);
            testStatistic(wordsStats, 325, 217, minWord, maxWord, avgWord, 1, 16, 31);
            testStatistic(numbersStats, 49, 43, 0.05, 2047.5656000000001, 4095.0812, 0, 0, 0);
            testStatistic(currencyStats, 0, 0, null, null, null, 0, 0, 0);
            testStatistic(dateStats, 1, 1, currentDate, currentDate, currentDate, 0, 0, 0);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterAll
    static void afterAll() {
        for (final TextAnalyzer analyzer : analyzers) {
            analyzer.closeAnalyzer();
        }
    }


}
