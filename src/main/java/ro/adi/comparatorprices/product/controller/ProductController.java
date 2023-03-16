package ro.adi.comparatorprices.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;
import ro.adi.comparatorprices.product.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public PageImpl<ProductResponseDto> getAllProductsWithSpecificCharacteristics(@RequestBody ProductRequestDto requestDto) {
        var results = productService.getAllProductsWithSpecificCharacteristics(requestDto);
        return ProductConverter.convertResultToPageable(requestDto, results);
    }
}
