package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.gstr3b.Gstr3BErrorMaterEntity;

/*
 * v
 * @author vishal.verma
 *
 */

@Repository("Gstr3BErrorMasterRepository")
public interface Gstr3BErrorMasterRepository
		extends JpaRepository<Gstr3BErrorMaterEntity, Long>, 
		JpaSpecificationExecutor<Gstr3BErrorMaterEntity> {
	
	@Query("SELECT m.id from Gstr3BErrorMaterEntity m WHERE m.errorCode = :errorCode")
	public Long findByErrorCode(@Param("errorCode") String errorCode);

}
