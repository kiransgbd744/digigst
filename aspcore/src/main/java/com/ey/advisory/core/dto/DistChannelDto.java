package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class DistChannelDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("distChannel")
	private String distChannel;

	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the distChannel
	 */
	public String getDistChannel() {
		return distChannel;
	}

	/**
	 * @param distChannel the distChannel to set
	 */
	public void setDistChannel(String distChannel) {
		this.distChannel = distChannel;
	}
	
	
}
