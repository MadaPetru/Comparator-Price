package ro.adi.comparatorprices.product.constants;

import java.util.HashMap;
import java.util.Map;

public enum ProductCurrencyEnum {
    RON("ron"), EURO("euro");

    public static final Map<String, ProductCurrencyEnum> mapByLabel = new HashMap<>();

    public String value;

    static {
        for (ProductCurrencyEnum currency : ProductCurrencyEnum.values()) {
            mapByLabel.put(currency.value, currency);
        }
    }

    ProductCurrencyEnum(String value) {
        this.value = value;
    }

    public static ProductCurrencyEnum getProductByValue(String key) {
        if (mapByLabel.containsKey(key))
            return mapByLabel.get(key);
        return null;// TODO enum exception
    }
}
