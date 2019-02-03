package dark.webb.spider.clearweb.data;

import dark.webb.spider.common.data.BaseModel;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Alistair Oxley on 15/10/2017.
 */
@Validated
@Table(schema = "CW", name = "OnionLinksFoundOnClearWeb")
@Entity(name = "CW.OnionLinksFoundOnClearWeb")
public class OnionLinksFoundOnClearWeb extends BaseModel {

    @Column(length = 512, nullable = false)
    private String urlLinkWasFoundOn;

    @Column(length = 512, nullable = false)
    private String onionUrl;

    public OnionLinksFoundOnClearWeb() {
    }

    public OnionLinksFoundOnClearWeb(String urlLinkWasFoundOn, String onionUrl) {
        this.urlLinkWasFoundOn = urlLinkWasFoundOn;
        this.onionUrl = onionUrl;
    }

    public String getUrlLinkWasFoundOn() {
        return urlLinkWasFoundOn;
    }

    public void setUrlLinkWasFoundOn(String urlLinkWasFoundOn) {
        this.urlLinkWasFoundOn = urlLinkWasFoundOn;
    }

    public String getOnionUrl() {
        return onionUrl;
    }

    public String getFullyQualifiedUrl() {
        return "http://www." + this.onionUrl;
    }

    public void setOnionUrl(String onionUrl) {
        this.onionUrl = onionUrl;
    }
}
