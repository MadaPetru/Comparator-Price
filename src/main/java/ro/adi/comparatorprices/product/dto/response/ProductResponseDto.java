package ro.adi.comparatorprices.product.dto.response;

import lombok.*;
import ro.adi.comparatorprices.product.constants.ProductCurrencyEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private String link;
    private String name;
    private Integer price;
    private ProductCurrencyEnum currency;
}
