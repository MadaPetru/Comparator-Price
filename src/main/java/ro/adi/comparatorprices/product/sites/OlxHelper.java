package ro.adi.comparatorprices.product.sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Component;
import ro.adi.comparatorprices.product.constants.ProductCurrencyEnum;
import ro.adi.comparatorprices.product.constants.SitesUrls;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OlxHelper implements SiteHelper {

    private final String baseUrlOlx = SitesUrls.getUrlByEnum(SitesUrls.OLX);

    @Override
    public String getUrlsWithPage(String mainUrl, int page) {

        return mainUrl + "?page=" + page;
    }

    @Override
    public Set<ProductResponseDto> getProducts(List<String> results) {
        Set<ProductResponseDto> productResponses = new HashSet<>();
        results.forEach(result -> productResponses.addAll(getProductsFromResult(result)));
        return productResponses;
    }

    @Override
    public String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url) {
        return getUrlOlxBasedOnRequest(requestDto, url);
    }

    public String getUrlOlxBasedOnRequest(ProductRequestDto requestDto, String url) {

        var county = requestDto.getCounty();
        var name = requestDto.getName();
        return url + county + '-' + "judet" + '/' + 'q' + '-' + name + '/';
    }

    public String getProductName(String result) {
        return result.substring(28, result.length() - 13);
    }

    private Set<ProductResponseDto> getProductsFromResult(String result) {

        var documentOfProducts = Jsoup.parse(result);
        var searchAfter = "/d/oferta/";
        var elementLinks = documentOfProducts.select("a");
        var allLinksWantedAsHtml = elementLinks.stream().map(Node::toString).filter(e -> e.contains(searchAfter))
                .toList();
        return allLinksWantedAsHtml.stream()
                .map(linkResult -> getProductsDetailsFromHtmlPageResults(searchAfter, linkResult))
                .collect(Collectors.toSet());
    }

    private ProductResponseDto getProductsDetailsFromHtmlPageResults(String searchAfter, String linkResult) {

        var stringToVerifyExistenceOfPriceLabel = "lei";
        var price = linkResult.contains(stringToVerifyExistenceOfPriceLabel) ? getPriceFromHtmlPageResult(linkResult)
                : -1;
        var link = getWantedLinkToSearchForProduct(searchAfter, linkResult);
        return ProductResponseDto.builder().link(link).price(price).currency(ProductCurrencyEnum.RON)
                .name(getProductName(link)).build();
    }

    private int getPriceFromHtmlPageResult(String linkResult) {

        var searchAfterApparitionPrice = "lei";
        return SiteUtility
                .getPriceFromHtmlPageResultSearchingAfterApparitionStringFromTheEndOfThePriceWith2CharactersDistance(
                        linkResult, searchAfterApparitionPrice);
    }

    private String getWantedLinkToSearchForProduct(String searchAfter, String link) {

        var indexBegin = link.indexOf(searchAfter);
        var index = indexBegin;
        while (link.charAt(index) != '>') {
            index++;
        }
        var indexToDeleteToNotIncludeDoubleQuoteEnding = 1;
        var indexEnd = index - indexToDeleteToNotIncludeDoubleQuoteEnding;
        assert baseUrlOlx != null;
        return baseUrlOlx.substring(0, baseUrlOlx.length() - 3) + link.substring(indexBegin, indexEnd);
    }
}