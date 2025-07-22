package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2BLinkingConfigEntity;

@Repository("Gstr2bLinkingConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bLinkingConfigRepository
		extends CrudRepository<Gstr2BLinkingConfigEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr2BLinkingConfigEntity SET isDelete=true "
			+ " WHERE gstin=:gstin AND taxPeriod=:taxPeriod"
			+ " AND isDelete=false ")
	void gstr2bLinkingInActiveUpdate(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("UPDATE Gstr2BLinkingConfigEntity doc SET doc.completedOn= CURRENT_TIMESTAMP,status ='SUCCESS' "
			+ "WHERE doc.id=:id AND doc.isDelete = false")
	void gstr2bLinkingUpdateSuccessStatus(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Gstr2BLinkingConfigEntity doc SET doc.completedOn= CURRENT_TIMESTAMP,status ='Failed' "
			+ "WHERE doc.id=:id AND doc.isDelete = false")
	void gstr2bLinkingUpdateFailedStatus(@Param("id") Long id);

	@Query("SELECT doc FROM Gstr2BLinkingConfigEntity doc "
			+ "WHERE doc.id=:id AND doc.isDelete = false")
	public List<Gstr2BLinkingConfigEntity> getGstr2bLinkingData(
			@Param("id") Long id);

	@Query("SELECT doc FROM Gstr2BLinkingConfigEntity doc "
			+ "WHERE doc.gstin IN :gstins AND doc.taxPeriod IN :taxPeriods AND doc.isDelete=false "
			+ " AND doc.status IN ('Linking In-Progress','Linking Initiated') ")
	public List<Gstr2BLinkingConfigEntity> findByGstinTaxPeriodAndStatus(
			@Param("gstins") List<String> gstins,
			@Param("taxPeriods") List<String> taxPeriods);

	@Modifying
	@Query("UPDATE Gstr2BLinkingConfigEntity doc SET doc.status=:status WHERE doc.id=:id")
	void gstr2bLinkingUpdateStatus(@Param("id") Long id,
			@Param("status") String status);

	public List<Gstr2BLinkingConfigEntity> findByStatusInAndIsDelete(
			@Param("status") List<String> status,
			@Param("isDelete") Boolean isDelete);

	@Query("SELECT doc FROM Gstr2BLinkingConfigEntity doc "
			+ "WHERE doc.gstin IN (:gstins) AND doc.taxPeriod IN (:taxPeriods) AND doc.isDelete=false ")
	public List<Gstr2BLinkingConfigEntity> findByGstinTaxPeriod(
			@Param("gstins") List<String> gstins,
			@Param("taxPeriods") List<String> taxPeriods);
	
	@Modifying
	@Query("UPDATE Gstr2BLinkingConfigEntity doc SET doc.isDelete=true WHERE doc.gstin=:gstin "
			+ " AND doc.taxPeriod=:taxPeriod")
	void updateInActive(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);
	
	@Query("SELECT doc FROM Gstr2BLinkingConfigEntity doc "
			+ "WHERE doc.gstin=:gstin AND doc.taxPeriod=:taxPeriod AND doc.isDelete=false ")
	public List<Gstr2BLinkingConfigEntity> findByGstinAndTaxPeriod(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);
}
