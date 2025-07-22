package com.ey.advisory.app.docs.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component
@Data
public class DocSeriesSDeleteReqDto {

	@Expose
	@SerializedName("id")
	private List<Long> id;

	@Expose
	@SerializedName("entityid")
	private String entityid;
}
