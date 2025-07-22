package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsGstnCountEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsGstnCountRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsGstnCountRepository
		extends JpaRepository<ImsGstnCountEntity, Long>,
		JpaSpecificationExecutor<ImsGstnCountEntity> {

	@Modifying
	@Query("UPDATE ImsGstnCountEntity e SET e.isDelete = true WHERE e.gstin =:gstin and e.goodsType =:type")
	void setDeleteTrueByGstinAndType(@Param("gstin") String gstin, @Param("type") String type);

}
