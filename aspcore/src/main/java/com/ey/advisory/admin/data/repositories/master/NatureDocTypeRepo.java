package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("NatureDocTypeRepo")
public interface NatureDocTypeRepo
		extends JpaRepository<NatureOfDocEntity, Long>,
		                        JpaSpecificationExecutor<NatureOfDocEntity> {
	
	@Query("SELECT g FROM NatureOfDocEntity g")
    List<NatureOfDocEntity> findAll();
	
	
	@Query("SELECT count(g.natureDocType) FROM NatureOfDocEntity g"
			+ " WHERE g.natureDocType = :docType")
	public int findDocType(@Param("docType") String docType);
	
	
	@Query("SELECT g FROM NatureOfDocEntity g WHERE  "
			+ "g.id = :intSerial")
	public NatureOfDocEntity findNatureDocType(@Param("intSerial") 
	                                                  Long intSerial);

}
