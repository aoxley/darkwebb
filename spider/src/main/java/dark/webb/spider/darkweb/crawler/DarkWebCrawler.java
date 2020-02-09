package dark.webb.spider.darkweb.crawler;

import dark.webb.spider.darkweb.data.OnionLinksFoundOnDarkWeb;
import dark.webb.spider.darkweb.service.OnionLinksFoundOnDarkWebService;
import dark.webb.spider.elasticsearch.ElasticsearchService;
import dark.webb.spider.elasticsearch.data.SiteData;
import dark.webb.spider.elasticsearch.data.SiteDataBuilder;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Alistair Oxley on 29/09/2017.
 */
public class DarkWebCrawler extends WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DarkWebCrawler.class);

    private final static Pattern ONION_LINK_PATTERN = Pattern.compile("([2-7|a-z]{16}\\.onion)|([2-7|a-z]{56}\\.onion)");

    private ElasticsearchService elasticsearchService;
    private OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService;

    public DarkWebCrawler(ElasticsearchService elasticsearchService, OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService) {
        this.elasticsearchService = elasticsearchService;
        this.onionLinksFoundOnDarkWebService = onionLinksFoundOnDarkWebService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String referringDomain = referringPage.getWebURL().getDomain();
        final String domain = url.getDomain();
        LOGGER.trace("Determining if dark web crawler should visit: " + url.getURL().toLowerCase());
        //TODO! add lookup in dark web table to see if we hit it. but this then means we never revisit a site....
        return !StringUtils.equalsIgnoreCase(domain, referringDomain) && ONION_LINK_PATTERN.matcher(domain).matches();
    }

    @Override
    public void visit(Page page) {
        LOGGER.debug("visited dark web page: " + page.getWebURL().getURL());
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            saveDataToElasticsearch(page, htmlParseData);
            onionLinksFoundOnDarkWebService.save(new OnionLinksFoundOnDarkWeb(page.getWebURL().getParentUrl(), page.getWebURL().getDomain()));
        }
    }

    private void saveDataToElasticsearch(Page page, HtmlParseData htmlParseData) {
        final SiteData siteData = new SiteDataBuilder()
                .withStatusCode(page.getStatusCode())
                .withUrl(page.getWebURL().getURL())
                .withParentUrl(page.getWebURL().getParentUrl())
                .withTitle(htmlParseData.getTitle())
                .withMetaTags(htmlParseData.getMetaTags())
                .withHtml(htmlParseData.getHtml())
                .withText(htmlParseData.getText())
                .build();

        LOGGER.debug("Attempting to save data from dark web site: " + page.getWebURL().getURL());
        elasticsearchService.saveSiteDataToElasticsearch(siteData);
    }

    @Override
    protected void onUnhandledException(WebURL webUrl, Throwable e) {
        if (e instanceof SocketTimeoutException) {
            LOGGER.trace("socket connection timeout, most likely the site isn't up: " + webUrl.getURL());
        } else {
            LOGGER.debug("unhandled exception: " + webUrl.getURL() + ". " + e.getMessage());
        }
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {

    }
}
