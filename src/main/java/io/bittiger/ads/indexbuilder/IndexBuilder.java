package io.bittiger.ads.indexbuilder;


import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.entities.Campaign;
import io.bittiger.ads.ad.repos.AdRepo;
import io.bittiger.ads.ad.repos.CampaignRepo;
import io.bittiger.ads.support.Utility;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AD Index builder.
 */
@Service
public class IndexBuilder {
	private int EXP = 0; //Set expiry to 0: which means never expire
	private final MemcachedClient cache;
	private final AdRepo adRepo;
	private final CampaignRepo campaignRepo;

	@Autowired
	public IndexBuilder(MemcachedClient cache, AdRepo adRepo, CampaignRepo campaignRepo) {
		this.cache = cache;
		this.adRepo = adRepo;
		this.campaignRepo = campaignRepo;
	}

	/**
	 * Build Reverted Index with the AD.
	 * @param ad POJO class for AD.
	 * @return succeed or not.
	 */
	public Boolean buildInvertIndex(Ad ad)
	{
		String keyWords = Utility.strJoin(ad.keyWords, ",");
		List<String> tokens = Utility.cleanedTokenize(keyWords);
		for(int i = 0; i < tokens.size();i++)
		{
			String key = tokens.get(i);
			if(cache.get(key) instanceof Set)
			{
				@SuppressWarnings("unchecked")
				Set<Long>  adIdList = (Set<Long>)cache.get(key);
				adIdList.add(ad.adId);
				// Set ad list in cache with an expiration.
			    cache.set(key, EXP, adIdList);
			}
			else
			{
				Set<Long>  adIdList = new HashSet<Long>();
				adIdList.add(ad.adId);
				cache.set(key, EXP, adIdList);
			}
		}
		return true;
	}

	/**
	 * Update DB for new AD.
	 * @param ad POJO class for AD.
	 * @return succeed or not.
	 */
	public Boolean buildForwardIndex(Ad ad)
	{
		try 
		{
			// Add the ad to database if not exist.
			adRepo.save(ad);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}

	/**
	 * Update DB for new Campaign.
	 * @param camp POJO class for CAMPAIGN.
	 * @return succeed or not.
	 */
	public Boolean updateBudget(Campaign camp)
	{
		try 
		{
			campaignRepo.save(camp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}
}
