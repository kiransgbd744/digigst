/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Itc04OriginalDocCheckService {

	Map<String, List<ProcessingResult>> checkForItc04OrgInvoices(
			List<Itc04HeaderEntity> docs);

}
