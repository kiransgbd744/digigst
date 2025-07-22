package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnB2bItemEntity;

@Repository("GetIrnLineItemB2BRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnLineItemB2BRepository
		extends JpaRepository<GetIrnB2bItemEntity, Long>,
		JpaSpecificationExecutor<GetIrnB2bItemEntity> {

	@Query("SELECT i FROM GetIrnB2bItemEntity i WHERE i.headerId = :headerId")
    List<GetIrnB2bItemEntity> findByHeaderId(@Param("headerId") Long headerId);

}
