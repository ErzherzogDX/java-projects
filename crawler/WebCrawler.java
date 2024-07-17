package info.kgeorgiy.ja.grigorev.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static info.kgeorgiy.java.advanced.crawler.URLUtils.getHost;

public class WebCrawler implements AdvancedCrawler {
    private final Downloader downloader;
    private final int perHost;
    private final ExecutorService serviceD;
    private final ExecutorService serviceE;
    private CopyOnWriteArrayList<String> result;
    private final ConcurrentHashMap<String, IOException> exceptions;
    private final Set<String> used;
    private final Set<String> urlPerPhase;

    // :NOTE: unbound growth
    private final ConcurrentHashMap<String, AtomicInteger> hosts;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> newQueue;


    private static void printDebug(String st, String dx, String phas) {
        //   System.out.println(st + " " + dx + " " + phas);
    }

    private static boolean containsSubstring(Collection<String> substrings, String input) {
        return substrings.stream()
                .anyMatch(input::contains);
    }


    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;

        result = new CopyOnWriteArrayList<>();
        exceptions = new ConcurrentHashMap<>();
        used = ConcurrentHashMap.newKeySet();
        urlPerPhase = ConcurrentHashMap.newKeySet();
        hosts = new ConcurrentHashMap<>();
        newQueue = new ConcurrentHashMap<>();
        serviceD = Executors.newFixedThreadPool(downloaders);
        serviceE = Executors.newFixedThreadPool(extractors);
    }


    private Result finalDownload(String url, int depth, Collection<String> hosts, boolean mode) {
        urlPerPhase.add(url);
        Set<String> hostsSet = new HashSet<>();
        hostsSet.addAll(hosts);

        for (int i = 0; i < depth; i++) {
            List<String> listOfWebsite = new ArrayList<>(urlPerPhase.stream().toList());
            AtomicInteger cnt = new AtomicInteger(listOfWebsite.size());
            List<Future<?>> futures = new ArrayList<>();
            for (Map.Entry<String, AtomicInteger> entry : this.hosts.entrySet()) {
                String key = entry.getKey();
                this.hosts.get(key).set(0);
            }

            List<String> purifiedListOfWebsites = new ArrayList<>();
            for (int j = 0; j < listOfWebsite.size(); j++) {
                String curURL = listOfWebsite.get(j);
                try {
                    String host = getHost(curURL);
                    boolean isStringInList = hostsSet.stream().anyMatch(str -> str.equals(host));
                    if (((mode) ? isStringInList : !containsSubstring(hosts, curURL)) && used.add(curURL)) {
                        if (!this.hosts.containsKey(host)) {
                            this.hosts.put(host, new AtomicInteger());
                            newQueue.put(host, new ConcurrentLinkedDeque<>());
                        }
                        if (this.hosts.get(host).get() >= perHost) {
                            newQueue.get(host).add(curURL);
                        } else {
                            purifiedListOfWebsites.add(curURL);
                        }
                        this.hosts.get(host).incrementAndGet();
                    } else {
                        cnt.decrementAndGet();
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            for (int j = 0; j < purifiedListOfWebsites.size(); j++) {
                newQueue.get(purifiedListOfWebsites.get(j));
                futures.add(serviceD.submit(new getDocumentTask(purifiedListOfWebsites.get(j), downloader, serviceE, result, exceptions, urlPerPhase, cnt, this.hosts, newQueue)));
            }
            // :NOTE: active wait?
            while (cnt.get() > 0) {

            }
        }
        serviceD.shutdown();
        serviceE.shutdown();
        return new Result(result, exceptions);
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url      start URL.
     * @param depth    download depth.
     * @param excludes URLs containing one of given substrings are ignored.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        return finalDownload(url, depth, excludes, false);
    }


    /**
     * Downloads website up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth) {
        return download(url, depth, null);
    }

    /**
     * Closes this crawler, freeing any allocated resources.
     */
    @Override
    public void close() {
        serviceD.close();
        serviceE.close();
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @param hosts domains to follow, pages on another domains should be ignored.
     * @return download result.
     */
    @Override
    public Result advancedDownload(String url, int depth, List<String> hosts) {
        return finalDownload(url, depth, hosts, true);
    }

    /**
     * Setup the WebCrawler using cmd.
     *
     * @param args parameters to download
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 5) {
            throw new IllegalArgumentException("You should put in input 5 args.");
        }

        WebCrawler wc = new WebCrawler(new CachingDownloader(0), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        Result x = wc.download(args[0], Integer.parseInt(args[1]));

        System.out.println("===== DOWNLOADED =====");
        for (int i = 0; i < x.getDownloaded().size(); i++) {
            System.out.println(x.getDownloaded().get(i));
        }

        System.out.println("===== FAILED =====");
        for (String key : x.getErrors().keySet()) {
            String value = String.valueOf(x.getErrors().get(key));
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    private static class getDocumentTask implements Runnable {
        private String url;
        private final Downloader downloader;
        private final ExecutorService extractor;
        private final CopyOnWriteArrayList<String> result;
        private final ConcurrentHashMap<String, IOException> exceptions;
        private final Set<String> urlPerPhase;
        private final AtomicInteger cnt;
        private final ConcurrentHashMap<String, AtomicInteger> hosts2;
        private final ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> newQueue;


        public getDocumentTask(String url, Downloader downloader, ExecutorService extractor, CopyOnWriteArrayList<String> result,
                               ConcurrentHashMap<String, IOException> exceptions, Set<String> urlPerPhase, AtomicInteger cnt,
                               ConcurrentHashMap<String, AtomicInteger> hosts2, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> newQueue) {
            this.url = url;
            this.downloader = downloader;
            this.extractor = extractor;
            this.result = result;
            this.exceptions = exceptions;
            this.urlPerPhase = urlPerPhase;
            this.cnt = cnt;
            this.hosts2 = hosts2;
            this.newQueue = newQueue;
        }

        @Override
        public void run() {
            while (true) {
                urlPerPhase.remove(url);
                try {
                    Document document = downloader.download(url);
                    result.add(url);
                    extractor.submit(new getLinksTask(document, urlPerPhase, cnt));
                } catch (IOException e) {
                    exceptions.put(url, e);
                    cnt.decrementAndGet();
                }

                try {
                    String currentHost = getHost(url);
                    if (!newQueue.get(currentHost).isEmpty()) {
                        String nextURL = newQueue.get(currentHost).pollFirst();
                        hosts2.get(currentHost).decrementAndGet();
                        url = nextURL;
                    } else {
                        break;
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class getLinksTask implements Runnable {
        Document document;
        Set<String> urlPerPhase;
        AtomicInteger cnt;

        public getLinksTask(Document document, Set<String> urlPerPhase, AtomicInteger cnt) {
            this.document = document;
            this.urlPerPhase = urlPerPhase;
            this.cnt = cnt;
        }

        @Override
        public void run() {
            try {
                urlPerPhase.addAll(document.extractLinks());
            } catch (Exception e) {
            }
            cnt.decrementAndGet();
        }
    }
}