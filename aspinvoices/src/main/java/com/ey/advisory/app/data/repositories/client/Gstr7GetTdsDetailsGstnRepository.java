package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr7TdsDetailsEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr7GetTdsDetailsGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7GetTdsDetailsGstnRepository
		extends CrudRepository<Gstr7TdsDetailsEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr7TdsDetailsEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.tdsDeductorGstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteTdsHeader(@Param("sGstin") String tdsDeductorGstin,
			@Param("taxPeriod") String returnPeriod,
			@Param("modifiedOn") LocalDateTime modifiedOn);

}
