package ro.adi.comparatorprices.product.sites;

import org.springframework.stereotype.Component;
import ro.adi.comparatorprices.product.constants.ProductCurrencyEnum;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Publi24Helper implements SiteHelper {

    @Override
    public ArrayList<String> getUrlsWithPage(int totalPages, String mainUrl) {

        var urlsWithPage = new ArrayList<String>();
        for (int j = 2; j <= totalPages; j++) {
            urlsWithPage.add(mainUrl + "&pag=" + j);
        }
        return urlsWithPage;
    }

    @Override
    public Set<String> getProductsLinks(List<String> results) {
        return getLinksFromResults(results);
    }

    @Override
    public ProductResponseDto getProductDto(String link, String result) {

        var name = getProductName(link);
        var price = getProductPrice(result);
        var currency = getProductCurrency(result);
        return ProductResponseDto.builder().name(name).currency(currency).price(price).link(link).build();
    }

    @Override
    public String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url) {
        return updateUrlPubli24(requestDto, url);
    }

    @Override
    public int getTotalPages(String result) {
        return getTotalPagesResultMainUrl(result);
    }

    public String updateUrlPubli24(ProductRequestDto requestDto, String url) {

        var county = requestDto.getCounty();
        var name = requestDto.getName();
        return url + county + '/' + "?q=" + name + '/';
    }

    public int getTotalPagesResultMainUrl(String result) {

        String searchAfter = " name=\"search-result-total-items\" content=\"";
        int length = searchAfter.length();
        int index = result.indexOf(searchAfter) + length;
        int sol = 0;
        while (result.charAt(index) >= '0' && result.charAt(index) <= '9') {
            sol = sol * 10 + (result.charAt(index) - '0');
            index++;
        }
        int nrPages = sol / 20;// TODO implement to change the page dynamically
        if (sol % 20 != 0)
            nrPages++;
        return nrPages;
    }

    public Set<String> getLinksFromResults(List<String> results) {

        Set<String> responses = new HashSet<>();
        var searchAfter = "publi24.ro";
        results.forEach(result -> {
            if (result.contains(searchAfter))
                updatePubli24Response(result, responses);
        });
        return responses;
    }

    private void updatePubli24Response(String result, Set<String> responses) {

        var searchAfter = ".html\" class=\"maincolor\"";
        var length = searchAfter.length();
        var ok = true;
        var indexStart = 0;
        while (ok) {
            var index = result.indexOf(searchAfter);
            if (index == -1) {
                ok = false;
                continue;
            }
            indexStart = index + length + 1;
            String link = "";
            while (result.charAt(index) != '"') {
                link = "" + result.charAt(index) + link;
                index--;
            }
            String sol = link + "html";
            responses.add(sol);
            var endIndex = result.length();
            result = result.substring(indexStart, endIndex);
        }
    }

    public String getProductName(String link) {

        int index = link.length() - 38 - 1;
        while (link.charAt(index) != '/')
            index--;
        return link.substring(index + 1, link.length() - 38);
    }

    public Integer getProductPrice(String result) {

        var searchAfter = "itemprop=\"price\" content=\"";
        var index = result.indexOf(searchAfter) + searchAfter.length();
        if (!result.contains(searchAfter))
            return 0;
        var sol = 0;
        while (result.charAt(index) >= '0' && result.charAt(index) <= '9') {
            sol = sol * 10 + (result.charAt(index) - '0');
            index++;
        }
        return sol;
    }

    public ProductCurrencyEnum getProductCurrency(String result) {

        String searchAfter = "itemprop=\"priceCurrency\" content=\"";
        int index = result.indexOf(searchAfter) + searchAfter.length();
        String currency = "" + result.charAt(index) + result.charAt(index + 1) + result.charAt(index + 2);
        if (currency.contains("EUR"))
            return ProductCurrencyEnum.EURO;
        return ProductCurrencyEnum.RON;
    }
}