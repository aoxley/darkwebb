package dark.webb.spider.elasticsearch.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Alistair Oxley on 3/10/2017.
 */
public class SiteData {
    private static final int version = 1;
    @JsonProperty("@timestamp")
    private Long created = System.currentTimeMillis();
    private int statusCode;
    private String url;
    private String parentUrl;
    private String title;
    private Map<String, String> metaTags;
    private String html;
    private String text;

    //enhancement fields
    private String hashOfSite;

    public SiteData(int statusCode, String url, String parentUrl, String title, Map<String, String> metaTags, String html, String text) {
        this.statusCode = statusCode;
        this.url = url;
        this.parentUrl = parentUrl;
        this.title = title;
        this.metaTags = metaTags;
        this.html = html;
        this.text = text;
        generateHash(html);
    }

    private void generateHash(String html) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (digest != null) {
            digest.update(this.html.getBytes());
            this.hashOfSite = DatatypeConverter.printHexBinary(digest.digest()).toUpperCase();
        }
    }

    public int getVersion() {
        return version;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getMetaTags() {
        return metaTags;
    }

    public void setMetaTags(Map<String, String> metaTags) {
        this.metaTags = metaTags;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHashOfSite() {
        return hashOfSite;
    }
}
