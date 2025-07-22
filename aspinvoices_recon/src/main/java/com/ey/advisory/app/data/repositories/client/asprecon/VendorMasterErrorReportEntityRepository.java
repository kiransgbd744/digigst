package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterErrorReportEntity;

@Repository("VendorMasterErrorReportEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorMasterErrorReportEntityRepository extends JpaRepository<VendorMasterErrorReportEntity, Long>,
		JpaSpecificationExecutor<VendorMasterErrorReportEntity> {

	VendorMasterErrorReportEntity findByInvoiceKey(String invoiceKey);
	
	List<VendorMasterErrorReportEntity> findByFileId(Long fileId);
	
	List<VendorMasterErrorReportEntity> findByRefId(String fileId);

	List<VendorMasterErrorReportEntity> findByPayloadId(String payloadId);

}
