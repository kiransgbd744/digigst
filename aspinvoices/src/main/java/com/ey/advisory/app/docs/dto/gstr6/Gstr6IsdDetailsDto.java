package com.ey.advisory.app.docs.dto.gstr6;

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
public class Gstr6IsdDetailsDto {

	@Expose
	@SerializedName("elglst")
	private List<Gstr6IsdElglstDto> elglst;

	@Expose
	@SerializedName("inelglst")
	private List<Gstr6IsdElglstDto> inelglst;
	
	@Expose
	@SerializedName("docnum")
	private String docnum;
	
	@Expose
	@SerializedName("docdt")
	private String docdt;
	
	@Expose
    @SerializedName("crddt")
    private String crddt;
   
    @Expose
    @SerializedName("crdnum")
    private String crdnum;
    
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
	
	@Expose
	@SerializedName("isd_docty")
	private String isd_docty;	

	

}
