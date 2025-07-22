package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.core.async.domain.master.EYSensitiveConfig;

@Repository
@Transactional
public interface EYSensitiveConfigRepository
		extends JpaRepository<EYSensitiveConfig, Long> {

	public EYSensitiveConfig findByCategoryAndKey(String category, String key);

	public EYSensitiveConfig findByCategoryAndKeyAndGroupCode(String category,
			String key, String groupCode);

	public List<EYSensitiveConfig> findAll();

	@Query("from EYSensitiveConfig config where "
			+ "config.category = :category and "
			+ "config.key like :keyStartsWith% and groupCode =:groupCode")
	public List<EYSensitiveConfig> findEWBConfigsByKeyPatternAndGroupCode(
			@Param(value = "category") String category,
			@Param(value = "keyStartsWith") String keyStartsWith,
			@Param(value = "groupCode") String groupCode);
}
