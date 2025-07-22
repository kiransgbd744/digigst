package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class Gstr9DifferentialTaxSaveUserInputDto {

	private String fy;
	private String gstin;
	private String status;
	private List<Gstr9DiffTaxMapDto> userInputList;
}
