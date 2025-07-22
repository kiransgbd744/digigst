package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public interface FileStatusDao {

	List<Gstr1FileStatusEntity> fileStatusSection(String sectionType,
			String buildQuery, LocalDate dataRecvFrom, LocalDate dataRecvTo,
			String fileType, String dataType,String source);

	List<Object[]> fileStatus(String buildQuery, String fileType,
			String dataType, LocalDate dataRecvFrom, LocalDate dataRecvTo,String source);

	List<Object[]> masterFileStatus(final String buildQuery,
			final LocalDate fromDate, final LocalDate toDate,
			final String fileType, Long  entityId);

}
