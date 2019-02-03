package dark.webb.spider.darkweb.controller;

import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWeb;
import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWebRepository;
import dark.webb.spider.darkweb.crawler.DarkWebCrawlerFactory;
import dark.webb.spider.darkweb.data.OnionLinksFoundOnDarkWeb;
import dark.webb.spider.darkweb.data.OnionLinksFoundOnDarkWebRepository;
import dark.webb.spider.darkweb.service.OnionLinksFoundOnDarkWebService;
import dark.webb.spider.elasticsearch.ElasticsearchService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Alistair Oxley on 29/09/2017.
 */
@Component
public class DarkWebSpiderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DarkWebSpiderController.class);

    private CrawlController controller;

    @Value("${spider.darkweb.storage.location}")
    private String storageFolder;
    @Value("${spider.darkweb.number.of.crawlers}")
    private int numberOfCrawlers;
    @Value("${spider.darkweb.user.agent}")
    private String userAgentString;
    @Value("${spider.darkweb.socket.timeout}")
    private int socketTimeout;
    @Value("${tor.proxy.host}")
    private String proxyHost;
    @Value("${tor.proxy.port}")
    private int proxyPort;

    @Resource
    private ElasticsearchService elasticsearchService;
    @Resource
    private OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService;
    private DarkWebCrawlerFactory darkWebCrawlerFactory;

    @Resource
    private OnionLinksFoundOnClearWebRepository onionLinksFoundOnClearWebRepository;
    @Resource
    private OnionLinksFoundOnDarkWebRepository onionLinksFoundOnDarkWebRepository;

    @PostConstruct
    public void postConstruct() throws Exception {

        controller = getNewCrawlController();
        /*
         * Ensure that we at least have one starting point.
         */
        controller.addSeed("http://www.torlinkbgs6aabns.onion");

        darkWebCrawlerFactory = new DarkWebCrawlerFactory(elasticsearchService, onionLinksFoundOnDarkWebService);

        startCrawlingDoNotCheckIfFinished();
    }

    /**
     * Why don't we just set it once on initialisation and leave it. We were getting database closed issues
     * after the first close. check: https://github.com/yasserg/crawler4j/issues/186
     * @return
     * @throws Exception
     */
    private CrawlController getNewCrawlController() throws Exception {
        CrawlConfig config = getCrawlConfig();
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtServer robotstxtServer = getRobotstxtServer(pageFetcher);
        return new CrawlController(config, pageFetcher, robotstxtServer);
    }

    private CrawlConfig getCrawlConfig() {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(storageFolder + System.currentTimeMillis());
        config.setProxyHost(proxyHost);
        config.setProxyPort(proxyPort);
        config.setSocketTimeout(socketTimeout);
        config.setResumableCrawling(true);
        return config;
    }

    private RobotstxtServer getRobotstxtServer(PageFetcher pageFetcher) {
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        return new RobotstxtServer(robotstxtConfig, pageFetcher);
    }

    private void seedControllerWithUrls() {
        List<OnionLinksFoundOnClearWeb> onionLinkFoundOnClearWebs = (List) onionLinksFoundOnClearWebRepository.findAll();
        for (OnionLinksFoundOnClearWeb link : onionLinkFoundOnClearWebs) {
            controller.addSeed(link.getFullyQualifiedUrl());
        }
        List<OnionLinksFoundOnDarkWeb> onionLinksFoundOnDarkWebs = (List) onionLinksFoundOnDarkWebRepository.findAll();
        for (OnionLinksFoundOnDarkWeb link : onionLinksFoundOnDarkWebs) {
            controller.addSeed(link.getFullyQualifiedUrl());
        }
    }

    /**
     * Attempt a kick-off every 30 minutes
     */
    @Scheduled(fixedRate = 300000L)
    public void scheduledRun() {
        startCrawling();
    }

    /**
     * Checks if the crawler has finished running, and if it has, will start it.
     **/
    private void startCrawling() {
        if (controller.isFinished()) {
            try {
                controller = getNewCrawlController();
            } catch (Exception e) {
                LOGGER.error("Unable to create new controller for crawling the dark web!");
                LOGGER.debug(e.getMessage());
                return;
            }
            startCrawlingDoNotCheckIfFinished();
        }
    }

    /**
     * Doesn't check if the crawler is running. This is needed in the initial instance of kicking the crawler off.
     */
    private void startCrawlingDoNotCheckIfFinished() {
        seedControllerWithUrls();
        LOGGER.info("Starting dark web spider");
        controller.startNonBlocking(darkWebCrawlerFactory, numberOfCrawlers);
        LOGGER.info("Dark web spider has started");
    }

    @PreDestroy
    public void preDestroy() {
        shutdownCrawler();
    }

    private void shutdownCrawler() {
        if (!controller.isShuttingDown()) {
            LOGGER.info("Shutting down dark web spider");
            controller.shutdown();
            controller.waitUntilFinish();
            LOGGER.info("Dark web spider shutdown successfully");
            return;
        }
        LOGGER.info("A call to shutdown the crawler was made, however it was already in the process of shutting down.");
    }
}
