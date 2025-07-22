package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilProcEntity;
/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr1ANilProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ANilProcRepository extends CrudRepository<Gstr1ANilProcEntity, Long> {
	@Modifying
	@Query("UPDATE Gstr1ANilProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
	
	
	@Query("SELECT SUM(doc.amt) "
			+ "FROM Gstr1ANilProcEntity doc WHERE "
			+ "doc.supplyType IN('NIL') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<BigDecimal> getNil(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	@Query("SELECT SUM(doc.amt) "
			+ "FROM Gstr1ANilProcEntity doc WHERE "
			+ "doc.supplyType IN('NON') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<BigDecimal> getNon(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	
	@Query("SELECT SUM(doc.amt) "
			+ "FROM Gstr1ANilProcEntity doc WHERE "
			+ "doc.supplyType IN('EXPT') AND doc.sgstin IN(:sgstins) "
			+ "AND doc.returnPeriod IN(:returnPeriods) "
			+ "AND doc.isSaved = true "
			+ "AND doc.isDelete = false")
	public List<BigDecimal> getEXpt(
			@Param("returnPeriods") String returnPeriods,
			@Param("sgstins") String sgstins);
	
	@Modifying
	@Query("UPDATE Gstr1ANilProcEntity doc SET doc.isSaved=true, doc.isError =false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE Gstr1ANilProcEntity doc SET doc.isError=true, doc.isSaved=false "
			+ "WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
	
	
	
	
}
