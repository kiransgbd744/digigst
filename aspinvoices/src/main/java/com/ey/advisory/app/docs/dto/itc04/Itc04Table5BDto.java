package com.ey.advisory.app.docs.dto.itc04;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Itc04Table5BDto {

	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("jw_stcd")
	private String jwStCd;

	@Expose
	@SerializedName("items")
	private List<Itc04ItemsDto> itc04ItemsDto;
	
	@Expose
	@SerializedName("flag")
	private String flag;
	
	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;	
	
	@Expose
	@SerializedName("error_cd")
	private String error_cd;

}
