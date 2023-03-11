package ro.adi.comparatorprices.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.adi.comparatorprices.collections.PairLinkProduct;
import ro.adi.comparatorprices.product.constants.SitesUrls;
import ro.adi.comparatorprices.product.dto.request.ProductRequestDto;
import ro.adi.comparatorprices.product.dto.response.ProductResponseDto;
import ro.adi.comparatorprices.product.sites.SiteHelperFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestTemplate restTemplate;
    private final SiteHelperFactory siteHelperFactory;
    private Map<SitesUrls, String> mainUrlsToSearchProductsMapBySite;
    private Map<SitesUrls, String> resultsFromUrlsOfTheMainPageMapBySite;
    private HashMap<SitesUrls, ArrayList<String>> urlsWithPageMapBySite;
    private HashMap<SitesUrls, List<String>> resultsFromUrlsWithPageMapBySite;
    private HashMap<SitesUrls, List<String>> resultsFromUrlsWithPageAndMainUrlsMapBySite;

    public List<ProductResponseDto> getAllProductsWithSpecificCharacteristics(ProductRequestDto requestDto)
            throws Exception {

        mainUrlsToSearchProductsMapBySite = generateAllUrlsSearchProducts(requestDto);
        try {
            resultsFromUrlsOfTheMainPageMapBySite = getResultsFromUrlsOfTheMainPageOfSites();
            urlsWithPageMapBySite = getUrlsWithPageMapBySite();
            resultsFromUrlsWithPageMapBySite = getResultsFromUrlsWithPage();
            resultsFromUrlsWithPageAndMainUrlsMapBySite = getResultsFromUrlsWithPageAndMainPageMapBySite();
            return getProductsFromResults();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("");
        }
    }

    private HashMap<SitesUrls, List<String>> getResultsFromUrlsWithPageAndMainPageMapBySite() {

        var resultsFromUrlsWithPageAndMainUrlsMapBySite = new HashMap<SitesUrls, List<String>>();
        for (var entry : resultsFromUrlsOfTheMainPageMapBySite.entrySet()) {
            resultsFromUrlsWithPageAndMainUrlsMapBySite.put(entry.getKey(),
                    Collections.singletonList(entry.getValue()));
        }
        for (var entry : resultsFromUrlsWithPageMapBySite.entrySet()) {
            resultsFromUrlsWithPageAndMainUrlsMapBySite.put(entry.getKey(), entry.getValue());
        }

        return resultsFromUrlsWithPageAndMainUrlsMapBySite;
    }

    private HashMap<SitesUrls, List<String>> getResultsFromUrlsWithPage() {

        var resultsFromUrlsWithPageMapBySite = new HashMap<SitesUrls, List<String>>();
        for (var urlWithPageMapBySite : urlsWithPageMapBySite.entrySet()) {
            var site = urlWithPageMapBySite.getKey();
            var urlsWithPage = urlWithPageMapBySite.getValue();
            var result = urlsWithPage.stream().map(url -> restTemplate.getForObject(url, String.class))
                    .collect(Collectors.toList());
            resultsFromUrlsWithPageMapBySite.put(site, result);
        }

        return resultsFromUrlsWithPageMapBySite;
    }

    private Map<SitesUrls, String> getResultsFromUrlsOfTheMainPageOfSites() {

        Map<SitesUrls, String> resultsFromUrlsOfTheMainPageMapSite = new HashMap<>();
        for (var urlSiteMapBySite : mainUrlsToSearchProductsMapBySite.entrySet()) {
            var key = urlSiteMapBySite.getKey();
            var urlOfTheMainPage = urlSiteMapBySite.getValue();
            resultsFromUrlsOfTheMainPageMapSite.put(key, restTemplate.getForObject(urlOfTheMainPage, String.class));
        }
        return resultsFromUrlsOfTheMainPageMapSite;
    }

    private List<ProductResponseDto> getProductsFromResults() {

        List<ProductResponseDto> allProducts = new ArrayList<>();
        var linksMapBySite = getProductsLinksMapBySite();
        for (var linksMapBySiteEntry : linksMapBySite.entrySet()) {
            var links = linksMapBySiteEntry.getValue();
            var site = linksMapBySiteEntry.getKey();
            var products = getProductsFromSite(links, site);
            allProducts.addAll(products);
        }
        return allProducts;
    }

    private List<ProductResponseDto> getProductsFromSite(Set<String> links, SitesUrls site) {

        return links.stream().map(link -> new PairLinkProduct(link, restTemplate.getForObject(link, String.class)))
                .map(linkResultPairLinkProduct -> siteHelperFactory.createCorrespondentSiteHelper(site)
                        .getProductDto(linkResultPairLinkProduct.getLink(), linkResultPairLinkProduct.getResult()))
                .collect(Collectors.toList());
    }

    private HashMap<SitesUrls, String> generateAllUrlsSearchProducts(ProductRequestDto requestDto) {
        var siteEnums = SitesUrls.values();
        var urlsToSearchProductsByHisSite = new HashMap<SitesUrls, String>();
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

    private HashMap<SitesUrls, Set<String>> getProductsLinksMapBySite() {

        var linksMapBySite = new HashMap<SitesUrls, Set<String>>();
        for (var resultsMapBySite : resultsFromUrlsWithPageAndMainUrlsMapBySite.entrySet()) {
            var site = resultsMapBySite.getKey();
            var results = resultsMapBySite.getValue();
            var links = siteHelperFactory.createCorrespondentSiteHelper(site).getProductsLinks(results);
            linksMapBySite.put(site, links);
        }

        return linksMapBySite;
    }

    private HashMap<SitesUrls, ArrayList<String>> getUrlsWithPageMapBySite() {

        var urlsWithPageMapBySite = new HashMap<SitesUrls, ArrayList<String>>();
        for (var mainUrlMapBySite : mainUrlsToSearchProductsMapBySite.entrySet()) {

            var mainUrl = mainUrlMapBySite.getValue();
            var site = mainUrlMapBySite.getKey();
            var urlsWithPage = getUrlsWithPages(mainUrl, site);
            urlsWithPageMapBySite.put(site, urlsWithPage);
        }

        return urlsWithPageMapBySite;
    }

    private ArrayList<String> getUrlsWithPages(String mainUrl, SitesUrls site) {

        var resultsOfMainPageAfterSite = resultsFromUrlsOfTheMainPageMapBySite.get(site);
        int totalPages = getTotalPages(resultsOfMainPageAfterSite, site);
        return siteHelperFactory.createCorrespondentSiteHelper(site).getUrlsWithPage(totalPages, mainUrl);
    }

    private int getTotalPages(String result, SitesUrls urlEnum) {
        return siteHelperFactory.createCorrespondentSiteHelper(urlEnum).getTotalPages(result);
    }
}