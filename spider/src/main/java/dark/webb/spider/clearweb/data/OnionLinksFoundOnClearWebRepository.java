package dark.webb.spider.clearweb.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Alistair Oxley on 16/10/2017.
 */
@Repository
public interface OnionLinksFoundOnClearWebRepository extends CrudRepository<OnionLinksFoundOnClearWeb, Long> {
    
    OnionLinksFoundOnClearWeb findByOnionUrl(String onionUrl);
}
