package ro.adi.comparatorprices.product.controller;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.List;

@UtilityClass
public class ProductConverter {

    public PageImpl convertResultToPageable(ProductRequestDto requestDto, List<ProductResponseDto> results) {
        var dtoPageable = requestDto.getPageable();
        var size = dtoPageable.getSize();
        var page = dtoPageable.getPage();
        var pageable = PageRequest.of(page, size);
        int endIndex = Math.min(results.size(), dtoPageable.getSize());
        var subList = results.subList(0, endIndex);
        return new PageImpl(subList, pageable, endIndex);
    }
}
