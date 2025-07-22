package com.ey.advisory.services.gstr3b.entity.setoff;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr3BSaveLiabilitySetOffStatusDto {

	@Expose
	private String gstin;

	@Expose
	@SerializedName("liabilitySetoffTimeStamp")
	private LocalDateTime liabilitySetoffTimeStamp;

	@Expose
	@SerializedName("liabilitySetoffStatus")
	private String liabilitySetoffStatus;

}
