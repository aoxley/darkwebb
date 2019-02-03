package dark.webb.spider.darkweb.service;

import dark.webb.spider.darkweb.data.OnionLinksFoundOnDarkWeb;
import dark.webb.spider.darkweb.data.OnionLinksFoundOnDarkWebRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Alistair Oxley on 3/11/2017.
 */
@Service
public class OnionLinksFoundOnDarkWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnionLinksFoundOnDarkWebService.class);

    @Resource
    private OnionLinksFoundOnDarkWebRepository onionLinksFoundOnDarkWebRepository;

    public OnionLinksFoundOnDarkWeb save(OnionLinksFoundOnDarkWeb onionLinksFoundOnDarkWeb) {
        OnionLinksFoundOnDarkWeb savedOnionLink = onionLinksFoundOnDarkWebRepository.findByOnionUrl(onionLinksFoundOnDarkWeb.getOnionUrl());
        if (savedOnionLink == null) {
            LOGGER.debug("New onion link found, that hasn't been seen before. Attempting to save to DB: " + onionLinksFoundOnDarkWeb.getOnionUrl());
            return onionLinksFoundOnDarkWebRepository.save(onionLinksFoundOnDarkWeb);
        }
        LOGGER.trace("Onion link that was sent for saving has previously been found: " + savedOnionLink.getOnionUrl() + " at date/time: " + savedOnionLink.getCreated().toString());
        return savedOnionLink;
    }

    public Boolean alreadySaved(String onionUrl) {
        return onionLinksFoundOnDarkWebRepository.findByOnionUrl(onionUrl) != null;
    }
}
