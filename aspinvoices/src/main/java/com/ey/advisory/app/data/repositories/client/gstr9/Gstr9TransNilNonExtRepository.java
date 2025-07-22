package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9TransNilNonExtEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr9TransNilNonExtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9TransNilNonExtRepository
		extends JpaRepository<Gstr9TransNilNonExtEntity, Long> {

	@Query("SELECT distinct chunkId FROM Gstr9TransNilNonExtEntity WHERE "
			+ "sgstin=:sgstin AND gstr9Fy=:gstr9Fy "
			+ "AND isDelete = :isDelete")
	public List<Long> getDistinctChunkIds(@Param("sgstin") String sgstin,
			@Param("gstr9Fy") String gstr9Fy,
			@Param("isDelete") boolean isDelete);

	public List<Gstr9TransNilNonExtEntity> findBySgstinAndGstr9FyAndIsDeleteAndChunkId(
			String sgstin, String gstr9Fy, boolean isDelete, Long chunkId);
}
