package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.ReturnTableEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 *         Repository for Return Tables class
 */
@Repository("ReturnTableRepository")
public interface ReturnTableRepository
		extends JpaRepository<ReturnTableEntity, Long>,
		JpaSpecificationExecutor<ReturnTableEntity> {
	
	@Query("SELECT count(g.returnTable) FROM ReturnTableEntity g"
			+ " WHERE g.returnTable = :returnTable AND g.retType =:retType and "
			+ "g.retTableName =:retTableName")
	public int findReturnTableValue(@Param("returnTable") String returnTable,
			@Param("retType") String retType, @Param("retTableName") 
	        String retTableName);
}
