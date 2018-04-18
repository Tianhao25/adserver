package io.bittiger.ads.ad.services;

import io.bittiger.ads.indexbuilder.IndexBuilder;
import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.entities.Campaign;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdsEngine {
	private final AdsCampaignManager campaignManager;
	private final QueryParser queryParser;
	private final AdsFilter adsFilter;
	private final AdsAllocation adsAllocation;
	private final AdsSelector adsSelector;

	@Autowired
	public AdsEngine(AdsCampaignManager campaignManager, QueryParser queryParser, AdsFilter adsFilter, AdsAllocation adsAllocation, AdsSelector adsSelector) {
		this.campaignManager = campaignManager;
		this.queryParser = queryParser;
		this.adsFilter = adsFilter;
		this.adsAllocation = adsAllocation;
		this.adsSelector = adsSelector;
	}

	public List<Ad> selectAds(String query)
	{
		//query understanding
		List<String> queryTerms = queryParser.QueryUnderstand(query);
		//select ads candidates
		List<Ad> adsCandidates = adsSelector.selectAds(queryTerms);
		//L0 filter by pClick, relevance score
		List<Ad> L0unfilteredAds = adsFilter.LevelZeroFilterAds(adsCandidates);
		System.out.println("L0unfilteredAds ads left = " + L0unfilteredAds.size());
		
		// TODO: sort by relevance sore (ranker)
		// Place holder.

		//L1 filter by relevance score : select top K ads
		int k = 20;
		List<Ad> unfilteredAds = adsFilter.LevelOneFilterAds(L0unfilteredAds,k);
		System.out.println("unfilteredAds ads left = " + unfilteredAds.size());

		//Dedupe ads per campaign, only one ad is allowed for one campaign.
		List<Ad> dedupedAds = campaignManager.DedupeByCampaignId(unfilteredAds);
		System.out.println("dedupedAds ads left = " + dedupedAds.size());

		//Apply expense on the campaing's budget for ad showing.
		List<Ad> ads = campaignManager.ApplyBudget(dedupedAds);
		System.out.println("AdsCampaignManager ads left = " + ads.size());

		//allocate ads.
		// If PClick is over a price bar, put to a better location.
		// else just put to a less obvious position.
		adsAllocation.AllocateAds(ads);
		return ads;
	}
}
