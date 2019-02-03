package dark.webb.spider.elasticsearch.data;

import java.util.Map;

/**
 * Created by Alistair Oxley on 3/10/2017.
 */
public class SiteDataBuilder {

    public SiteDataBuilder() {}

    public UrlInterface withStatusCode(int statusCode) {
        return new InternalSiteDataBuilder(statusCode);
    }

    public interface UrlInterface {
        public ParentUrlInterface withUrl(String url);
    }

    public interface ParentUrlInterface {
        public TitleInterface withParentUrl(String parentUrl);
    }

    public interface TitleInterface {
        public MetaTagsInterface withTitle(String title);
    }

    public interface MetaTagsInterface {
        public HtmlInterface withMetaTags(Map<String, String> metaTags);
    }

    public interface HtmlInterface {
        public TextInterface withHtml(String html);
    }

    public interface TextInterface {
        public BuilderInterface withText(String text);
    }

    public interface BuilderInterface {
        public SiteData build();
    }

    public class InternalSiteDataBuilder implements UrlInterface, ParentUrlInterface, TitleInterface, MetaTagsInterface, HtmlInterface, TextInterface, BuilderInterface {
        private int statusCode;
        private String url;
        private String parentUrl;
        private String title;
        private Map<String, String> metaTags;
        private String html;
        private String text;

        private InternalSiteDataBuilder(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public ParentUrlInterface withUrl(String url) {
            this.url = url;
            return this;
        }

        @Override
        public TitleInterface withParentUrl(String parentUrl) {
            this.parentUrl = parentUrl;
            return this;
        }

        @Override
        public MetaTagsInterface withTitle(String title) {
            this.title = title;
            return this;
        }

        @Override
        public HtmlInterface withMetaTags(Map<String, String> metaTags) {
            this.metaTags = metaTags;
            return this;
        }

        @Override
        public TextInterface withHtml(String html) {
            this.html = html;
            return this;
        }

        @Override
        public BuilderInterface withText(String text) {
            this.text = text;
            return this;
        }

        @Override
        public SiteData build() {
            return new SiteData(statusCode, url, parentUrl, title, metaTags, html, text);
        }
    }
}
