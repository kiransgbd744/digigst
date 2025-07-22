package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ELExtractEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("ELExtractRepository")
public interface ELExtractRepository extends 
	JpaRepository<ELExtractEntity, Long> ,
	JpaSpecificationExecutor<ELExtractEntity>{
	
	/**
	 * @param elId
	 */
	@Modifying
	@Transactional
	@Query("UPDATE ELExtractEntity SET isFlag =true WHERE elId = :elId")
	public void deleterecord(@Param("elId") Long elId);
	
	/**
	 * @return
	 */
	@Query("SELECT entity FROM ELExtractEntity entity WHERE entity.isFlag=false")
	public List<ELExtractEntity> findDetails();

	

}
