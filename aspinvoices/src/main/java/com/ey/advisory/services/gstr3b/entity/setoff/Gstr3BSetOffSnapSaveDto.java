package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetOffSnapSaveDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	private List<LedgerDetailsDto> ledgerDetails = new ArrayList<>();
	
	private List<Gstr3bDetailsDto> gstr3bDetails = new ArrayList<>();

}
