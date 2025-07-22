package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("UserCreationRepository")
public interface UserCreationRepository
		extends JpaRepository<UserCreationEntity, Long>,
		JpaSpecificationExecutor<UserCreationEntity> {

	/**
	 * @param id
	 */
	@Modifying
	@Transactional
	@Query("UPDATE UserCreationEntity SET isFlag =true WHERE id = :id")
	public void deleterecord(@Param("id") Long id);

	/**
	 * @return
	 */
	@Query("SELECT entity FROM UserCreationEntity entity "
			+ "WHERE entity.isFlag=false")
	public List<UserCreationEntity> findDetails();
	


	@Query("SELECT entity FROM ELEntitlementEntity entity WHERE "
			+ "entity.groupCode=:groupCode "
			+ "AND entity.entityId=:entityId AND entity.isDelete=false")
	public List<ELEntitlementEntity> findAllEntitlementdetails(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId);

	@Query("SELECT id FROM UserCreationEntity entity "
			+ "WHERE entity.userName = :usrPrnplName")
	public Long findIdByUserName(@Param("usrPrnplName") String usrPrnplName);

	@Query("SELECT id FROM UserCreationEntity entity "
			+ "WHERE entity.userName = :usrPrnplName OR entity.email=:email")
	public Long findIdByUserNameAndEmail(
			@Param("usrPrnplName") String usrPrnplName,
			@Param("email") String email);

	@Query("SELECT entity FROM UserCreationEntity entity "
			+ "WHERE entity.userName = :userName AND entity.email = :email")
	public UserCreationEntity findUserEntityByUserNameAndEmail(
			@Param("userName") String userName, @Param("email") String email);

	@Query("SELECT entity FROM UserCreationEntity entity "
			+ "WHERE entity.isFlag=false AND entity.email = :email")
	public UserCreationEntity findUserEntityByEmail(
			@Param("email") String email);

	@Query("SELECT entity.id FROM UserCreationEntity entity "
			+ "WHERE entity.isFlag=false AND entity.userName = :userName AND "
			+ "entity.groupCode =:groupCode ")
	public List<Long> findUserIdByUserName(@Param("userName") String userName,
			@Param("groupCode") String groupCode);

	@Query("SELECT count(e) FROM UserCreationEntity e WHERE "
			+ "e.userName=:userName")
	public int userNamecount(@Param("userName") String userName);

	@Query("SELECT e FROM UserCreationEntity e WHERE "
			+ "e.email=:email AND e.groupCode =:groupCode")
	public UserCreationEntity emailcount(@Param("email") String email,
			@Param("groupCode") String groupCode);

	@Modifying
	@Transactional
	@Query("UPDATE UserCreationEntity SET isFlag =true WHERE id = :id")
	public void disableUserDetails(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE UserCreationEntity SET itpUserName = :itpUserName,"
			+ "modifiedBy = 'ITP',modifiedOn = CURRENT_TIMESTAMP WHERE "
			+ "userName = :userName and isFlag = false")
	public int updateITPUserName(@Param("itpUserName") String itpUserName,
			@Param("userName") String userName);

	@Query("SELECT entity FROM UserCreationEntity entity "
			+ "WHERE entity.isFlag=false AND entity.itpUserName = :itpUserName")
	public UserCreationEntity findUserEntityByItpUserName(
			@Param("itpUserName") String itpUserName);

	@Query("SELECT entity FROM UserCreationEntity entity "
			+ "WHERE entity.isFlag=false AND entity.userName = :userName")
	public UserCreationEntity findUserEntityByUserName(
			@Param("userName") String userName);

	@Query("SELECT e.id, e.userName FROM UserCreationEntity e WHERE e.id in (:ids)")
	public List<Object[]> findUserNamesAsId(@Param("ids") List<Long> ids);

	@Query("SELECT e.email FROM UserCreationEntity e"
			+ " WHERE e.userName = :userName")
	public List<String> findEmailByUser(@Param("userName") String userName);

	@Query("SELECT DISTINCT(e.userName) FROM UserCreationEntity e")
	public List<String> getUserNames();

	@Query("SELECT DISTINCT(e.email) FROM UserCreationEntity e")
	public List<String> getEmailIds();

	@Query("SELECT entity FROM UserCreationEntity entity")
	public List<UserCreationEntity> findAll();

	@Query("SELECT userName, email FROM UserCreationEntity WHERE id IN ( SELECT userId"
			+ " FROM EntityUserMapping WHERE entityId =:entityId AND isFlag=false) ")
	public List<Object[]> getUserInfoByEntityId(
			@Param("entityId") Long entityId);
	
	@Query("SELECT u FROM UserCreationEntity u WHERE u.isFlag = false") 
	public List<UserCreationEntity> getActiveUsers();

}
