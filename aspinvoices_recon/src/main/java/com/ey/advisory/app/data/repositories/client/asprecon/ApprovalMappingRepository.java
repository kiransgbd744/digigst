/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ApprovalMappingEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("ApprovalWorkflowRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ApprovalMappingRepository
		extends JpaRepository<ApprovalMappingEntity, Long>,
		JpaSpecificationExecutor<ApprovalMappingEntity> {
	
	@Query("SELECT prt.gstin,chd.name,chd.email,chd.isMaker "
			+ "FROM ApprovalMappingEntity prt INNER JOIN ApprovalPreferenceChildEntity chd "
			+ "ON prt.id = chd.mappingId AND prt.isDelete=FALSE "
			+ "AND prt.entityId =:entityId " + "AND prt.retType=:retType ")
	List<Object[]> findByEntityIdAndIsDeleteFalseANDRetType(
			@Param("entityId") Long entityId,
			@Param("retType") String retType);
	
	List<ApprovalMappingEntity> findByEntityIdAndIsDeleteFalseAndRetType(
			Long entityId, String retType);

	@Query("select id from ApprovalMappingEntity "
			+ "where entityId = :entityId")
	public List<Long> getWorkflowIds(@Param("entityId") Long entiyIdId);

	@Modifying
	@Query("UPDATE ApprovalMappingEntity SET isDelete=true, modifiedBy =:userName, modifiedOn =CURRENT_TIMESTAMP WHERE entityId =:entityId AND "
			+ "gstin =:gstin AND retType =:retType AND isDelete=false")
	public void softDeleteExistingEntries(@Param("entityId") Long entityId,
			@Param("gstin") String gstin, @Param("userName") String userName,
			@Param("retType") String retType);

	@Query("SELECT prt.gstin,chd.name,chd.email "
			+ "FROM ApprovalMappingEntity prt INNER JOIN ApprovalPreferenceChildEntity chd "
			+ "ON prt.id = chd.mappingId AND prt.isDelete=FALSE "
			+ "AND prt.entityId =:entityId " + "AND prt.retType=:retType "+"AND prt.gstin IN (:gstins) "
			+ "AND chd.isMaker=FALSE " + "AND prt.id IN "
			+ "( SELECT chd1.mappingId "
			+ "FROM ApprovalPreferenceChildEntity chd1 "
			+ "WHERE chd1.name =:name " + "AND chd1.isMaker=TRUE ) ")
	List<Object[]> findByEntityIdANDNameANDRetType(
			@Param("entityId") Long entityId, @Param("name") String name,
			@Param("gstins") List<String> gstins,
			@Param("retType") String retType);
	
	@Query("SELECT prt.gstin "
			+ "FROM ApprovalMappingEntity prt INNER JOIN ApprovalPreferenceChildEntity chd "
			+ "ON prt.id = chd.mappingId AND prt.isDelete=FALSE "
			+ "AND prt.entityId =:entityId "
			+ "AND chd.isMaker=FALSE AND "
			+ "chd.name =:name ")
	public List<String> findCheckerGstins(@Param("entityId") Long entityId,
			@Param("name") String name);
	
	@Query("SELECT chd.isMaker "
			+ "FROM ApprovalMappingEntity prt INNER JOIN ApprovalPreferenceChildEntity chd "
			+ "ON prt.id = chd.mappingId AND prt.isDelete=FALSE "
			+ "AND prt.entityId =:entityId "
			+ "AND prt.gstin =:gstin AND prt.retType =:retType AND chd.name =:name ")
	public Boolean findisMakerCheckerNames(@Param("entityId") Long entityId,
			@Param("gstin") String gstin,
			@Param("retType") String retType,
			@Param("name") String name);

}
