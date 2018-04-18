package io.bittiger.ads;

import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.entities.Campaign;
import io.bittiger.ads.ad.repos.CampaignRepo;
import io.bittiger.ads.indexbuilder.IndexBuilder;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by ross.wang on 4/17/18 for project ad-system.
 */
@SpringBootApplication
@Slf4j
public class AdApp {
    @Value("${memcached.host:localhost}")
    private String memcachedHost;
    @Value("${memcached.port:11211}")
    private Integer memcachedPort;

    @Autowired
    private IndexBuilder indexBuilder;
    @Autowired
    private CampaignRepo campaignRepo;

    public static void main(String... args) {
        SpringApplication.run(AdApp.class, args);
    }

    @Bean
    public MemcachedClient memcachedClient() throws Exception {
        log.info("=======================================");
        log.info("======= MEMCACHED CLIENT INIT.. =======");
        log.info("=======================================");
        try {
            MemcachedClient memcachedClient =
                    new MemcachedClient(new InetSocketAddress("localhost", 11211));
            return memcachedClient;
        } catch (Exception e) {
            log.error("ERROR WHEN CREATING MEMCACHED CLIENT");
            e.printStackTrace();
            System.exit(0);
        }

        return null;
    }

    /**
     * Load Ads and Build Index.
     * @return
     */
    @Bean
    CommandLineRunner runner() {
        log.info("=======================================");
        log.info("============= INDEXING........  =======");
        log.info("=======================================");

        return args -> {
            InputStream adStream = this.getClass().getResourceAsStream("/metadata/ads_0502.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(adStream));

            String line;
            int lineCount = 0;
			while ((line = reader.readLine()) != null) {
                JSONObject adJson = new JSONObject(line);
                Ad ad = new Ad();
                if (adJson.isNull("adId") || adJson.isNull("campaignId")) {
                    continue;
                }
                ad.adId = adJson.getLong("adId");
                ad.campaignId = adJson.getLong("campaignId");
                ad.brand = adJson.isNull("brand") ? "" : adJson.getString("brand");
                ad.price = adJson.isNull("price") ? 100.0 : adJson.getDouble("price");
                ad.thumbnail = adJson.isNull("thumbnail") ? "" : adJson.getString("thumbnail");
                ad.title = adJson.isNull("title") ? "" : adJson.getString("title");
                ad.detail_url = adJson.isNull("detail_url") ? "" : adJson.getString("detail_url");
                ad.bidPrice = adJson.isNull("bidPrice") ? 1.0 : adJson.getDouble("bidPrice");
                ad.pClick = adJson.isNull("pClick") ? 0.0 : adJson.getDouble("pClick");
                ad.category = adJson.isNull("category") ? "" : adJson.getString("category");
                ad.description = adJson.isNull("description") ? "" : adJson.getString("description");
                ad.keyWords = new ArrayList<String>();
                JSONArray keyWords = adJson.isNull("keyWords") ? null : adJson.getJSONArray("keyWords");
                for (int j = 0; j < keyWords.length(); j++) {
                    ad.keyWords.add(keyWords.getString(j));
                }

                indexBuilder.buildForwardIndex(ad);
                indexBuilder.buildInvertIndex(ad);

                lineCount++;
                if (lineCount % 100 == 0) {
                    log.info("INDEXING ADs..... " + lineCount);
                }
            }

            log.info("=======================================");
            log.info("=========== INDEX FINISHED" + lineCount +" =======");
            log.info("=======================================");

            InputStream campaignStream = this.getClass().getResourceAsStream("/metadata/campaign.txt");
            BufferedReader cReader = new BufferedReader(new InputStreamReader(campaignStream));

            lineCount = 0;
            while ((line = cReader.readLine()) != null) {
                JSONObject campaignJson = new JSONObject(line);
                Campaign campaign = new Campaign();
                campaign.campaignId = campaignJson.getLong("campaignId");
                campaign.budget = campaignJson.getDouble("budget");
                campaignRepo.save(campaign);

                lineCount++;
                if (lineCount % 20 == 0) {
                    log.info("LOADING Campaigns..... " + lineCount);
                }
            }

            log.info("=======================================");
            log.info("====== CAMPAIGN LOADED " + lineCount +" =============");
            log.info("=======================================");

            log.info("=======================================");
            log.info("==========  SERVER STARTED  ===========");
            log.info("=======================================");
        };
    }
}
