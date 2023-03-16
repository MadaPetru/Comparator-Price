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
public class Publi24Helper implements SiteHelper {

    private final String baseUrlPubli24 = SitesUrls.getUrlByEnum(SitesUrls.PUBLI24);

    @Override
    public String getUrlsWithPage(String mainUrl, int page) {

        return mainUrl + "&pag=" + page;
    }

    @Override
    public Set<ProductResponseDto> getProducts(List<String> results) {
        Set<ProductResponseDto> productResponses = new HashSet<>();
        results.forEach(result -> productResponses.addAll(getProductsFromResult(result)));
        return productResponses;
    }

    @Override
    public String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url) {
        return updateUrlPubli24(requestDto, url);
    }

    public String updateUrlPubli24(ProductRequestDto requestDto, String url) {

        var county = requestDto.getCounty();
        var name = requestDto.getName();
        return url + county + '/' + "?q=" + name + '/';
    }

    private Set<ProductResponseDto> getProductsFromResult(String result) {

        var documentOfProducts = Jsoup.parse(result);
        var searchAfter = ".html\" class=\"maincolor\"";
        var elementLinks = documentOfProducts.select("div");
        var wantedResultsFromHtmlLinks = elementLinks.stream().map(Node::toString).filter(r -> r.contains(searchAfter)
                && r.contains("price maincolor") && r.startsWith("<div class=\"listing-data\">")).toList();
        return wantedResultsFromHtmlLinks.stream().map(this::getProductsDetailsFromHtmlPageResults)
                .collect(Collectors.toSet());
    }

    private ProductResponseDto getProductsDetailsFromHtmlPageResults(String linkResult) {

        var stringToVerifyExistenceOfPriceLabel = "RON";
        var price = linkResult.contains(stringToVerifyExistenceOfPriceLabel) ? getPriceFromHtmlPageResult(linkResult)
                : -1;
        var link = getWantedLinkToSearchForProduct(linkResult);
        return ProductResponseDto.builder().link(link).price(price).currency(ProductCurrencyEnum.RON)
                .name(getProductName(link)).build();
    }

    private int getPriceFromHtmlPageResult(String linkResult) {

        var searchAfterApparitionPrice = "RON</strong>";
        return SiteUtility
                .getPriceFromHtmlPageResultSearchingAfterApparitionStringFromTheEndOfThePriceWith2CharactersDistance(
                        linkResult, searchAfterApparitionPrice);
    }

    private String getWantedLinkToSearchForProduct(String result) {

        assert baseUrlPubli24 != null;
        var indexBegin = result.indexOf(baseUrlPubli24);
        var index = indexBegin;
        while (result.charAt(index) != '"') {
            index++;
        }
        var indexToDeleteToNotIncludeDoubleQuoteEnding = 1;
        var indexEnd = index - indexToDeleteToNotIncludeDoubleQuoteEnding;
        return result.substring(indexBegin, indexEnd);
    }

    public String getProductName(String link) {

        var index = link.length() - 38 - 1;
        while (link.charAt(index) != '/')
            index--;
        return link.substring(index + 1, link.length() - 38);
    }
}