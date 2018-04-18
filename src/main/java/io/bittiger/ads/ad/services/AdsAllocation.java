package io.bittiger.ads.ad.services;

import io.bittiger.ads.ad.entities.Ad;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdsAllocation {
	private static double mainLinePriceThreshold = 4.5;

	public void AllocateAds(List<Ad> ads)
	{
		for(Ad ad : ads)
		{
			if(ad.costPerClick >= mainLinePriceThreshold)
			{
				ad.position = 1;
			}
			else
			{
				ad.position = 2;
			}
		}
	}
}
