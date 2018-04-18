package io.bittiger.ads.ad.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Campaign {
	@Id
	public Long campaignId;
	public double budget;
}
