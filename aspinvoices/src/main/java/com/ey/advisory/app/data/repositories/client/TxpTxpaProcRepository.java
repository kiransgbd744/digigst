package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.TxpProcEntity;
import com.google.common.primitives.Longs;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("TxpTxpaProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TxpTxpaProcRepository extends CrudRepository<TxpProcEntity, Long> {
	@Modifying
	@Query("UPDATE TxpProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
	
	/*@Modifying
	@Query("UPDATE TxpProcEntity doc SET "
			+ "doc.isSentToGstn = true WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("ids") List<Long> ids);*/
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.adjamt),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM TxpProcEntity doc WHERE "
			+ "doc.gstnBifurcation IN('TXPD') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getTxpd(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.adjamt),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM TxpProcEntity doc WHERE "
			+ "doc.gstnBifurcation IN('TXPDA') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> getTxpda(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	@Modifying
	@Query("UPDATE TxpProcEntity doc SET doc.isSaved=true, doc.isError =false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE TxpProcEntity doc SET doc.isError=true, doc.isSaved=false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
}
