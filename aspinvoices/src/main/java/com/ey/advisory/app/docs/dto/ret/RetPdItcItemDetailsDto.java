package com.ey.advisory.app.docs.dto.ret;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class RetPdItcItemDetailsDto {

	@Expose
	@SerializedName("trans_typ")
	private BigDecimal trans_typ;

	@Expose
	@SerializedName("trandate")
	private LocalDate trandate;

	@Expose
	@SerializedName("i_pdi")
	private BigDecimal i_pdi;

	@Expose
	@SerializedName("i_pdc")
	private BigDecimal i_pdc;

	@Expose
	@SerializedName("i_pds")
	private BigDecimal i_pds;

	@Expose
	@SerializedName("c_pdi")
	private BigDecimal c_pdi;

	@Expose
	@SerializedName("c_pdc")
	private BigDecimal c_pdc;

	@Expose
	@SerializedName("s_pdi")
	private BigDecimal s_pdi;

	@Expose
	@SerializedName("s_pds")
	private BigDecimal s_pds;

	@Expose
	@SerializedName("cs_pdcs")
	private BigDecimal cs_pdcs;
}
