package io.bittiger.ads.ad.controllers;

import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.services.AdsEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by ross.wang on 4/17/18 for project ad-system.
 */
@RestController
@Slf4j
public class SearchAdsController {
    private final AdsEngine adsEngine;
    private String uiTemplate;
    private String adTemplate;

    @Autowired
    public SearchAdsController(AdsEngine adsEngine) {
        this.adsEngine = adsEngine;
    }

    @PostConstruct
    public void init() throws Exception {
        log.info("Initiating Ads Controller....");
        InputStream uiStream = this.getClass().getResourceAsStream("/temp/ui.html");
        uiTemplate = IOUtils.toString(uiStream, StandardCharsets.UTF_8.name());
        InputStream adStream = this.getClass().getResourceAsStream("/temp/item.html");
        adTemplate = IOUtils.toString(adStream, StandardCharsets.UTF_8.name());
    }

    @GetMapping(value = "/SearchAds")
    public String searchAds(@RequestParam("q") String query) {
        List<Ad> adsCandidates = adsEngine.selectAds(query);
        String result = uiTemplate;
        String list = "";
        for(Ad ad : adsCandidates)
        {
            System.out.println("final selected ad id = " + ad.adId);
            System.out.println("final selected ad rank score = " + ad.rankScore);
            String adContent = adTemplate;
            adContent = adContent.replace("$title$", ad.title);
            adContent = adContent.replace("$brand$", ad.brand);
            adContent = adContent.replace("$img$", ad.thumbnail);
            adContent = adContent.replace("$link$", ad.detail_url);
            adContent = adContent.replace("$price$", Double.toString(ad.price));
            //System.out.println("adContent: " + adContent);
            list = list + adContent;
        }
        result = result.replace("$list$", list);
        return result;
    }
}
