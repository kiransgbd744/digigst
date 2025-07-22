/**
 * 
 */
package com.ey.advisory.app.data.daos.client.anx2;

import java.util.List;

import com.ey.advisory.app.docs.dto.VendorANX2SummaryRespDto;
import com.ey.advisory.core.dto.VendorSummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface VendorANX2SummaryDao {

	List<VendorANX2SummaryRespDto> loadVendorANX2Summary(
			VendorSummaryReqDto criteria);

}
