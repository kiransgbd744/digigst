package com.ey.advisory.app.data.services.compliancerating;

/**
 * @author Jithendra.B
 *
 */
public interface VendorRatingCriteriaService {

	public void saveVendorRatingCriteria(VendorRatingCriteriaListDTO dto);

	public VendorRatingCriteriaListDTO getVendorRatingCriteria(Long entityId,
			String source);

	public VendorRatingTimestampDTO getTimeStamp(String financialYear,
			Long entityId);
}
