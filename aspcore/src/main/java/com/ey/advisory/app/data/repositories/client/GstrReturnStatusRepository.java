package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;

@Repository("gstrReturnStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstrReturnStatusRepository
		extends CrudRepository<GstrReturnStatusEntity, Long>,
		JpaSpecificationExecutor<GstrReturnStatusEntity> {

	@Modifying
	@Query("UPDATE GstrReturnStatusEntity b SET b.status = :status "
			+ "WHERE b.gstin = :gstin AND b.taxPeriod = :taxPeriod AND "
			+ "b.returnType = :returnType")
	void updateReturnStatus(@Param("status") String status,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);

	@Modifying
	@Query("UPDATE GstrReturnStatusEntity b SET b.status = :status,b.submitRefid"
			+ " = :submitRefid WHERE b.gstin = :gstin AND b.taxPeriod = :taxPeriod and updatedOn = CURRENT_TIMESTAMP")
	void updateReturnStatusWithSubmitId(@Param("status") String status,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("submitRefid") String submitRefid);

	@Query("SELECT b from GstrReturnStatusEntity b"
			+ " WHERE  b.gstin IN (:gstins) AND b.taxPeriod = :taxPeriod AND b.returnType = 'GSTR3B' ")
	public List<GstrReturnStatusEntity> getStatuswithTaxPeriod(
			@Param("gstins") List<String> gstins,
			@Param("taxPeriod") String taxPeriod);

	@Query("SELECT b from GstrReturnStatusEntity b"
			+ " WHERE  b.gstin IN (:gstins) AND b.returnType = 'GSTR3B' ")
	public List<GstrReturnStatusEntity> getAllStatus(
			@Param("gstins") List<String> gstins);

	public List<GstrReturnStatusEntity> findByGstinIn(List<String> gstinList);

	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstin(
			List<String> gstinList, String returnType, List<String> taxPeriods,
			boolean isCounterParty);

	public List<GstrReturnStatusEntity> findByGstinInAndTaxPeriodInAndIsCounterPartyGstinTrueAndArnNoIsNotNull(
			List<String> gstinList, List<String> taxPeriods);

	public GstrReturnStatusEntity findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstin(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,@Param("isCounterPartyGstin") Boolean isCounterPartyGstin);
	
	public List<GstrReturnStatusEntity> findByGstinAndTaxPeriodAndIsCounterPartyGstin(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("isCounterPartyGstin") Boolean isCounterPartyGstin);

	public GstrReturnStatusEntity findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
			String gstin, String taxPeriod, String returnType);

	public GstrReturnStatusEntity findByGstinAndTaxPeriodAndReturnTypeContainingIgnoreCaseAndIsCounterPartyGstinFalse(
			String gstin, String taxPeriod, String returnType);

	@Query("SELECT b from GstrReturnStatusEntity b"
			+ " WHERE  b.gstin = :gstin AND  b.taxPeriod = :taxPeriod AND b.returnType = :returnType "
			+ " AND  b.status = 'SUBMITTED' AND b.submitRefid IS NOT NULL AND b.isCounterPartyGstin = false ")
	public GstrReturnStatusEntity findSubmittedRecords(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);

	@Modifying
	@Query("UPDATE GstrReturnStatusEntity b SET b.status = :status,b.updatedOn = CURRENT_TIMESTAMP,b.filingDate = CURRENT_TIMESTAMP,b.arnNo = :arnNo "
			+ " WHERE b.gstin = :gstin AND b.taxPeriod = :taxPeriod AND "
			+ " b.returnType = :returnType")
	public int updateReturnStatusFiling(@Param("status") String status,
			@Param("arnNo") String arnNo, @Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);

	public List<GstrReturnStatusEntity> findByGstinAndTaxPeriodInAndReturnTypeAndIsCounterPartyGstinFalse(
			String gstin, List<String> taxPeriods, String returnType);

	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeInAndTaxPeriodInAndIsCounterPartyGstinTrue(
			List<String> gstinList, List<String> returnType,
			List<String> taxPeriods);

	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeInAndTaxPeriodInAndIsCounterPartyGstinFalse(
			List<String> gstinList, List<String> returnType,
			List<String> taxPeriods);

	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstinTrue(
			List<String> gstinList, String returnType, List<String> taxPeriods);

	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstinFalse(
			List<String> gstinList, String returnType, List<String> taxPeriods);

	public int countByGstinAndReturnTypeAndStatusAndTaxPeriodInAndIsCounterPartyGstinFalse(
			String gstin, String returnType, String status,
			List<String> taxPeriods);

	public int countByGstinAndReturnTypeAndStatusAndTaxPeriodInAndIsCounterPartyGstinTrue(
			String gstin, String returnType, String status,
			List<String> taxPeriods);

	public List<GstrReturnStatusEntity> findByGstinInAndTaxPeriodAndReturnTypeInAndStatusIgnoreCase(
			List<String> gstinList, String taxPeriod, List<String> returnTypes,
			String status);

	public GstrReturnStatusEntity findByGstinAndTaxPeriodAndReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
			String gstin, String taxPeriod, String returnType, String status);

	List<GstrReturnStatusEntity> findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
			String returnType, String status);

	@Query(value = " select return_type,gstin from GSTR_RETURN_STATUS "
			+ " where gstin in (:gstinList)  " + " and taxperiod =:taxPeriod  "
			+ " and status in ('FILED','Filed')  and IS_COUNTER_PARTY_GSTIN = false group by return_type, gstin ", nativeQuery = true)
	public List<Object[]> getReturnTypebygstinstaxPeriod(
			@Param("gstinList") List<String> gstinList,
			@Param("taxPeriod") String taxPeriod);

	@Query(value = " SELECT GSTIN,COUNT(*) FROM GSTR_RETURN_STATUS "
			+ " WHERE GSTIN IN (:gstinList)  AND TAXPERIOD IN (:taxPeriod)  "
			+ " AND STATUS IN ('FILED','Filed') AND RETURN_TYPE =:returnType AND "
			+ "IS_COUNTER_PARTY_GSTIN =:isCounterParty GROUP BY GSTIN  ", nativeQuery = true)
	public List<Object[]> getFilingCountForGstin(
			@Param("gstinList") List<String> gstinList,
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("returnType") String returnType,
			@Param("isCounterParty") boolean isCounterParty);
	
	public List<GstrReturnStatusEntity> findByGstinInAndReturnTypeInAndTaxPeriodIn(
			List<String> gstinList, List<String> returnType,
			List<String> taxPeriods);
	
	@Query("SELECT b from GstrReturnStatusEntity b "
			+ "WHERE b.taxPeriod IN (:taxPeriod) AND b.gstin IN (:gstins) AND b.returnType = 'GSTR3B' "
			+ "AND b.status IN ('FILED','Filed') AND b.isCounterPartyGstin = false ")
	public List<GstrReturnStatusEntity> getAllwithTaxPeriod(
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("gstins") List<String> gstins);
	
	@Query("SELECT b from GstrReturnStatusEntity b"
			+ " WHERE  b.gstin = :gstins AND b.taxPeriod = :taxPeriod AND b.returnType = 'GSTR1' ")
	public GstrReturnStatusEntity getGstr1StatuswithTaxPeriod(
			@Param("gstins") String gstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Query("SELECT b from GstrReturnStatusEntity b"
			+ " WHERE  b.gstin = :gstins AND b.taxPeriod = :taxPeriod AND b.returnType = 'GSTR3B' ")
	public GstrReturnStatusEntity getGstr3bStatuswithTaxPeriod(
			@Param("gstins") String gstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Query(value = "SELECT (GSTIN || '|' || TAXPERIOD) FROM GSTR_RETURN_STATUS WHERE RETURN_TYPE = 'GSTR6' AND IS_COUNTER_PARTY_GSTIN = false AND STATUS IN ('Filed','FILED') AND (GSTIN || '|' || TAXPERIOD) IN (:docKeys) ", nativeQuery = true)
	List<String> getFiledGstr6Keys(@Param("docKeys") List<String> docKeys);

}
