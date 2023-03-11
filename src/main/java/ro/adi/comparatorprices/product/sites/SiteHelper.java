package ro.adi.comparatorprices.product.sites;

import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface SiteHelper {

    ArrayList<String> getUrlsWithPage(int totalPages, String mainUrl);

    Set<String> getProductsLinks(List<String> results);

    ProductResponseDto getProductDto(String link, String result);

    String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url);

    int getTotalPages(String result);
}