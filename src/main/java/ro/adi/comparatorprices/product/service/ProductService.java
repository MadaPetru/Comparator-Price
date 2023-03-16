package ro.adi.comparatorprices.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.adi.comparatorprices.product.constants.SitesUrls;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;
import ro.adi.comparatorprices.product.sites.SiteHelperFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplate restTemplate;
    private final SiteHelperFactory siteHelperFactory;
    private EnumMap<SitesUrls, String> mainUrlsToSearchProductsMapBySite = new EnumMap<>(SitesUrls.class);
    private EnumMap<SitesUrls, String> resultsFromUrlsOfTheMainPageMapBySite = new EnumMap<>(SitesUrls.class);
    private EnumMap<SitesUrls, String> urlsMapBySiteFromDesiredPage = new EnumMap<>(SitesUrls.class);
    private EnumMap<SitesUrls, String> resultsFromUrlsWithPageMapBySite = new EnumMap<>(SitesUrls.class);
    private EnumMap<SitesUrls, List<String>> resultsFromUrlsWithPageAndMainUrlsMapBySite = new EnumMap<>(SitesUrls.class);

    public List<ProductResponseDto> getAllProductsWithSpecificCharacteristics(ProductRequestDto requestDto) {

        setResultsFromUrlsBasedOnRequestedPage(requestDto);
        resultsFromUrlsWithPageAndMainUrlsMapBySite = getResultsFromUrlsWithPageAndMainPageMapBySite();
        return getProductsFromResults();
    }

    private void setResultsFromUrlsBasedOnRequestedPage(ProductRequestDto requestDto) {

        var page = requestDto.getPageable().getPage();
        if (page == 0) {
            mainUrlsToSearchProductsMapBySite = generateAllUrlsSearchProducts(requestDto);
            resultsFromUrlsOfTheMainPageMapBySite = getResultsFromUrlsOfTheMainPageOfSites();
            return;
        }
        urlsMapBySiteFromDesiredPage = getUrlsMapBySiteWithPageFromRequest(page);
        resultsFromUrlsWithPageMapBySite = getResultsFromUrlsWithPage();
    }

    private EnumMap<SitesUrls, List<String>> getResultsFromUrlsWithPageAndMainPageMapBySite() {

        var results = new EnumMap<SitesUrls, List<String>>(SitesUrls.class);
        for (var entry : resultsFromUrlsOfTheMainPageMapBySite.entrySet()) {
            results.put(entry.getKey(),
                    Collections.singletonList(entry.getValue()));
        }
        for (var entry : resultsFromUrlsWithPageMapBySite.entrySet()) {
            results.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }

        return results;
    }

    private EnumMap<SitesUrls, String> getResultsFromUrlsWithPage() {

        var results = new EnumMap<SitesUrls, String>(SitesUrls.class);
        for (var urlWithPageMapBySite : urlsMapBySiteFromDesiredPage.entrySet()) {
            var site = urlWithPageMapBySite.getKey();
            var url = urlWithPageMapBySite.getValue();
            var result = restTemplate.getForObject(url, String.class);
            results.put(site, result);
        }
        return results;
    }

    private EnumMap<SitesUrls, String> getResultsFromUrlsOfTheMainPageOfSites() {

        var resultsFromUrlsOfTheMainPageMapSite = new EnumMap<SitesUrls, String>(SitesUrls.class);
        for (var urlSiteMapBySite : mainUrlsToSearchProductsMapBySite.entrySet()) {
            var key = urlSiteMapBySite.getKey();
            var urlOfTheMainPage = urlSiteMapBySite.getValue();
            resultsFromUrlsOfTheMainPageMapSite.put(key, restTemplate.getForObject(urlOfTheMainPage, String.class));
        }
        return resultsFromUrlsOfTheMainPageMapSite;
    }

    private List<ProductResponseDto> getProductsFromResults() {

        List<ProductResponseDto> allProducts = new ArrayList<>();
        for (var resultMapBySite : resultsFromUrlsWithPageAndMainUrlsMapBySite.entrySet()) {
            var site = resultMapBySite.getKey();
            var results = resultMapBySite.getValue();
            allProducts.addAll(siteHelperFactory.createCorrespondentSiteHelper(site).getProducts(results));
        }
        return allProducts;
    }

    private EnumMap<SitesUrls, String> generateAllUrlsSearchProducts(ProductRequestDto requestDto) {

        var siteEnums = SitesUrls.values();
        var urlsToSearchProductsByHisSite = new EnumMap<SitesUrls, String>(SitesUrls.class);
        for (var site : siteEnums) {
            var urlSite = SitesUrls.getUrlByEnum(site);
            var urlCreatedBasedOnRequest = getUrlCreatedBasedOnRequest(requestDto, urlSite, site);
            urlsToSearchProductsByHisSite.put(site, urlCreatedBasedOnRequest);
        }
        return urlsToSearchProductsByHisSite;
    }

    private String getUrlCreatedBasedOnRequest(ProductRequestDto requestDto, String url, SitesUrls site) {
        return siteHelperFactory.createCorrespondentSiteHelper(site).getUrlCreatedBasedOnRequest(requestDto, url);
    }

    private EnumMap<SitesUrls, String> getUrlsMapBySiteWithPageFromRequest(int page) {

        var urls = new EnumMap<SitesUrls, String>(SitesUrls.class);
        for (var mainUrlMapBySite : mainUrlsToSearchProductsMapBySite.entrySet()) {

            var mainUrl = mainUrlMapBySite.getValue();
            var site = mainUrlMapBySite.getKey();
            var urlsWithPage = getUrlsWithDesiredPageFromRequest(mainUrl, site, page);
            urls.put(site, urlsWithPage);
        }

        return urls;
    }

    private String getUrlsWithDesiredPageFromRequest(String mainUrl, SitesUrls site, int page) {

        return siteHelperFactory.createCorrespondentSiteHelper(site).getUrlsWithPage(mainUrl, page);
    }
}