/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.core.dto.EinvEwbDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface OutwardSftpResponseRevIntService {

	public List<EinvEwbDto> getEinvEwbDetails(Long fileId);

}
