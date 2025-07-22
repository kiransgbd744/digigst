package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnB2bHeaderEntity;

@Repository("GetIrnHeaderB2BRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnHeaderB2BRepository
		extends JpaRepository<GetIrnB2bHeaderEntity, Long>,
		JpaSpecificationExecutor<GetIrnB2bHeaderEntity> {

	@Query("SELECT h FROM GetIrnB2bHeaderEntity h WHERE h.irn = :irn AND h.irnStatus = :irnStatus")
	Optional<GetIrnB2bHeaderEntity> findByIrnAndIrnStatus(@Param("irn") String irn, @Param("irnStatus") String irnStatus);

}
