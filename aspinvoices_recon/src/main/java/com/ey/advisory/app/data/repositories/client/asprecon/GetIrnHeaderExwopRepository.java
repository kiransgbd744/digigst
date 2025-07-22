package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnExpWopHeaderEntity;

@Repository("GetIrnHeaderExwopRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnHeaderExwopRepository
		extends JpaRepository<GetIrnExpWopHeaderEntity, Long>,
		JpaSpecificationExecutor<GetIrnExpWopHeaderEntity> {

	@Query("SELECT h FROM GetIrnExpWopHeaderEntity h WHERE h.irn = :irn AND h.irnStatus = :irnStatus")
	Optional<GetIrnExpWopHeaderEntity> findByIrnAndIrnStatus(@Param("irn") String irn, @Param("irnStatus") String irnStatus);

}
