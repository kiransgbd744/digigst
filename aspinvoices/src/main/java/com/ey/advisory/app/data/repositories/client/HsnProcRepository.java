package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.HsnProcEntity;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("HsnProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface HsnProcRepository extends CrudRepository<HsnProcEntity, Long> {

	@Modifying
	@Query("UPDATE HsnProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
	/*@Modifying
	@Query("UPDATE HsnProcEntity doc SET "
			+ "doc.isSentToGstn = true WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("ids") List<Long> ids);*/
	
	@Query("SELECT "
			+ "COUNT(doc.id),"
			+ "SUM(doc.taxableval),SUM(totalvalue),"
			+ "SUM(doc.igst),SUM(doc.cgst),SUM(doc.sgst),SUM(doc.cess) "
			+ "FROM HsnProcEntity doc WHERE "
			+ "doc.sgstin IN(:sgstins) AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<Object[]> gethsn(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	
	@Modifying
	@Query("UPDATE HsnProcEntity doc SET doc.isSaved=true, doc.isError=false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	@Modifying
	@Query("UPDATE HsnProcEntity doc SET doc.isError=true, doc.isSaved=false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
	
	
	
	
}
