package com.ey.advisory.app.data.repositories.client.gstr7;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr7TdsSaveEntity;

/**
 * 
 * @author SriBhavya
 *
 */
@Repository("Gstr7TdsSaveRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7TdsSaveRepository extends CrudRepository<Gstr7TdsSaveEntity, Long>{
	
	@Modifying
	@Query("UPDATE Gstr7TdsSaveEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);

}

