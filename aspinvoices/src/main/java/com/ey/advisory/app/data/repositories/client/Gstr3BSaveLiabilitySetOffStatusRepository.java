package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffStatusEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr3BSaveLiabilitySetOffStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BSaveLiabilitySetOffStatusRepository
		extends CrudRepository<Gstr3BSaveLiabilitySetOffStatusEntity, Long> {

	List<Gstr3BSaveLiabilitySetOffStatusEntity> findByStatusInAndRefIdIsNotNull(
			List<String> status);

	@Modifying
	@Query("update Gstr3BSaveLiabilitySetOffStatusEntity e set status= :status "
			+ "where id in (:idList)")
	void updateRefIdStatusForList(@Param("idList") List<Long> idList,
			@Param("status") String status);

	@Query("select count(e) from Gstr3BSaveLiabilitySetOffStatusEntity e where"
			+ " gstin = :gstin and taxPeriod= :taxPeriod and status = :status")
	public Long findByGstinAndTaxPeriodAndStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);

	public Gstr3BSaveLiabilitySetOffStatusEntity findFirstByGstinAndTaxPeriodOrderByIdDesc(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("update Gstr3BSaveLiabilitySetOffStatusEntity set status= :status "
			+ " where  id =:id ")
	void updateStatus(@Param("status") String status, @Param("id") Long id);

	@Query("from Gstr3BSaveLiabilitySetOffStatusEntity WHERE "
			+ " gstin IN (:gstinsList) AND taxPeriod = :taxPeriod ")
	List<Gstr3BSaveLiabilitySetOffStatusEntity> findByGstinInAndTaxPeriod(
			@Param("gstinsList") List<String> gstinsList,
			@Param("taxPeriod") String taxPeriod);

}
