package ro.adi.comparatorprices.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductPageableRequestDto {
    private int page;
    private int size;
}
