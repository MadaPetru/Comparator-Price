package ro.adi.comparatorprices.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;
import ro.adi.comparatorprices.product.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Page<ProductResponseDto> getAllProductsWithSpecificCharacteristics(@RequestBody ProductRequestDto requestDto)
            throws Exception {

        var results = productService.getAllProductsWithSpecificCharacteristics(requestDto);
        return convertResultToPageable(requestDto, results);
    }

    private PageImpl convertResultToPageable(ProductRequestDto requestDto, List<ProductResponseDto> results) {
        var dtoPageable = requestDto.getPageable();
        var size = dtoPageable.getSize();
        var page = dtoPageable.getPage();
        var pageable = PageRequest.of(page, size);
        var startIndex = (page - 1) * size;
        var endIndex = startIndex + pageable.getPageSize();
        var subList = results.subList(startIndex, results.size() > endIndex ? endIndex : results.size());
        return new PageImpl(subList, pageable, results.size());
    }
}
