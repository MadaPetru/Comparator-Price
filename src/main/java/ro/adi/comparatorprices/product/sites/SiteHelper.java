package ro.adi.comparatorprices.product.sites;

import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.List;
import java.util.Set;

public interface SiteHelper {

    String getUrlsWithPage(String mainUrl, int page);

    Set<ProductResponseDto> getProducts(List<String> results);

    String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url);
}