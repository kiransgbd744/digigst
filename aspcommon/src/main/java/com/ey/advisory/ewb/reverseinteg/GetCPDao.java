/**
 * 
 */
package com.ey.advisory.ewb.reverseinteg;

import java.util.List;

/**
 * @author Khalid.Khan
 *
 */
public interface GetCPDao {

	List<ReverseIntegParamsDto> getDistinctGstinDate();

}
