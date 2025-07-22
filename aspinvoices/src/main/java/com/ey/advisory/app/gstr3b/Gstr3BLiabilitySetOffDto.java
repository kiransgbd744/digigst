package com.ey.advisory.app.gstr3b;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@AllArgsConstructor
public class Gstr3BLiabilitySetOffDto {

	private String liabilitySetoffStatus;

	private String updatedOn;

	private List<LedgerDetailsDto> ledgerDetails;

	private List<PaidThroughItcDto> gstr3bDetails;

	private String message;
	
	private Boolean isRule86B;
}
