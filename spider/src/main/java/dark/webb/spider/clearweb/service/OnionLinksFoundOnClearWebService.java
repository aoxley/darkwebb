package dark.webb.spider.clearweb.service;

import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWeb;
import dark.webb.spider.clearweb.data.OnionLinksFoundOnClearWebRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Alistair Oxley on 16/10/2017.
 */
@Service
public class OnionLinksFoundOnClearWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnionLinksFoundOnClearWebService.class);

    @Resource
    private OnionLinksFoundOnClearWebRepository onionLinksFoundOnClearWebRepository;

    public OnionLinksFoundOnClearWeb save(OnionLinksFoundOnClearWeb clearwebOnionLink) {
        OnionLinksFoundOnClearWeb savedOnionLink = onionLinksFoundOnClearWebRepository.findByOnionUrl(clearwebOnionLink.getOnionUrl());
        if (savedOnionLink == null) {
            LOGGER.debug("New onion link found, that hasn't been seen before. Attempting to save to DB: " + clearwebOnionLink.getOnionUrl());
            return onionLinksFoundOnClearWebRepository.save(clearwebOnionLink);
        }
        LOGGER.trace("Onion link that was sent for saving has previously been found: " + savedOnionLink.getOnionUrl() + " at date/time: " + savedOnionLink.getCreated().toString());
        return savedOnionLink;
    }
}
