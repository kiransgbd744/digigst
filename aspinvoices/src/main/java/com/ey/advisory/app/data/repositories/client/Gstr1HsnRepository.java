package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1HsnFileUploadEntity;
@Repository("Gstr1HsnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1HsnRepository
		extends JpaRepository<Gstr1HsnFileUploadEntity, Long>, 
		        JpaSpecificationExecutor<Gstr1HsnFileUploadEntity> {

	@Modifying
	@Query("UPDATE Gstr1HsnFileUploadEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.invHsnKey IN (:entityKey) ")
	public void UpdateSameInvKey(
			@Param("entityKey") List<String> entityKey);
	
}
