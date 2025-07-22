package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author vishal.verma
 *
 */
@Getter
@Setter
@ToString
public class SupplierGetImsInvoicesSectionDtlsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("rtnprd")
	private String rtnprd;

	@Expose
	@SerializedName("gstr1")
	private Gstr1DTO gstr1;

	@Expose
	@SerializedName("gstr1a")
	private Gstr1DTO gstr1a;
	
}



@Data
 class Gstr1DTO {

    @Expose
    @SerializedName("b2b")
    private List<B2BDataDTO> b2b;

    @Expose
    @SerializedName("b2ba")
    private List<B2BDataDTO> b2ba;

    @Expose
    @SerializedName("cdnr")
    private List<B2BDataDTO> cdnr;

    @Expose
    @SerializedName("cdnra")
    private List<B2BDataDTO> cdnra;

    @Expose
    @SerializedName("ecom")
    private ECOMDataDTO ecom;

    @Expose
    @SerializedName("ecoma")
    private ECOMDataDTO ecoma;

}



