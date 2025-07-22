package com.ey.advisory.app.data.repositories.clientBusiness;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.data.entities.clientBusiness.EinvEwbStatisticsEntity;

/**
 * @author Jithendra Kumar B
 *
 */
@Repository("EinvEwbStatisticsRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface EinvEwbStatisticsRepository
		extends JpaRepository<EinvEwbStatisticsEntity, Long>,
		JpaSpecificationExecutor<EinvEwbStatisticsEntity> {

}
