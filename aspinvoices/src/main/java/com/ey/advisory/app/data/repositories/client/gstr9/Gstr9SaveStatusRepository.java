package com.ey.advisory.app.data.repositories.client.gstr9;

import java.sql.Clob;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;

@Repository("gstr9SaveStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9SaveStatusRepository
		extends CrudRepository<Gstr9SaveStatusEntity, Long> {

	List<Gstr9SaveStatusEntity> findByStatusInAndRefIdIsNotNull(
			List<String> status);

	List<Gstr9SaveStatusEntity> findByGstinAndTaxPeriodOrderByIdDesc(
			String gstin, String taxPeriod);

	public Optional<Gstr9SaveStatusEntity> findFirstByGstinAndTaxPeriodOrderByIdDesc(
			String gstin, String taxPeriod);

	@Modifying
	@Query("update Gstr9SaveStatusEntity e set status= :status where id in (:idList)")
	void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status);

	@Modifying
	@Query("update Gstr9SaveStatusEntity set status= :status, filePath = :filePath,"
			+ " pollingResponsePayload = :pollingResponsePayload  where refId = :refId "
			+ " and gstin = :gstin and taxPeriod= :taxPeriod")
	void updateStatusAndFilePath(@Param("refId") String refId,
			@Param("status") String status, @Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("filePath") String filePath,
			@Param("pollingResponsePayload") Clob pollingResponsePayload);

	@Modifying
	@Query("update Gstr9SaveStatusEntity set status= :status, saveResponsePayload = :saveResponsePayload,"
			+ " saveRequestPayload = :saveRequestPayload, refId = :refId  where "
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status ='REQUEST_SUBMITTED'")
	void updateRefIdAndErrors(@Param("refId") String refId,
			@Param("status") String status, @Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("saveRequestPayload") Clob saveRequestPayload,
			@Param("saveResponsePayload") String saveResponsePayload);

	@Query("select count(e) from Gstr9SaveStatusEntity e where"
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status = :status")
	public Long findByGstinAndTaxPeriodAndStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);

	List<Gstr9SaveStatusEntity> findByGstinInAndTaxPeriod(
			List<String> gstnsLists, String returnPeriod);

}
