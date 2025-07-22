package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2InitiateReconInitiatedByDto {

	@Expose
	private String initiatedBy;
}
