package dark.webb.spider.darkweb.crawler;

import dark.webb.spider.darkweb.service.OnionLinksFoundOnDarkWebService;
import dark.webb.spider.elasticsearch.ElasticsearchService;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

/**
 * Created by Alistair Oxley on 3/10/2017.
 */
public class DarkWebCrawlerFactory implements CrawlController.WebCrawlerFactory {

    private ElasticsearchService elasticsearchService;
    private OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService;

    public DarkWebCrawlerFactory(ElasticsearchService elasticsearchService, OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService) {
        this.elasticsearchService = elasticsearchService;
        this.onionLinksFoundOnDarkWebService = onionLinksFoundOnDarkWebService;
    }

    @Override
    public WebCrawler newInstance() throws Exception {
        return new DarkWebCrawler(elasticsearchService, onionLinksFoundOnDarkWebService);
    }
}
