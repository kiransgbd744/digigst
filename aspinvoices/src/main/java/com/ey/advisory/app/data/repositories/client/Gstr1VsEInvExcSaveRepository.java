/**
 * 
 */
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

import com.ey.advisory.app.gstr1.einv.Gstr1VsEInvExcSaveEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr1VsEInvExcSaveRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1VsEInvExcSaveRepository
		extends JpaRepository<Gstr1VsEInvExcSaveEntity, Long>,
		JpaSpecificationExecutor<Gstr1VsEInvExcSaveEntity> {
	
	@Modifying
	@Query("Update Gstr1VsEInvExcSaveEntity SET isDelete = true  WHERE "
			+ "docKey in (:docKeyList) AND isDelete = false")
		int updateIsDeleteFlag(
				@Param("docKeyList")List<String> docKeyList);
	
	@Query("from Gstr1VsEInvExcSaveEntity WHERE isDelete = false "
			+ " AND fileId =:fileId AND isPsd = false")
	public List<Gstr1VsEInvExcSaveEntity> findErrorData(
			@Param("fileId") Integer fileId);
	
	public List<Gstr1VsEInvExcSaveEntity> findByFileIdAndIsPsdTrue(
			Integer fileId);


}
