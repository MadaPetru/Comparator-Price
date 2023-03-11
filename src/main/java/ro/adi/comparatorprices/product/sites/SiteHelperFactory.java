package ro.adi.comparatorprices.product.sites;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.adi.comparatorprices.product.constants.SitesUrls;

@Component
@RequiredArgsConstructor
public class SiteHelperFactory {

    private final OlxHelper olxHelper;
    private final Publi24Helper publi24Helper;

    public SiteHelper createCorrespondentSiteHelper(SitesUrls site) {
        switch (site) {
        case PUBLI24: {
            return publi24Helper;
        }
        case OLX:
            return olxHelper;
        }
        throw new RuntimeException();
    }
}