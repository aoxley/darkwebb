package dark.webb.spider.clearweb.crawler;

import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWeb;
import dark.webb.spider.clearweb.service.OnionLinksFoundOnClearWebService;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Alistair Oxley on 15/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WhenMatchingOnionLinksTest {

    private ClearWebCrawler clearWebCrawler;

    @Mock
    private OnionLinksFoundOnClearWebService onionLinksFoundOnClearWebService;
    @Mock
    private Page page;
    @Mock
    private HtmlParseData parseData;

    private ArgumentCaptor<OnionLinksFoundOnClearWeb> savingOnionLinkCaptor;

    @Before
    public void setup() {
        clearWebCrawler = new ClearWebCrawler(onionLinksFoundOnClearWebService);
        savingOnionLinkCaptor = ArgumentCaptor.forClass(OnionLinksFoundOnClearWeb.class);
    }

    @Test
    public void shouldFindSingleOnionLink() {
        final String pageText = "I made this new service: http://besthqdirnimrgpj.onion/ check it out!";
        when(parseData.getText()).thenReturn(pageText);
        when(page.getParseData()).thenReturn(parseData);
        WebURL webURL = new WebURL();
        webURL.setURL("http://pageOnionLinkWasFoundOn.com");
        when(page.getWebURL()).thenReturn(webURL);

        clearWebCrawler.visit(page);

        verify(onionLinksFoundOnClearWebService).save(savingOnionLinkCaptor.capture());
        assertThat(savingOnionLinkCaptor.getValue().getOnionUrl(), is("besthqdirnimrgpj.onion"));
    }
}
