/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("SignAndFileRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SignAndFileRepository
		extends CrudRepository<SignAndFileEntity, Long> {

	@Query("SELECT doc.sessionKey FROM SignAndFileEntity doc "
			+ "WHERE doc.sessionKey IN (:docKeys)  AND doc.isDelete = false ")
	public List<String> findSubmitDocsCountsByDocKeys(
			@Param("docKeys") List<String> docKeys);

	/**
	 * This method is used for Sign File Status
	 * 
	 * @author Balakrishna.S
	 * @param gstin
	 * @param retPeriod
	 * @param returnType
	 * @return
	 */
	@Query("select e FROM SignAndFileEntity e WHERE e.gstin IN (:gstin) and "
			+ "e.taxPeriod = :taxPeriod and e.returnType = :returnType "
			+ "and e.isDelete = false ")
	public List<SignAndFileEntity> findGstnFileSignStatus(
			@Param("gstin") List<String> gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);

	@Modifying
	@Query("UPDATE SignAndFileEntity b SET b.ackNum = :ackNum,b.errorMsg  = :errorMsg ,"
			+ " b.modifiedOn = CURRENT_TIMESTAMP,b.status =:status WHERE b.id = :id ")
	void updateStatus(@Param("ackNum") String ackNum,
			@Param("status") String status, @Param("errorMsg") String errorMsg,
			@Param("id") Long id);

	public SignAndFileEntity findFirstByGstinAndTaxPeriodAndReturnTypeOrderByIdDesc(
			String gstin, String taxPeriod, String returnType);

	public List<SignAndFileEntity> findByGstinAndTaxPeriodAndReturnTypeOrderByCreatedOnDesc(
			String gstin, String taxPeriod, String returnType);
	
	@Modifying
	@Query("UPDATE SignAndFileEntity b SET b.errorMsg  = :errorMsg, b.otpStatus = :otpStatus,"
			+ " b.modifiedOn = CURRENT_TIMESTAMP,b.status =:status WHERE b.id = :id ")
	void updateStatusEVC(@Param("status") String status, @Param("errorMsg") String errorMsg,
			@Param("id") Long id, @Param("otpStatus") String otpStatus);
	
	public SignAndFileEntity findByGstinAndTaxPeriodAndReturnTypeAndCreatedOn(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,@Param("returnType") String returnType,
			@Param("createdOn") LocalDateTime createdOn);

	@Modifying
	@Query("UPDATE SignAndFileEntity e SET e.payload = :payload WHERE e.id = :id")
	public void updatePayloadById(@Param("payload") Clob payload, @Param("id") Long id);



}
