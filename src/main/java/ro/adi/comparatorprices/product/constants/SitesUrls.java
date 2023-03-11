package ro.adi.comparatorprices.product.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*LA_JUMATE=PUBLI24*/
public enum SitesUrls {

    OLX("https://www.olx.ro/d/"), PUBLI24("https://www.publi24.ro/anunturi/");

    private static final Map<String, SitesUrls> mapByLabel = new HashMap<>();
    private static final Map<SitesUrls, String> mapByName = new HashMap<>();

    static {
        for (SitesUrls url : SitesUrls.values()) {
            mapByLabel.put(url.label, url);
            mapByName.put(url, url.label);
        }
    }

    private final String label;

    SitesUrls(String label) {
        this.label = label;
    }

    public static SitesUrls getByLabel(String label) {
        if (mapByLabel.containsKey(label))
            return mapByLabel.get(label);
        return null;// TODO exception
    }

    public static String getUrlByEnum(SitesUrls urlsEnum) {
        if (mapByName.containsKey(urlsEnum))
            return mapByName.get(urlsEnum);
        return null;// TODO exception
    }

    public static ArrayList<String> getUrls() {
        return new ArrayList<>(mapByLabel.keySet());
    }

}
