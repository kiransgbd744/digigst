package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetOffComputeSaveDto {
	
	
	Gstr3BSetOffComputeSaveInnerDto innerDto1;
	
	List<Gstr3BSetOffComputeSaveClosingBalDto> innerDto2;
	

}
