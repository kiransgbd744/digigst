package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Jithendra.B
 *
 */

@Setter
@Getter
@ToString
public class Gstr9InwardSaveUserInputData {

	private String fy;
	private String gstin;
	private String status;
	private List<Gstr9InwardUserInputDTO> userInputList;
}
