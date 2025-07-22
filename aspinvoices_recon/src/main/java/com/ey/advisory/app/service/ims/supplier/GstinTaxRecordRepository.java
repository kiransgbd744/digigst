package com.ey.advisory.app.service.ims.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Repository("GstinTaxRecordRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinTaxRecordRepository
		extends JpaRepository<GstinTaxRecordEntity, Long>, 
		JpaSpecificationExecutor<GstinTaxRecordEntity> {

}