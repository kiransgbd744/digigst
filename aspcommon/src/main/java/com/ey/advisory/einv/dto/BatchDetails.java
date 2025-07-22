
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BatchDetails implements Serializable {

	private static final long serialVersionUID = 7013220121323285986L;

	@SerializedName("Nm")
	@Expose
	private String nm;

	@SerializedName(value = "Expdt", alternate = ("ExpDt"))
	@Expose
	private LocalDate expDt;

	@SerializedName(value = "wrDt", alternate = ("WrDt"))
	@Expose
	private LocalDate wrDt;

	public static boolean isEmpty(BatchDetails bchDtls) {
		BatchDetails bchDetails = new BatchDetails();
		return bchDetails.hashCode() == bchDtls.hashCode();
	}

}
