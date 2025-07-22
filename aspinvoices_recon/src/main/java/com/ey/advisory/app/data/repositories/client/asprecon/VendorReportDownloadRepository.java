package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorReportDownloadEntity;

@Repository("VendorReportDownloadRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorReportDownloadRepository
		extends JpaRepository<VendorReportDownloadEntity, Long>,
		JpaSpecificationExecutor<VendorReportDownloadEntity> {

	@Query("Select vendorReportDownloadEntity from VendorReportDownloadEntity vendorReportDownloadEntity where "
			+ " requestID =:requestId")
	public VendorReportDownloadEntity findByRequestId(
			@Param("requestId") Long requestId);

	@Query("Select vendorReportDownloadEntity from VendorReportDownloadEntity vendorReportDownloadEntity where "
			+ "vendorReportDownloadEntity.createdBy = :createdBy")
	public List<VendorReportDownloadEntity> findByUserName(
			@Param("createdBy") String createdBy);
}
