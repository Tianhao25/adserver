package io.bittiger.ads.ad.services;


import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.repos.AdRepo;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class AdsSelector {
	private final MemcachedClient cache;
	private final AdRepo adRepo;

	@Autowired
	public AdsSelector(MemcachedClient cache, AdRepo adRepo) {
		this.cache = cache;
		this.adRepo = adRepo;
	}

	/**
	 * Select ADs with input Query Terms.
	 * @param queryTerms
	 * @return
	 */
	public List<Ad> selectAds(List<String> queryTerms)
	{
		List<Ad> adList = new ArrayList<Ad>();
		int numOfTerms = queryTerms.size();

		// Key : AD id.
		// Value: Count for how many times this ad has been found.
		HashMap<Long,Integer> matchedAds = new HashMap<Long,Integer>();
		try {
			//get ad id from inverted index
			for(String queryTerm : queryTerms)
			{
				System.out.println("selectAds queryTerm = " + queryTerm);
				@SuppressWarnings("unchecked")
				Set<Long>  adIdList = (Set<Long>)cache.get(queryTerm);
				if(adIdList != null && adIdList.size() > 0)
				{
					for(Object adId : adIdList)
					{
						Long key = (Long)adId;
						if(matchedAds.containsKey(key))
						{
							int count = matchedAds.get(key) + 1;
							matchedAds.put(key, count);
						}
						else
						{
							matchedAds.put(key, 1);
						}
					}
				}				
			}
			
			//get ad detail from forward index and calculate relevance score
			for(Long adId: matchedAds.keySet())
			{			
				// Filter out ads that does not match for all terms.
				if (numOfTerms != matchedAds.get(adId)) {
					continue;
				}

				Ad ad = adRepo.findById(adId).get();
				//number of word match query / total number of words in key words
                ad.relevanceScore = matchedAds.get(adId) * 1.0 / ad.keyWords.size();
				// Add to result.
				adList.add(ad);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return adList;
	}
}
