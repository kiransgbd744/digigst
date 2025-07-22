package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1GstnTxpdFileUploadEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("Gstr1TxpdGstnRepository")
public interface Gstr1TxpdGstnRepository
		extends JpaRepository<Gstr1GstnTxpdFileUploadEntity, Long>,
		JpaSpecificationExecutor<Gstr1GstnTxpdFileUploadEntity> {

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1GstnTxpdFileUploadEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND b.gstnTxpdKey IN (:entityKey) ")
	public void UpdateSameGstnKey(
			@Param("entityKey") List<String> existGstnProcessData);

}
