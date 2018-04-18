package io.bittiger.ads.ad.services;

import io.bittiger.ads.ad.entities.Ad;
import io.bittiger.ads.ad.entities.Campaign;
import io.bittiger.ads.ad.repos.CampaignRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class AdsCampaignManager {
	private final CampaignRepo campaignRepo;

	private static double minPriceThreshold = 0.0;

    @Autowired
    public AdsCampaignManager(CampaignRepo campaignRepo) {
        this.campaignRepo = campaignRepo;
    }

    public  List<Ad> DedupeByCampaignId(List<Ad> adsCandidates)
	{
		List<Ad> dedupedAds = new ArrayList<Ad>();
		HashSet<Long> campaignIdSet = new HashSet<Long>();
		for(Ad ad : adsCandidates)
		{
			if(!campaignIdSet.contains(ad.campaignId))
			{
				dedupedAds.add(ad);
				campaignIdSet.add(ad.campaignId);
			}
		}
		return dedupedAds;
	}
	public List<Ad> ApplyBudget(List<Ad> adsCandidates)
	{
		List<Ad> ads = new ArrayList<Ad>();
		try
		{
			for(int i = 0; i < adsCandidates.size()  - 1;i++)
			{
				Ad ad = adsCandidates.get(i);
				Long campaignId = ad.campaignId;
				System.out.println("campaignId: " + campaignId);
				Double budget = campaignRepo.findById(campaignId).get().budget;
				System.out.println("AdsCampaignManager ad.costPerClick= " + ad.costPerClick);
				System.out.println("AdsCampaignManager campaignId= " + campaignId);
				System.out.println("AdsCampaignManager budget left = " + budget);

				if(ad.costPerClick <= budget && ad.costPerClick >= minPriceThreshold)
				{
					ads.add(ad);
					budget = budget - ad.costPerClick;
					Campaign updatedCampaign = new Campaign();
					updatedCampaign.campaignId = campaignId;
					updatedCampaign.budget = budget;
					campaignRepo.save(updatedCampaign);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ads;
	}
	
}
