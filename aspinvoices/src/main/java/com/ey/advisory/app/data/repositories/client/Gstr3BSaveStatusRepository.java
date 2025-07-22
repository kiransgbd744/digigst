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

import com.ey.advisory.app.data.entities.client.Gstr3BSaveStatusEntity;

@Repository("gstr3BSaveStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BSaveStatusRepository
		extends CrudRepository<Gstr3BSaveStatusEntity, Long> {

	List<Gstr3BSaveStatusEntity> findByStatusInAndRefIdIsNotNull(
			List<String> status);

	@Modifying
	@Query("update Gstr3BSaveStatusEntity e set status= :status where id in (:idList)")
	void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status);

	@Query("select count(e) from Gstr3BSaveStatusEntity e where"
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status = :status")
	public Long findByGstinAndTaxPeriodAndStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);

	@Modifying
	@Query("update Gstr3BSaveStatusEntity set status= :status, filePath = :filePath,pollingResponsePayload = :pollingResponsePayload  where refId = :refId "
			+ " and gstin = :gstin and taxPeriod= :taxPeriod")
	void updateStatusAndFilePath(@Param("refId") String refId,
			@Param("status") String status, @Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("filePath") String filePath,
			@Param("pollingResponsePayload") Clob pollingResponsePayload);

	public Gstr3BSaveStatusEntity findFirstByGstinAndTaxPeriodOrderByIdDesc(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);

	@Query("SELECT e FROM Gstr3BSaveStatusEntity e WHERE gstin = :gstin AND taxPeriod= :taxPeriod AND createdOn= :createdOn")
	public Gstr3BSaveStatusEntity findByGstinAndTaxPeriodAndCreatedOn(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("createdOn") LocalDateTime createdOn);

}
