package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1B2csGstnDetailsEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("Gstr1B2CSGstnRepository")
public interface Gstr1B2CSGstnRepository
		extends JpaRepository<Gstr1B2csGstnDetailsEntity, Long>,
		JpaSpecificationExecutor<Gstr1B2csGstnDetailsEntity> {

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1B2csGstnDetailsEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND b.gstnB2csKey IN (:entityKey) ")
	public void UpdateSameGstnKey(
			@Param("entityKey") List<String> existGstnProcessData);

}
