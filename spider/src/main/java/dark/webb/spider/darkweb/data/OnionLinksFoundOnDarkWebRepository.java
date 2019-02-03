package dark.webb.spider.darkweb.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Alistair Oxley on 3/11/2017.
 */
@Repository
public interface OnionLinksFoundOnDarkWebRepository extends CrudRepository<OnionLinksFoundOnDarkWeb, Long> {
    OnionLinksFoundOnDarkWeb findByOnionUrl(String onionUrl);
}
