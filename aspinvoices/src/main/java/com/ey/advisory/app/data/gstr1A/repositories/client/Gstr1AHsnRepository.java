package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AHsnFileUploadEntity;

@Repository("Gstr1AHsnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AHsnRepository
		extends JpaRepository<Gstr1AHsnFileUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr1AHsnFileUploadEntity> {

	@Modifying
	@Query("UPDATE Gstr1AHsnFileUploadEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.invHsnKey IN (:entityKey) ")
	public void UpdateSameInvKey(@Param("entityKey") List<String> entityKey);

}
