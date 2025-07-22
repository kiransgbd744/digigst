/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public enum IrnStatusMaster {

	NOT_OPTED(1), NOT_APPLICABLE(2), PENDING(3), GENERATION_ERROR(4), GENERATED(
			5), CANCELLED(6), ASP_ERROR(7),DUPLICATE_IRN(8),PUSHED_TO_NIC(10),
	PENDING_ERROR(11),ALREADY_GENERATED_BY_USER(13),GENERATED_AVAILABLE_FOR_CANCELLATION(14)
	,IRN_GENERATED_IN_ERP(
					23), IRN_NOT_GENERATED_IN_ERP(
							24);

	int irnMasterCode;

	IrnStatusMaster(int irnMasterCode) {

		this.irnMasterCode = irnMasterCode;

	}

	public int getIrnStatusMaster() {
		
		return irnMasterCode;
	}

}
