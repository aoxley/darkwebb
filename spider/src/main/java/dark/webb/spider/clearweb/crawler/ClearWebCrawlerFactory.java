package dark.webb.spider.clearweb.crawler;

import dark.webb.spider.clearweb.service.OnionLinksFoundOnClearWebService;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

/**
 * Created by Alistair Oxley on 15/10/2017.
 */
public class ClearWebCrawlerFactory implements CrawlController.WebCrawlerFactory {

    private OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService;

    public ClearWebCrawlerFactory(OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService) {
        this.onionLinksFoundOnClearWebService = onionLinksFoundOnClearWebService;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return new ClearWebCrawler(onionLinksFoundOnClearWebService);
    }
}
