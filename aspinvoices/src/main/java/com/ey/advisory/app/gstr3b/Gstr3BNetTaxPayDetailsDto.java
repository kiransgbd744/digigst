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
public class Gstr3BNetTaxPayDetailsDto {
	
	 @SerializedName("intr")
     @Expose
     private BigDecimal intr;

     @SerializedName("tx")
     @Expose
     private BigDecimal tx;

     @SerializedName("fee")
     @Expose
     private BigDecimal fee;
}
