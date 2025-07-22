package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr3BPdnlsDto {
	
	@SerializedName("trans_typ")
    @Expose
    private int transType;

    @SerializedName("liab_ldg_id")
    @Expose
    private int liabLdgId;

    @SerializedName("ipd")
    @Expose
    private BigDecimal ipd;

    @SerializedName("cpd")
    @Expose
    private BigDecimal cpd;

    @SerializedName("spd")
    @Expose
    private BigDecimal spd;

    @SerializedName("cspd")
    @Expose
    private BigDecimal cspd;

}
