package com.ey.advisory.app.data.repositories.client.asprecon;

import java.math.BigDecimal;
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

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;


/**
 * @author Sakshi.jain
 *
 */
@Repository("GetIrnListingRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GetIrnListingRepository
		extends JpaRepository<GetIrnListEntity, Long>,
		JpaSpecificationExecutor<GetIrnListEntity> {

	@Modifying
	@Query("update GetIrnListEntity set isDelete = true where irn =:irn and irnSts = 'ACT' ")
	public void updateIsDeleteFlag(@Param("irn") String irn);
	
	List<GetIrnListEntity> findByCusGstinAndMonYear(String gstin, String month);
	
	@Modifying
	@Query("update GetIrnListEntity set batchId =:batchId, getIrnDetSts = 'NOT_INITIATED', updatedOn = CURRENT_TIMESTAMP where id IN (:ids) ")
	public void updateBatchId(@Param("ids") List<Long> ids, @Param("batchId") Long batchId);
	
	@Modifying
	@Query("update GetIrnListEntity set getIrnDetSts =:status, getIrnDetIniOn = CURRENT_TIMESTAMP where irn =:irn and irnSts =:irnSts ")
	public void updateGetDtlSts(@Param("irn") String irn,@Param("status") String status,@Param("irnSts") String irnSts );
	
	@Query(" Select e from GetIrnListEntity e where e.irn =:irn and e.isDelete = false and e.getIrnDetSts = 'SUCCESS' ")
	public GetIrnListEntity getByIrnNum(@Param("irn") String irn);

	// jpa query to get the count of records where is delete = false and
	// getIrnDetSts = 'SUCCESS'
	@Query(" Select count(e) from GetIrnListEntity e where e.isDelete = false and e.getIrnDetSts = 'SUCCESS' ")
	public Long getActiveCount();

	// jpa query to create the findByIrnIn
	@Query(" Select e from GetIrnListEntity e where e.irn in (:irn) and e.isDelete = false and e.getIrnDetSts = 'SUCCESS' ")
	public List<GetIrnListEntity> findByIrnIn(@Param("irn") List<String> irn);

	@Query(value = "SELECT (SUPPLIER_GSTIN || '|' || CUST_GSTIN || '|' || DOC_NUM || '|' || DOC_TYPE || '|' || TO_VARCHAR(DOC_DATE, 'DD.MM.YYYY')) FROM TBL_GETIRN_LIST WHERE IS_DELETE = false AND PR_TAGGING IS NULL AND GET_DETAIL_IRN_STATUS = 'SUCCESS' ", nativeQuery = true)
	List<String> findUntaggedAndSuccessfulRecordsWithDetails();

	@Query(value = "SELECT (SUPPLIER_GSTIN || '|' || CUST_GSTIN || '|' || DOC_NUM || '|' || DOC_TYPE || '|' || TO_VARCHAR(DOC_DATE, 'DD.MM.YYYY')) FROM TBL_GETIRN_LIST WHERE IS_DELETE = false AND GET_DETAIL_IRN_STATUS = 'SUCCESS' and id in (:listingId) ", nativeQuery = true)
	List<String> findSuccessfulRecordsWithDetailsWithListingId(@Param("listingId") List<Long> listingId);
	
	@Modifying
	@Query(value = "UPDATE TBL_GETIRN_LIST SET PR_TAGGING = 'Available', PR_TAGGING_TIME_STATUS = CURRENT_TIMESTAMP WHERE (SUPPLIER_GSTIN || '|' || CUST_GSTIN || '|' || DOC_NUM || '|' || DOC_TYPE || '|' || "
								+ " TO_VARCHAR(DOC_DATE, 'DD.MM.YYYY')) in (:docKeys)", nativeQuery = true)
	public int updateDocKeys(@Param("docKeys") List<String> docKeys);
	
	@Query("SELECT e FROM GetIrnListEntity e WHERE e.irn =:irnList AND e.batchId =:batchId and e.irnSts =:irnSts and e.isDelete = false")
	GetIrnListEntity findAllByIrnListAndBatchId(@Param("irnList") String irnList, @Param("batchId") Long batchId,
			@Param("irnSts") String irnSts);

	@Modifying
	@Query("UPDATE GetIrnListEntity g " + "SET g.docNum =:docNum, " + "    g.docTyp =:docType, "
			+ "    g.suppGstin =:suppGstin, " +  "    g.docDate =:docDate, "
			+ "    g.totInvAmt =:totInvAmt , g.suppType =:supplyType " + " WHERE g.irn =:irn " + "AND g.irnSts =:irnStatus")
	int updateIrnDetails(@Param("docNum") String docNum, @Param("docType") String docType,
			@Param("suppGstin") String suppGstin,
			@Param("docDate") LocalDateTime docDate, @Param("totInvAmt") BigDecimal totInvAmt, @Param("irn") String irn,
			@Param("irnStatus") String irnStatus,@Param("supplyType") String supplyType );
}