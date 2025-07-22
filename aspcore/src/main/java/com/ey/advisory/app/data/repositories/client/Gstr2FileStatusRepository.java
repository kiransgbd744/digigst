package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
@Component("Gstr2FileStatusRepository")
public interface Gstr2FileStatusRepository
		extends JpaRepository<Gstr2FileStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr2FileStatusEntity> {

 @Query("FROM Gstr2FileStatusEntity file WHERE file.fileName = :oriFileName ")	 
public Gstr2FileStatusEntity getFileName(@Param("oriFileName") String oriFileName);
 
 @Modifying
	@Query("UPDATE Gstr2FileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus where file.id = :id ")
	public void updateFileStatus(@Param("id") Long id,
			@Param("fileStatus") String fileStatus);

}
