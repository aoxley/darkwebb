package dark.webb.spider.darkweb.crawler;

import dark.webb.spider.darkweb.service.OnionLinksFoundOnDarkWebService;
import dark.webb.spider.elasticsearch.ElasticsearchService;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Alistair Oxley on 3/11/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WhenDeterminingIfCrawlerShouldVisitLinkTest {

    @Mock
    private ElasticsearchService elasticsearchService;
    @Mock
    private OnionLinksFoundOnDarkWebService onionLinksFoundOnDarkWebService;
    @InjectMocks
    private DarkWebCrawler darkWebCrawler;

    @Test
    public void shouldNotVisitPageAsParentPageHasSameBaseUrl() {
        Page referringPage = mock(Page.class);
        WebURL referringUrl = new WebURL();
        referringUrl.setURL("http://3g2upl4pq6kufc4m.onion/index");
        when(referringPage.getWebURL()).thenReturn(referringUrl);

        WebURL possibleUrlToVisit = new WebURL();
        possibleUrlToVisit.setURL("http://3g2upl4pq6kufc4m.onion/sub/directory");

        assertFalse(darkWebCrawler.shouldVisit(referringPage, possibleUrlToVisit));
    }

    @Test
    public void shouldVisitPageAsParentPageHasDifferentBaseUrl() {
        Page referringPage = mock(Page.class);
        WebURL referringUrl = new WebURL();
        referringUrl.setURL("http://3g2upl4pq6kufc4m.onion/index");
        when(referringPage.getWebURL()).thenReturn(referringUrl);

        WebURL possibleUrlToVisit = new WebURL();
        possibleUrlToVisit.setURL("http://expyuzz4wqqyqhjn.onion");

        assertTrue(darkWebCrawler.shouldVisit(referringPage, possibleUrlToVisit));
    }

    @Test
    public void shouldNotVisitPageAsTheLinkIsNotAnOnionLink() {
        Page referringPage = mock(Page.class);
        WebURL referringUrl = new WebURL();
        referringUrl.setURL("http://3g2upl4pq6kufc4m.com/index");
        when(referringPage.getWebURL()).thenReturn(referringUrl);

        WebURL possibleUrlToVisit = new WebURL();
        possibleUrlToVisit.setURL("http://google.com/sub/directory");

        assertFalse(darkWebCrawler.shouldVisit(referringPage, possibleUrlToVisit));
    }

    @Test
    public void shouldVisitPageAsUrlIsVersion2OnionUrl() {
        Page referringPage = mock(Page.class);
        WebURL referringUrl = new WebURL();
        referringUrl.setURL("http://f74jkyi76ehfj6ku.onion/index");
        when(referringPage.getWebURL()).thenReturn(referringUrl);

        WebURL possibleUrlToVisit = new WebURL();
        possibleUrlToVisit.setURL("http://qwdg3h4j5uydf3gr.onion");

        assertTrue(darkWebCrawler.shouldVisit(referringPage, possibleUrlToVisit));
    }

    @Test
    public void shouldVisitPageAsUrlIsVersion3OnionUrl() {
        Page referringPage = mock(Page.class);
        WebURL referringUrl = new WebURL();
        referringUrl.setURL("http://1jgk6iu4g31jgk6iu4g31jgk6iu4f41jgk6iu4g31jgk6iu4g3hgjtty.onion/index");
        when(referringPage.getWebURL()).thenReturn(referringUrl);

        WebURL possibleUrlToVisit = new WebURL();
        possibleUrlToVisit.setURL("http://ppppqh44jtpjrpqh44jtpl7pqh44jtpqweqh44jtpasdfh44jtrhgj66.onion");

        assertTrue(darkWebCrawler.shouldVisit(referringPage, possibleUrlToVisit));
    }
}
