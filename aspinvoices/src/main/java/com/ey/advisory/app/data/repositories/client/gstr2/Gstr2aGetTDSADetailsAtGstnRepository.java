package com.ey.advisory.app.data.repositories.client.gstr2;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aTDSADetailsEntity;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr2aGetTDSADetailsAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2aGetTDSADetailsAtGstnRepository
		extends CrudRepository<GetGstr2aTDSADetailsEntity, Long> {

	//Curently isDelete is not added in table
	@Modifying
	@Query("UPDATE GetGstr2aTDSDetailsEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = FALSE AND b.gstin = :gstin AND b.returnPeriod = :returnPeriod")
	void softlyDeleteTdsHeader(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}
