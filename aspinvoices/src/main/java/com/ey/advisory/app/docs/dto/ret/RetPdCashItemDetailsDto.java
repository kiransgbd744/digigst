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
public class RetPdCashItemDetailsDto {

	@Expose
	@SerializedName("trans_typ")
	private BigDecimal trans_typ;

	@Expose
	@SerializedName("trandate")
	private LocalDate trandate;

	@Expose
	@SerializedName("ipd")
	private BigDecimal ipd;

	@Expose
	@SerializedName("cpd")
	private BigDecimal cpd;

	@Expose
	@SerializedName("spd")
	private BigDecimal spd;

	@Expose
	@SerializedName("cspd")
	private BigDecimal cspd;

	@Expose
	@SerializedName("i_intrpd")
	private BigDecimal i_intrpd;

	@Expose
	@SerializedName("c_intrpd")
	private BigDecimal c_intrpd;

	@Expose
	@SerializedName("s_intrpd")
	private BigDecimal s_intrpd;

	@Expose
	@SerializedName("cs_intrpd")
	private BigDecimal cs_intrpd;

	@Expose
	@SerializedName("c_lfeepd")
	private BigDecimal c_lfeepd;

	@Expose
	@SerializedName("s_lfeepd")
	private BigDecimal s_lfeepd;
}
