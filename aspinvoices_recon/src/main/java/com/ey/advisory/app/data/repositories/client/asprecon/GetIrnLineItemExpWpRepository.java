package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnExpWpItemEntity;

@Repository("GetIrnLineItemExpWpRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnLineItemExpWpRepository
		extends JpaRepository<GetIrnExpWpItemEntity, Long>,
		JpaSpecificationExecutor<GetIrnExpWpItemEntity> {

	@Query("SELECT i FROM GetIrnExpWpItemEntity i WHERE i.headerId = :headerId")
	List<GetIrnExpWpItemEntity> findByHeaderId(@Param("headerId") Long headerId);

}
