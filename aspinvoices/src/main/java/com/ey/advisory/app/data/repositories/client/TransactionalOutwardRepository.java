/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.TransactionalOutwardEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("TransactionalOutwardRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TransactionalOutwardRepository
		extends CrudRepository<TransactionalOutwardEntity, Long> {

	@Modifying
	@Query(value = "UPDATE TRANSACTIONAL_MONITORING_OUTWARD A "
			+ "SET (CLOUD_NO_OF_INVOICES,CLOUD_TOTAL_INV_VALUE,CLOUD_ASSESSABLE_VALUE,CLOUD_IGST_AMT "
			+ ",CLOUD_CGST_AMT,CLOUD_SGST_AMT,CLOUD_CESS_AMT) = (SELECT COUNT(ID),SUM(IFNULL(DOC_AMT,0)) "
			+ ",SUM(IFNULL(INV_ASSESSABLE_AMT,0)) ,SUM(IFNULL(INV_IGST_AMT,0)) "
			+ ",SUM(IFNULL(INV_CGST_AMT,0)) ,SUM(IFNULL(INV_SGST_AMT,0)) "
			+ ",SUM(IFNULL(INV_CESS_ADVLRM_AMT,0)) "
			+ "FROM ANX_OUTWARD_DOC_HEADER B "
			+ "WHERE A.GSTIN = B.SUPPLIER_GSTIN "
			+ "AND A.DERIVED_RET_PERIOD = B.DERIVED_RET_PERIOD "
			+ "AND A.DOC_TYPE = B.DOC_TYPE "
			+ "AND A.SUPPLY_TYPE = B.SUPPLY_TYPE "
			+ "AND A.SOURCE_ID = B.DERIVED_SOURCE_ID "
			+ "AND IS_DELETE = FALSE AND DATAORIGINTYPECODE = 'A') WHERE GSTIN "
			+ "= :gstin AND DERIVED_RET_PERIOD = :drvdRetPeriod "
			+ "AND SOURCE_ID = :sourceId AND IS_DELETE = FALSE  AND A.STATUS = 3", nativeQuery = true)
	void setCloudValuesForPushPendingVerification(@Param("gstin") String gstin,
			@Param("drvdRetPeriod") Integer drvdRetPeriod,
			@Param("sourceId") String sourceId);
	
	
	@Modifying
	@Query(value = "UPDATE TRANSACTIONAL_MONITORING_OUTWARD A "
			+ "SET (CLOUD_NO_OF_INVOICES,CLOUD_TOTAL_INV_VALUE,CLOUD_ASSESSABLE_VALUE,CLOUD_IGST_AMT "
			+ ",CLOUD_CGST_AMT,CLOUD_SGST_AMT,CLOUD_CESS_AMT) = (SELECT COUNT(ID),SUM(IFNULL(DOC_AMT,0)) "
			+ ",SUM(IFNULL(INV_ASSESSABLE_AMT,0)) ,SUM(IFNULL(INV_IGST_AMT,0)) "
			+ ",SUM(IFNULL(INV_CGST_AMT,0)) ,SUM(IFNULL(INV_SGST_AMT,0)) "
			+ ",SUM(IFNULL(INV_CESS_ADVLRM_AMT,0)) "
			+ "FROM ANX_OUTWARD_DOC_HEADER B "
			+ "WHERE A.GSTIN = B.SUPPLIER_GSTIN "
			+ "AND A.DERIVED_RET_PERIOD = B.DERIVED_RET_PERIOD "
			+ "AND UPPER(A.DOC_TYPE) = UPPER(B.DOC_TYPE) "
			+ "AND UPPER(A.SUPPLY_TYPE) = UPPER(B.SUPPLY_TYPE) "
			+ "AND A.SOURCE_ID = B.DERIVED_SOURCE_ID "
			+ "AND A.STATUS = (CASE WHEN B.GSTN_ERROR = TRUE THEN 11 WHEN B.ASP_INVOICE_STATUS = 2 THEN 10 ELSE 9 END) "
			+ "AND IS_DELETE = FALSE AND DATAORIGINTYPECODE = 'A') WHERE GSTIN "
			+ "= :gstin AND DERIVED_RET_PERIOD = :drvdRetPeriod "
			+ "AND SOURCE_ID = :sourceId AND IS_DELETE = FALSE  AND A.STATUS <> 3", nativeQuery = true)
	void setCloudValues(@Param("gstin") String gstin,
			@Param("drvdRetPeriod") Integer drvdRetPeriod,
			@Param("sourceId") String sourceId);
	
	@Modifying
	@Query(value = "UPDATE TRANSACTIONAL_MONITORING_OUTWARD SET IS_DELETE = TRUE"
			+ " WHERE GSTIN = :gstin AND DERIVED_RET_PERIOD = :drvdRetPeriod "
			+ "AND SOURCE_ID = :sourceId "
			/*+ "AND DOC_TYPE = :docType "
			+ "AND SUPPLY_TYPE = :supType "
			+ "AND STATUS = :status "*/
			+ "AND IS_DELETE = FALSE", nativeQuery = true)
	void softDelete(@Param("gstin") String gstin,
			@Param("drvdRetPeriod") Integer drvdRetPeriod,
			@Param("sourceId") String sourceId);
}
