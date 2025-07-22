package com.ey.advisory.core.async.repositories.master;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.FeedbackUserConfigPrmtEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("FeedbackUserConfigPrmtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface FeedbackUserConfigPrmtRepository
		extends CrudRepository<FeedbackUserConfigPrmtEntity, Long> {

	List<FeedbackUserConfigPrmtEntity> findByUserNameAndIsDeleteFalse(
			String userName);

	@Query("SELECT e.answer FROM  FeedbackUserConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ "e.userName=:userName AND e.confgPrmtId=:confgPrmtId AND e.isDelete= false")
	String findByGroupCodeAndUserIdAndConfgPrmtId(
			@Param("groupCode") String groupCode,
			@Param("userName") String userName,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT e.filePath FROM  FeedbackUserConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ "e.userName=:userName AND e.confgPrmtId=:confgPrmtId AND e.isDelete= false")
	String getFilePath(@Param("groupCode") String groupCode,
			@Param("userName") String userName,
			@Param("confgPrmtId") Long confgPrmtId);

	Optional<FeedbackUserConfigPrmtEntity> findByGroupCodeAndUserNameAndConfgPrmtIdAndIsDeleteFalse(
			String groupCode, String userName, Long confgPrmtId);

	Optional<FeedbackUserConfigPrmtEntity> findByIdAndIsDeleteFalse(Long id);

	List<FeedbackUserConfigPrmtEntity> findByGroupCodeAndUserNameAndIsDeleteFalse(
			String groupCode, String userName);

	List<FeedbackUserConfigPrmtEntity> findByIsDeleteFalse();

	@Modifying
	@Query("UPDATE FeedbackUserConfigPrmtEntity e SET e.isDelete = true "
			+ "WHERE e.groupCode=:groupCode AND "
			+ "e.userName=:userName AND e.isDelete= false")
	int updateFeedbackDtls(@Param("groupCode") String groupCode,
			@Param("userName") String userName);

}
