/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.core.async.domain.master.EYRegularConfig;

@Repository("EYConfigRepository")
@Transactional
public interface EYConfigRepository extends JpaRepository<EYRegularConfig, Long> {

	public EYRegularConfig findByCategoryAndKey(String category, String key);

	public EYRegularConfig findByCategoryAndKeyAndGroupCode(String category, String key, String groupCode);

	public List<EYRegularConfig> findAll();

	@Query("from EYRegularConfig config where config.category = :category and "
			+ "config.key like :keyStartsWith% and groupCode =:groupCode")
	public List<EYRegularConfig> findEWBConfigsByKeyPatternAndGroupCode(@Param(value = "category") String category,
			@Param(value = "keyStartsWith") String keyStartsWith, @Param(value = "groupCode") String groupCode);

	@Query("SELECT ey.value FROM EYRegularConfig ey WHERE ey.key = :key")
	String findNextfinanYearEnd(@Param("key") String key);

	@Query("SELECT ey.value FROM EYRegularConfig ey WHERE ey.key ='einv.address.suppresreq' and ey.groupCode =:groupCode")
	String getSuppressValueByGroupKeyAndCode(@Param("groupCode") String groupCode);

	public EYRegularConfig findFirstByOrderByIdDesc();

	@Modifying
	@Query("UPDATE EYRegularConfig SET value = :date WHERE key = :key")
	public void updateDateValue(@Param("key") String key,
			@Param("date") String date);
	
	@Modifying
	@Query("UPDATE EYRegularConfig SET value = :value WHERE key = :key")
	public void updateValueOnKey(@Param("key") String key,
			@Param("value") String value);

}
