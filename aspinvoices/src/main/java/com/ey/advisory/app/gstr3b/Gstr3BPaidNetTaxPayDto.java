package com.ey.advisory.app.gstr3b;

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
public class Gstr3BPaidNetTaxPayDto {
	
	@SerializedName("trans_typ")
    @Expose
    private int transType;

    @SerializedName(value = "trans_desc", alternate = { "tran_desc" })
    @Expose
    private String transDesc;

    @SerializedName("liab_ldg_id")
    @Expose
    private int liabLdgId;

    @SerializedName("sgst")
    @Expose
    private Gstr3BNetTaxPayDetailsDto sgst;

    @SerializedName("cgst")
    @Expose
    private Gstr3BNetTaxPayDetailsDto cgst;

    @SerializedName("cess")
    @Expose
    private Gstr3BNetTaxPayDetailsDto cess;

    @SerializedName("igst")
    @Expose
    private Gstr3BNetTaxPayDetailsDto igst;

}
