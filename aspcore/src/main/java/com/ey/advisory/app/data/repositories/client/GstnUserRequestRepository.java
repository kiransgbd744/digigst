/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GstnUserRequestRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstnUserRequestRepository
		extends CrudRepository<GstnUserRequestEntity, Long> {

	Optional<GstnUserRequestEntity> findTop1ByGstinAndTaxPeriodAndRequestTypeAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("requestType") String requestType,
			@Param("returnType") String returnType);
	
	Optional<GstnUserRequestEntity> findTop1ByGstinAndTaxPeriodAndRequestTypeInAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("requestType") List<String> requestType,
			@Param("returnType") String returnType);

	@Modifying
	@Query("UPDATE GstnUserRequestEntity b SET b.requestStatus = "
			+ ":requestStatus WHERE b.id =:id ")
	void updateRequestStatus(@Param("id") Long id,
			@Param("requestStatus") Integer requestStatus);

	@Modifying
	@Query("UPDATE GstnUserRequestEntity SET  getResponsePayload = :reqClob, "
			+ "requestStatus = :requestStatus, createdOn = :createdOn  WHERE gstin = :gstin and "
			+ "taxPeriod = :taxPeriod and returnType = :returnType")
	void updateGstnResponse(@Param("reqClob") Clob reqClob,
			@Param("requestStatus") Integer requestStatus,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("createdOn") LocalDateTime createdOn);

	GstnUserRequestEntity findByGstinAndTaxPeriodAndReturnType(String gstin,
			String taxPeriod, String string);

	// getting json Response.
	GstnUserRequestEntity findByGstinAndTaxPeriodAndReturnTypeAndRequestType(
			String gstin, String taxPeriod, String retuenType,
			String requestType);

	@Query("SELECT b.id,b.gstin,b.taxPeriod FROM GstnUserRequestEntity b WHERE "
			+ "(b.requestStatus NOT IN(1) OR b.requestStatus IS NULL) AND "
			+ "b.requestType =:requestType AND b.returnType =:returnType AND "
			+ "b.isDelete=false")
	public List<Object[]> findNewUserRequestIds(
			@Param("requestType") String requestType,
			@Param("returnType") String returnType);

	@Query("SELECT b.gstin,b.taxPeriod,b.createdOn FROM GstnUserRequestEntity b"
			+ " WHERE b.gstin in (:listGstin) AND b.requestType =:requestType "
			+ "AND b.returnType =:returnType AND b.isDelete=false")
	List<Object[]> findActiveGetStatus(
			@Param("listGstin") List<String> listGstin,
			@Param("returnType") String returnType,
			@Param("requestType") String requestType);

	@Modifying
	@Query("UPDATE GstnUserRequestEntity b SET b.isDelete = true WHERE gstin = "
			+ ":gstin and taxPeriod = :taxPeriod and requestType = :requestType"
			+ " and returnType = :returnType and b.isDelete = false")
	void deleteOldEntries(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("requestType") String requestType,
			@Param("returnType") String returnType);

	GstnUserRequestEntity findByGstinAndTaxPeriodAndReturnTypeAndRequestTypeAndRequestStatusAndIsDeleteFalse(
			String gstin, String taxPeriod, String gstr3b, String string,
			int i);
	
	GstnUserRequestEntity findTop1ByGstinAndTaxPeriodAndReturnTypeAndRequestTypeAndIsDeleteFalseOrderByIdDesc(String gstin,
			String taxPeriod, String returnType,String requesttype);
	
	//autocal update
	@Modifying
	@Query("UPDATE GstnUserRequestEntity SET  autoCalcResponse = :reqClob, "
			+ "requestStatus = :requestStatus, createdOn = :createdOn  "
			+ " WHERE gstin = :gstin and "
			+ "taxPeriod = :taxPeriod and returnType = :returnType")
	void updateGstinAutoCalGstnResponse(@Param("reqClob") Clob reqClob,
			@Param("requestStatus") Integer requestStatus,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("createdOn") LocalDateTime createdOn);
	
	//interest Get
	@Modifying
	@Query("UPDATE GstnUserRequestEntity SET  intrtAutoCalcResponse = :reqClob, "
			+ "requestStatus = :requestStatus, createdOn = :createdOn  "
			+ " WHERE gstin = :gstin and "
			+ "taxPeriod = :taxPeriod and returnType = :returnType")
	void updateGstinInterestAutoCalGstnResponse(@Param("reqClob") Clob reqClob,
			@Param("requestStatus") Integer requestStatus,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("createdOn") LocalDateTime createdOn);
	
		// getting json Response.
		List<GstnUserRequestEntity> findByGstinInAndTaxPeriodAndReturnTypeAndRequestType(
				List<String> gstin, String taxPeriod, String retuenType,
				String requestType);
	
}
