package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.inward.einvoice.GetIrnContractDetailEntity;

@Repository("GetIrnContractDtlsRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface GetIrnContractDtlsRepository
		extends JpaRepository<GetIrnContractDetailEntity, Long>,
		JpaSpecificationExecutor<GetIrnContractDetailEntity> {

}
