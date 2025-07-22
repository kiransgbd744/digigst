package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1AsGstr1InvEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr1InvoiceGstnRepository")
public interface Gstr1InvoiceGstnRepository
		extends JpaRepository<Gstr1AsGstr1InvEntity, Long>, 
		JpaSpecificationExecutor<Gstr1AsGstr1InvEntity> {
	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr1AsGstr1InvEntity inv SET inv.isDelete= TRUE "
			+ "WHERE inv.isDelete= FALSE AND inv.invoiceKey IN (:entityKey) ")
	public void UpdateSameGstnKey(
			@Param("entityKey") List<String> existGstnProcessData);
}
 