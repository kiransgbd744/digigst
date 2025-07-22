package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnSezWopItemEntity;

@Repository("GetIrnLineItemSezwopRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnLineItemSezwopRepository
		extends JpaRepository<GetIrnSezWopItemEntity, Long>,
		JpaSpecificationExecutor<GetIrnSezWopItemEntity> {

	@Query("SELECT i FROM GetIrnSezWopItemEntity i WHERE i.headerId = :headerId")
	List<GetIrnSezWopItemEntity> findByHeaderId(@Param("headerId") Long headerId);

}
