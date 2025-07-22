package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Setter
@Getter
@ToString
public class FileStatusDataSecReqDto extends SearchCriteria {

	public FileStatusDataSecReqDto(String searchType) {
		super(SearchTypeConstants.FILE_STATUS_SEARCH);
	}

	@Expose
	@SerializedName("GSTIN")
	private List<String> gstn = new ArrayList<>();

	@Expose
	@SerializedName("Plant")
	private List<String> plant = new ArrayList<>();

	@Expose
	@SerializedName("PC")
	private List<String> pc = new ArrayList<>();

	@Expose
	@SerializedName("D")
	private List<String> d = new ArrayList<>();

	@Expose
	@SerializedName("L")
	private List<String> l = new ArrayList<>();

	@Expose
	@SerializedName("SO")
	private List<String> so = new ArrayList<>();
	
	@Expose
	@SerializedName("DC")
	private List<String> dc = new ArrayList<>();

	@Expose
	@SerializedName("UD1")
	private List<String> ud1 = new ArrayList<>();

	@Expose
	@SerializedName("UD2")
	private List<String> ud2 = new ArrayList<>();

	@Expose
	@SerializedName("UD3")
	private List<String> ud3 = new ArrayList<>();

	@Expose
	@SerializedName("UD4")
	private List<String> ud4 = new ArrayList<>();
	
	@Expose
	@SerializedName("UD5")
	private List<String> ud5 = new ArrayList<>();

	@Expose
	@SerializedName("UD6")
	private List<String> ud6 = new ArrayList<>();
}