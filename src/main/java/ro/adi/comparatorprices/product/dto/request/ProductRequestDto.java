package ro.adi.comparatorprices.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    private String name;
    private String county;
    private ProductPageableRequestDto pageable;
}
