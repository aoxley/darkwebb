package dark.webb.spider.clearweb.controller;

import dark.webb.spider.clearweb.crawler.ClearWebCrawlerFactory;
import dark.webb.spider.clearweb.service.OnionLinksFoundOnClearWebService;
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

/**
 * Created by Alistair Oxley on 15/10/2017.
 *
 * Controller for the spider that searches the clearweb, looking for new .onion web links.
 */
@Component
public class ClearWebSpiderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearWebSpiderController.class);

    private CrawlController controller;

    @Value("${spider.clearweb.storage.location}")
    private String storageFolder;
    @Value("${spider.clearweb.number.of.crawlers}")
    private int numberOfCrawlers;
    @Value("${spider.clearweb.user.agent}")
    private String userAgentString;
    @Value("${spider.clearweb.socket.timeout}")
    private int socketTimeout;

    @Resource
    private OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService;

    //reddit rules (https://github.com/reddit/reddit/wiki/API) have a max number of requests a min.
    private final int millisecondsPauseBetweenRequests = 2500;

    private ClearWebCrawlerFactory clearWebCrawlerFactory;

    @PostConstruct
    public void postConstruct() throws Exception {

        controller = getNewCrawlController();

        clearWebCrawlerFactory = new ClearWebCrawlerFactory(onionLinksFoundOnClearWebService);

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
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("https://www.reddit.com/r/onions/");
        controller.addSeed("https://www.reddit.com/r/deepweb/");
        controller.addSeed("https://www.reddit.com/r/DarkWebLinks/");
        return controller;
    }

    private CrawlConfig getCrawlConfig() {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(storageFolder + System.currentTimeMillis());
        config.setSocketTimeout(socketTimeout);
        config.setResumableCrawling(true);
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(millisecondsPauseBetweenRequests);
        config.setMaxDepthOfCrawling(5);
        return config;
    }

    private RobotstxtServer getRobotstxtServer(PageFetcher pageFetcher) {
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        return new RobotstxtServer(robotstxtConfig, pageFetcher);
    }

    /**
     * Attempt a kick-off every 30 minutes
     */
    @Scheduled(fixedRate = 300000L)
    public void scheduledRun() {
        startCrawling();
    }

    private void startCrawling() {
        if (controller.isFinished()) {
            try {
                controller = getNewCrawlController();
            } catch (Exception e) {
                LOGGER.error("Unable to create new controller for crawling the clear web!");
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
        LOGGER.info("Starting clear web spider");
        controller.startNonBlocking(clearWebCrawlerFactory, numberOfCrawlers);
        LOGGER.info("Clear web spider has started");
    }

    @PreDestroy
    public void preDestroy() {
        shutdownCrawler();
    }

    private void shutdownCrawler() {
        if (!controller.isShuttingDown()) {
            LOGGER.info("Shutting down clear web spider");
            controller.shutdown();
            controller.waitUntilFinish();
            LOGGER.info("Clear web spider shutdown successfully");
        }
        LOGGER.info("A call to shutdown the crawler was made, however it was already in the process of shutting down.");
    }
}
