package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr1InvoiceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1InvoiceRepository
		extends JpaRepository<Gstr1InvoiceFileUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr1InvoiceFileUploadEntity> {

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.gstnBatchId = "
			+ ":gstnBatchId, doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity b SET b.isDelete= TRUE "
			+ "WHERE b.invoiceKey IN (:invoiceKey) ")
	public void updateKey(@Param("invoiceKey") String invoiceKey);

	@Query("SELECT "
			+ "SUM(doc.totalNumber),SUM(doc.cancelled),SUM(doc.netNumber) "
			+ "FROM Gstr1InvoiceFileUploadEntity doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true " + "AND doc.isDelete = false")
	public List<Object[]> getInvoice(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.isSaved=true, "
			+ "doc.gstnError = false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.gstnError=true, "
			+ "doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity inv SET inv.isDelete= TRUE "
			+ "WHERE inv.isDelete= FALSE AND  inv.id IN (:deleteIds) ")
	int deleteRecord(@Param("deleteIds") List<Long> deleteIds);

	@Query("SELECT doc.id FROM Gstr1InvoiceFileUploadEntity doc "
			+ "WHERE doc.invoiceKey IN (:docKeys) AND doc.isDelete = false ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP,doc.modifiedBy=:updatedBy "
			+ "WHERE  doc.sgstin = :sgstin AND doc.returnPeriod = :returnPeriod "
			+ "AND doc.isDelete = false AND doc.dataOriginType = :dataOriginType ")
	public void updateExistingEntries(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("dataOriginType") String dataOriginType,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP,doc.modifiedBy=:updatedBy "
			+ "WHERE  doc.sgstin = :sgstin AND doc.returnPeriod = :returnPeriod "
			+ "AND doc.isDelete = false")
	public void updateExistingEntries(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn =:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr1InvoiceFileUploadEntity doc SET doc.isSentToGstn = false,"
			+ " doc.isSaved = false, doc.gstnError = false, doc.gstnBatchId =null"
			+ " WHERE doc.sgstin = :sgstin AND doc.returnPeriod = :retPeriod AND "
			+ " doc.isDelete = false AND doc.isSentToGstn = true")
	public void resetSaveGstr1AuditColumns(@Param("sgstin") String sgstin,
			@Param("retPeriod") String retPeriod);

	Optional<Gstr1InvoiceFileUploadEntity> findTop1BySgstinAndReturnPeriodAndIsDeleteFalseOrderByCreatedOnDesc(
			@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod);

	@Query("SELECT COUNT(*) FROM Gstr1InvoiceFileUploadEntity  WHERE  "
			+ " isDelete = false and sgstin=:sgstin and returnPeriod=:returnPeriod")
	public int isInvSeriDataAvail(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod);

}
