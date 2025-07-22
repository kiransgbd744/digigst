package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.LogoConfigEntity;

import jakarta.transaction.Transactional;

@Repository("LogoConfigRepository")
@Transactional
public interface LogoConfigRepository
		extends CrudRepository<LogoConfigEntity, Long> {

	@Query("SELECT l.entityId,l.logofile,l.logoType FROM "
			+ "LogoConfigEntity l WHERE l.entityId IN (:entityIds) AND l.isDelete=false ")
	public List<Object[]> getLogoConfigDetails(
			@Param("entityIds") List<Long> entityIds);

	@Query("SELECT b FROM LogoConfigEntity b WHERE b.gstinId=:gstinId "
			+ "AND b.isDelete=false ")
	public List<LogoConfigEntity> getLogFileByGstin(
			@Param("gstinId") final Long gstinId);

	@Modifying
	@Query("UPDATE LogoConfigEntity SET isDelete=true WHERE entityId=:entityId "
			+ "AND gstinId=:gstinId AND isDelete=false ")
	public void updateLogoConfigFile(@Param("entityId") Long entityId,
			@Param("gstinId") final Long gstinId);

}
