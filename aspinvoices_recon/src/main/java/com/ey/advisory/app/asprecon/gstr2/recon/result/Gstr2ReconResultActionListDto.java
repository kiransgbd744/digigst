package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2ReconResultActionListDto {
	
	private String resp;
	
	private List<Gstr2ReconResultActionDto> reconIds;

}
