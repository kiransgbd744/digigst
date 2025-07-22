package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1ARGstnDetailsEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1ATGstnRepository")
public interface Gstr1ATGstnRepository
		extends JpaRepository<Gstr1ARGstnDetailsEntity, Long>,
		JpaSpecificationExecutor<Gstr1ARGstnDetailsEntity> {

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1ARGstnDetailsEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND b.gstnAtKey IN (:entityKey) ")
	public void UpdateSameGstnKey(
			@Param("entityKey") List<String> existGstnProcessData);

}
