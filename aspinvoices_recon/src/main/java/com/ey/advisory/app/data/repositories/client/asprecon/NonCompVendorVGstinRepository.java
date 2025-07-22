package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorVGstinEntity;

@Repository("NonCompVendorVGstinRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface NonCompVendorVGstinRepository
		extends JpaRepository<NonCompVendorVGstinEntity, Long>,
		JpaSpecificationExecutor<NonCompVendorVGstinEntity> {

	@Query("select nonCompVendorVGstinEntity.vendorGstin from NonCompVendorVGstinEntity "
			+ " nonCompVendorVGstinEntity where nonCompVendorVGstinEntity.requestId = :requestId")
	public List<String> getVendorGstin(@Param("requestId") Long requestId);
	
	@Modifying
	@Query("UPDATE NonCompVendorVGstinEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where requestId  = :requestId and vendorGstin =:vendorGstin")
	void updateReportStatus(@Param("requestId") Long requestId,
			@Param("reportStatus") String reportStatus,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("vendorGstin") String vendorGstin);
	
	public List<NonCompVendorVGstinEntity> findByRequestId(Long requestId);
	
	@Query("select nonCompVendorVGstinEntity.filePath from NonCompVendorVGstinEntity nonCompVendorVGstinEntity "
			+ "  where nonCompVendorVGstinEntity.requestId = :requestId "
			+ "  and nonCompVendorVGstinEntity.vendorGstin = :vendorGstin")
	public String getFilePath(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin);
	
	@Modifying
	@Query("UPDATE NonCompVendorVGstinEntity SET emailStatus = :emailStatus,"
			+ " updatedOn =:updatedOn where "
			+ " vendorGstin in :vendorGstin and requestId  = :requestId ")
	void updateEmailStatus(@Param("requestId") Long requestId,
			@Param("vendorGstin") List<String> vendorGstin ,
			@Param("emailStatus") String emailStatus,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	@Query("select nonCompVendorVGstinEntity.returnType from NonCompVendorVGstinEntity nonCompVendorVGstinEntity "
			+ "  where nonCompVendorVGstinEntity.requestId = :requestId "
			+ "  and nonCompVendorVGstinEntity.vendorGstin = :vendorGstin")
	public String getReturnType(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin);
}
