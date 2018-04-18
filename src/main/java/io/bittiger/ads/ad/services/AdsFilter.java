package io.bittiger.ads.ad.services;

import io.bittiger.ads.ad.entities.Ad;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdsFilter {
	private static double pClickThreshold = 0.0;
	private static double relevanceScoreThreshold = 0.01;
	private static int mimNumOfAds = 4;

	/**
	 * Level 0 filter to filter out ads with not enough Pclick and relevance score.
	 * @param adsCandidates list of possible ads.
	 * @return ads passed the filter.
	 */
	public List<Ad> LevelZeroFilterAds(List<Ad> adsCandidates)
	{
		// If not enough ads, just return all ads found.
		if(adsCandidates.size() <= mimNumOfAds)
			return adsCandidates;
		
		List<Ad> unfilteredAds = new ArrayList<Ad>();
		for(Ad ad : adsCandidates)
		{
			// If Price per click is higher than Threshold and relevence Score is large enough,
			// Add to list.
			if(ad.pClick >= pClickThreshold &&
					ad.relevanceScore > relevanceScoreThreshold)
			{
				unfilteredAds.add(ad);
			}
		}
		return unfilteredAds;
	}

	/**
	 * Level 1 filter to select top K ads.
	 * @param adsCandidates sorted ads candidates.
	 * @param k num of ads needed.
	 * @return ads passed filter.
	 */
	public List<Ad> LevelOneFilterAds(List<Ad> adsCandidates,int k)
	{
		if(adsCandidates.size() <= mimNumOfAds)
			return adsCandidates;
		
		List<Ad> unfilteredAds = new ArrayList<Ad>();
		for(int i = 0; i < Math.min(k, adsCandidates.size());i++)
		{
			unfilteredAds.add(adsCandidates.get(i));
		}
		return unfilteredAds;
	}
}
