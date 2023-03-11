package ro.adi.comparatorprices.product.sites;

import org.springframework.stereotype.Component;
import ro.adi.comparatorprices.product.constants.ProductCurrencyEnum;
import ro.adi.comparatorprices.product.constants.SitesUrls;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OlxHelper implements SiteHelper {

    @Override
    public ArrayList<String> getUrlsWithPage(int totalPages, String mainUrl) {

        var urlsWithPage = new ArrayList<String>();
        for (int j = 2; j <= totalPages; j++) {
            urlsWithPage.add(mainUrl + "?page=" + j);
        }
        return urlsWithPage;
    }

    @Override
    public Set<String> getProductsLinks(List<String> results) {
        return updateOlxResponses(results);
    }

    @Override
    public ProductResponseDto getProductDto(String link, String result) {

        var name = getProductName(link);
        var price = getProductPrice(result);
        var currency = getProductCurrency(result);
        return ProductResponseDto.builder().currency(currency).name(name).link(link).price(price).build();
    }

    @Override
    public String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url) {
        return getUrlOlxBasedOnRequest(requestDto, url);
    }

    @Override
    public int getTotalPages(String result) {
        return getTotalPagesResultMainUrl(result);
    }

    public String getUrlOlxBasedOnRequest(ProductRequestDto requestDto, String url) {

        var county = requestDto.getCounty();
        var name = requestDto.getName();
        return url + county + '-' + "judet" + '/' + 'q' + '-' + name + '/';
    }

    public Set<String> updateOlxResponses(List<String> results) {

        Set<String> response = new HashSet<>();
        var searchAfter = "olx.ro";
        results.forEach(result -> {
            if (result.contains(searchAfter))
                olxUpdateResponse(result, response);
        });
        return response;
    }

    public String getProductName(String result) {
        return result.substring(28, result.length() - 13);
    }

    public Integer getProductPrice(String result) {

        var searchAfter = "€";
        var index = result.indexOf(searchAfter);
        var searchAfterLei = "lei:";
        var indexLei = result.indexOf(searchAfterLei);
        if (index > indexLei && indexLei != -1)
            index = indexLei;
        index = index - 2;
        var sol = 0;
        var z = 1;
        while (result.charAt(index) >= '0' && result.charAt(index) <= '9') {
            sol = (Integer.parseInt(String.valueOf(result.charAt(index)))) * z + sol;
            index--;
            z = z * 10;
        }
        return sol;
    }

    public ProductCurrencyEnum getProductCurrency(String result) {

        var euro = "€:";
        int indexEuro = result.indexOf(euro);
        var lei = "lei:";
        var indexLei = result.indexOf(lei);
        if (indexEuro < indexLei && indexEuro != -1)
            return ProductCurrencyEnum.EURO;
        return ProductCurrencyEnum.RON;
    }

    private void olxUpdateResponse(String result, Set<String> response) {

        var ok = true;
        var searchAfter = "/d/oferta/";
        var lengthSearchAfter = searchAfter.length();
        var indexSearchBeginning = 0;
        while (ok) {
            var indexResultEnd = result.length() - 1;
            var searchAfterIndex = result.indexOf(searchAfter);
            if (searchAfterIndex == -1) {
                ok = false;
                continue;
            }
            var indexBegin = searchAfterIndex + lengthSearchAfter - 1;
            var index = indexBegin;
            while (result.charAt(index) != '>') {
                index++;
            }
            var indexEnd = index;
            indexSearchBeginning = indexEnd + 1;
            var baseUrlOlx = SitesUrls.getUrlByEnum(SitesUrls.OLX);
            assert baseUrlOlx != null;// TODO handle this situation if it ever possible
            var auxResponse = baseUrlOlx.substring(0, baseUrlOlx.length() - 3) + searchAfter.substring(0, 9)
                    + result.substring(indexBegin, indexEnd - 1);
            response.add(auxResponse);
            result = result.substring(indexSearchBeginning, indexResultEnd);
        }
    }

    public int getTotalPagesResultMainUrl(String result) {
        var searchAfter = "totalPages";
        var indexBeginning = result.indexOf(searchAfter) + 13;
        var sol = 0;
        while (result.charAt(indexBeginning) >= '0' && result.charAt(indexBeginning) <= '9') {
            sol = sol * 10 + Integer.parseInt(String.valueOf(result.charAt(indexBeginning)));
            indexBeginning++;
        }
        return sol;
    }
}