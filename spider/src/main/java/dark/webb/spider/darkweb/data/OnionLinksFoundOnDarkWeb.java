package dark.webb.spider.darkweb.data;

import dark.webb.spider.common.data.BaseModel;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Alistair Oxley on 3/11/2017.
 *
 * Save away the onion links we find on the dark web.
 */
@Validated
@Table(schema = "DW", name = "OnionLinksFoundOnDarkWeb")
@Entity(name = "DW.OnionLinksFoundOnDarkWeb")
public class OnionLinksFoundOnDarkWeb extends BaseModel {

    @Column(length = 512, nullable = false)
    private String urlLinkWasFoundOn;

    @Column(length = 512, nullable = false)
    private String onionUrl;

    public OnionLinksFoundOnDarkWeb() {
    }

    public OnionLinksFoundOnDarkWeb(String urlLinkWasFoundOn, String onionUrl) {
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
