package dark.webb.spider.clearweb.crawler;

import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWeb;
import dark.webb.spider.clearweb.service.OnionLinksFoundOnClearWebService;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alistair Oxley on 15/10/2017.
 */
public class ClearWebCrawler extends WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearWebCrawler.class);

    private OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");

    private final Pattern ONION_LINK_PATTERN = Pattern.compile("([2-7|a-z]{16}\\.onion)|([2-7|a-z]{56}\\.onion)");
    private Matcher onionLinkMatcher = ONION_LINK_PATTERN.matcher("");


    public ClearWebCrawler(OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService) {
        this.onionLinksFoundOnClearWebService = onionLinksFoundOnClearWebService;
    }

    /**
     * Ensuring that the links we visit are only html pages, and within the reddit domain.
     *
     * @param referringPage
     * @param url
     * @return
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        LOGGER.trace("Determining if clear web crawler should visit: " + href);
        return !FILTERS.matcher(href).matches() && url.getURL().toLowerCase().startsWith("https://www.reddit.com");
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            onionLinkMatcher = ONION_LINK_PATTERN.matcher(htmlParseData.getText());
            while (onionLinkMatcher.find()) {
                LOGGER.debug("Found onion site on the clear web: " + onionLinkMatcher.group(0));
                onionLinksFoundOnClearWebService.save(new OnionLinksFoundOnClearWeb(page.getWebURL().getURL(), onionLinkMatcher.group(0)));
            }
        }
    }

    @Override
    protected void onUnhandledException(WebURL webUrl, Throwable e) {
        LOGGER.debug("unhandled exception: " + webUrl.getURL() + ". " + e.getMessage());
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {

    }
}
