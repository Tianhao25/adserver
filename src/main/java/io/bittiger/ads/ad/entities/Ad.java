package io.bittiger.ads.ad.entities;

import lombok.ToString;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Entity
@ToString
public class Ad implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	public Long adId;
	public Long campaignId;
	@ElementCollection(targetClass=String.class)
	public List<String> keyWords;
	public double relevanceScore;
	public double pClick;	
	public double bidPrice;
	public double rankScore;
	public double qualityScore;
	public double costPerClick;
	public int position;//1: top , 2: bottom
    public String title; // required
    public double price; // required
    public String thumbnail; // required
    public String description; // required
    public String brand; // required
    public String detail_url; // required
    public String query; //required
    public String category;
}
