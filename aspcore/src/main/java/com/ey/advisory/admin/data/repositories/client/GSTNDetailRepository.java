package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;

import jakarta.transaction.Transactional;

/**
 * @author Umesha.M
 *
 */
@Repository("GSTNDetailRepository")
public interface GSTNDetailRepository
		extends JpaRepository<GSTNDetailEntity, Long>,
		JpaSpecificationExecutor<GSTNDetailEntity> {

	/**
	 * @param id
	 */
	@Modifying
	@Transactional
	@Query("UPDATE GSTNDetailEntity SET isDelete =true WHERE id = :id")
	public void deleterecord(@Param("id") Long id);

	/**
	 * @return
	 */
	@Query("SELECT entity FROM GSTNDetailEntity entity "
			+ "WHERE entity.isDelete=false")
	public List<GSTNDetailEntity> findDetails();

	public GSTNDetailEntity findByGstinAndIsDeleteFalse(
			@Param("gstin") String gstin);

	@Query("SELECT entity.entityId, entity.gstin FROM GSTNDetailEntity entity "
			+ "WHERE entity.gstin IN :gstins AND entity.isDelete=false "
			+ "ORDER BY entity.entityId, entity.gstin")
	public List<Object[]> findByGstinIn(@Param("gstins") List<String> gstins);

	/**
	 * @param gstin
	 * @return
	 */
	@Query("SELECT g FROM GSTNDetailEntity g WHERE g.gstin = :gstin "
			+ "AND g.isDelete=false")
	public List<GSTNDetailEntity> findByGstin(@Param("gstin") String gstin);

	/**
	 * @param gstin
	 * @return
	 */
	@Query("SELECT count(g.gstin) FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND g.isDelete=false")
	public int findgstin(@Param("gstin") String gstin);

	/**
	 * @param s
	 * @return
	 */
	@Query("SELECT count(g.stateCode) FROM GSTNDetailEntity g"
			+ " WHERE g.stateCode = :s AND g.isDelete=false")
	public int findStateCode(@Param("s") String s);

	/**
	 * @return
	 */
	@Query("select g.gstin from GSTNDetailEntity g WHERE g.isDelete=false")
	public List<String> findAllGstin();

	/**
	 * @param entityIds
	 * @return
	 */
	@Query("SELECT distinct g FROM GSTNDetailEntity g WHERE  "
			+ " g.entityId IN (:entityIds) AND g.isDelete=false")
	List<GSTNDetailEntity> findByEntityIds(
			@Param("entityIds") List<Long> entityIds);

	/**
	 * @param entityIds
	 * @return
	 */
	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId IN (:entityIds) AND g.isDelete=false")
	public List<String> findByEntityId(
			@Param("entityIds") List<Long> entityIds);

	/**
	 * @param sgstin
	 * @return
	 */
	@Query("SELECT g.entityId FROM GSTNDetailEntity g WHERE g.gstin=:sgstin "
			+ "AND g.isDelete=false")
	public Long findEntityIdByGstin(@Param("sgstin") String sgstin);

	/**
	 * @param sgstin
	 * @return
	 */

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in "
			+ "(:sgstin) AND g.registrationType IN ('REGULAR','SEZ','SEZU','SEZD') "
			+ "AND g.isDelete=false ")
	public List<String> filterGstinBasedOnRegType(
			@Param("sgstin") List<String> sgstin);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in (:sgstin) "
			+ "AND g.registrationType= 'ISD' " + "AND g.isDelete=false ")
	public List<String> getGstinRegTypeISD(
			@Param("sgstin") List<String> sgstin);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in (:sgstin) "
			+ "AND g.registrationType= 'TDS' " + "AND g.isDelete=false ")
	public List<String> getGstinRegTypeTDS(
			@Param("sgstin") List<String> sgstin);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in (:sgstin)"
			+ " AND registrationType in (:regType) " + "AND g.isDelete=false ")
	public List<String> filterGstinBasedByRegType(
			@Param("sgstin") List<String> sgstin,
			@Param("regType") List<String> regType);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in"
			+ " (:sgstin) AND registrationType in (:regType)"
			+ "AND g.isDelete=false order by g.gstin asc")
	public List<String> filterTdsGstinBasedByRegType(
			@Param("sgstin") List<String> sgstin,
			@Param("regType") List<String> regType);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in"
			+ " (:sgstin) AND registrationType in (:regType)"
			+ "AND g.isDelete=false order by g.gstin asc")
	public List<String> filterTcsGstinBasedByRegType(
			@Param("sgstin") List<String> sgstin,
			@Param("regType") List<String> regType);
	
	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in (:sgstin)"
			+ " AND g.registrationType <> :regTypeListTds AND"
			+ " g.registrationType <> :regTypeListIsd"
			+ " AND g.isDelete=false ")
	public List<String> filterTDSISDGstinBasedByRegType(
			@Param("sgstin") List<String> sgstin,
			@Param("regTypeListTds") List<String> regTypeListTds,
			@Param("regTypeListIsd") List<String> regTypeListIsd);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.gstin in (:sgstin)"
			+ " AND g.registrationType NOT IN (:regTypeListTds) "
			+ " AND g.isDelete=false ")
	public List<String> filter2aTDSISDGstinBasedByRegType(
			@Param("sgstin") List<String> sgstin,
			@Param("regTypeListTds") List<String> regTypeListTds);

	/**
	 * @param groupCode
	 * @return
	 */

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId AND g.isDelete=false order by g.gstin asc")
	public List<String> findgstinByEntityId(@Param("entityId") Long entityId);
	
	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
	        + "WHERE g.entityId IN :entityIds AND g.isDelete = false order by g.gstin asc")
	public List<String> findgstinByEntityIds(@Param("entityIds") List<Long> entityIds);


	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId = :entityId AND g.registrationType ="
			+ ":registrationType AND g.isDelete=false order by g.gstin asc")
	public List<String> findgstinByEntityIdAndRegistrationType(
			@Param("entityId") Long entityId,
			@Param("registrationType") String registrationType);
	
	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
	        + "WHERE g.entityId = :entityId AND g.registrationType IN :registrationType "
	        + "AND g.isDelete = false ORDER BY g.gstin ASC")
	public List<String> findgstinByEntityIdAndRegistrationTypes(
	        @Param("entityId") Long entityId,
	        @Param("registrationType") List<String> registrationType);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.groupCode IN (:groupCode) AND g.isDelete=false")
	public List<String> findByGroupCode(@Param("groupCode") String groupCode);

	@Query("SELECT g FROM GSTNDetailEntity g "
			+ "WHERE g.groupCode IN (:groupCode) AND g.isDelete=false")
	public List<GSTNDetailEntity> findAllByGroupCode(
			@Param("groupCode") String groupCode);

	/**
	 * @param groupCode
	 * @param entityId
	 * @return
	 */
	@Query("SELECT g FROM GSTNDetailEntity g"
			+ " WHERE g.groupCode = :groupCode "
			+ "AND g.entityId=:entityId AND g.isDelete=false")
	List<GSTNDetailEntity> findByGstnAll(@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId);

	/**
	 * @param groupCode
	 * @param entityId
	 * @return
	 */
	@Query("SELECT g FROM GSTNDetailEntity g  WHERE g.groupCode = :groupCode "
			+ "AND g.entityId=:entityId AND g.gstin=:gstin AND g.isDelete=false")
	GSTNDetailEntity findByGroupAndGstnAll(@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("gstin") String gstin);

	/*
	 *
	 * findIdByGstinAndGroupCode
	 */
	@Query("SELECT g.id FROM GSTNDetailEntity g WHERE g.gstin=:sgstin "
			+ "and g.groupCode = :groupCode AND g.isDelete=false")
	public Long findIdByGstinAndGroupCode(@Param("sgstin") String sgstin,
			@Param("groupCode") String groupCode);

	@Query("SELECT g.registrationType FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND registrationType <> 'ISD'")
	public List<String> findRegTypeByGstin(@Param("gstin") String gstin);

	/**
	 * For Gstr2 PR Summary
	 * 
	 * @param gstin
	 * @return
	 */

	@Query("SELECT g.registrationType FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND registrationType <> 'TDS'")
	public List<String> findRegTypeByGstinForGstr2PR(
			@Param("gstin") String gstin);

	@Query("SELECT g.registrationType FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin")
	public List<String> findgstr2avs3bRegTypeByGstin(
			@Param("gstin") String gstin);

	@Query("SELECT g FROM GSTNDetailEntity g" + " WHERE g.gstin = :gstin "
			+ "AND g.isDelete=false")
	public GSTNDetailEntity findRegDates(@Param("gstin") String gstin);

	@Modifying
	@Transactional
	@Query("update GSTNDetailEntity e set e.isDelete=true WHERE "
			+ "e.entityId=:entityId AND e.groupCode = :groupCode AND"
			+ " e.gstin IN :gstin")
	public void updateAll(@Param("gstin") final List<String> gstins,
			@Param("entityId") final Long entityId,
			@Param("groupCode") final String groupCode);

	@Query("SELECT g.regDate from GSTNDetailEntity g WHERE g.gstin = :gstin "
			+ "AND g.isDelete=false")
	public LocalDate findRegistraionDate(@Param("gstin") String gstin);

	@Query("SELECT g.gstin from GSTNDetailEntity g WHERE g.entityId "
			+ "in :entityIds " + "AND g.isDelete=false " + "ORDER BY g.gstin")
	public List<String> findGstinByEntityIds(
			@Param("entityIds") List<Long> entityIds);

	@Query("SELECT g.id, g.gstin FROM GSTNDetailEntity g WHERE "
			+ "g.entityId=:entityId AND g.isDelete=false")
	public List<Object[]> getGsinIdName(@Param("entityId") Long entityId);

	@Query("select g.gstin from GSTNDetailEntity g where g.gstin in :gstnsList "
			+ "and g.registrationType in ('REGULAR','SEZ')")
	public List<String> getRegularandSezGstins(
			@Param("gstnsList") List<String> gstnsList);

	@Query("select distinct g.gstin from GSTNDetailEntity g where"
			+ " g.registrationType " + "in ('ISD') AND g.isDelete=false")
	public List<String> getActiveISDGstins();

	@Query("select distinct g.gstin from GSTNDetailEntity g where "
			+ "g.registrationType "
			+ "in ('REGULAR','SEZ') AND g.isDelete=false")
	public List<String> getActiveRegularSEZGstins();

	@Query("select distinct g.gstin from GSTNDetailEntity g where "
			+ "g.registrationType "
			+ "in ('REGULAR','SEZ','SEZU','ISD','SEZD') AND g.isDelete=false")
	public List<String> getActiveGstins();
	
	@Query("select distinct g.gstin from GSTNDetailEntity g where "
			+ "g.registrationType "
			+ "in ('REGULAR','SEZ','SEZU','ISD','SEZD') AND g.isDelete=false")
	public List<String> getActiveGstin(@Param("gstin") String gstin);
	
	@Query("select distinct g.gstin from GSTNDetailEntity g where "
			+ "g.registrationType "
			+ "in ('REGULAR','SEZ','SEZU','SEZD') AND g.isDelete=false")
	public List<String> getGstr1Gstr3bActiveGstns();

	@Query("SELECT g.id FROM GSTNDetailEntity g WHERE g.gstin=:sgstin "
			+ "AND g.isDelete=false")
	public Long findIdByGstin(@Param("sgstin") String sgstin);

	@Query("SELECT e.entityName,e.pan,e.companyHq,g.entityId FROM GSTNDetailEntity g "
			+ "INNER JOIN EntityInfoEntity e "
			+ "ON g.entityId=e.id WHERE g.gstin=:sgstin AND g.isDelete=false "
			+ "AND e.isDelete=false ")
	public List<Object[]> getEntityNameAndEntityPan(
			@Param("sgstin") String sgstin);

	/*
	 * @Query("SELECT g FROM GSTNDetailEntity g" +
	 * " WHERE g.docHeaderId = :docHeaderId " + "AND g.isDelete=false") public
	 * GSTNDetailEntity findbyHeaderId(@Param("docHeaderId") String
	 * docHeaderId);
	 */

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ " WHERE g.entityId=:entityId AND g.isDelete=false "
			+ " AND g.registrationType IN ('TDS') ")
	public List<String> findgstinByEntityIdWithRegTypeForGstr7(
			@Param("entityId") Long entityId);

	/*
	 * @Query(
	 * value="select GSTIN from GSTIN_INFO where ENTITY_ID=:entityId AND " +
	 * "IS_DELETE=false AND REG_TYPE in ('TDS') ",nativeQuery=true) public
	 * List<String> findgstinByEntityIdWithRegTypeForGstr7(
	 * 
	 * @Param("entityId") Long entityId);
	 */

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ " WHERE g.entityId=:entityId AND g.isDelete=false "
			+ " AND g.registrationType IN ('REGULAR','SEZU','SEZD','SEZ') ")
	public List<String> findgstinByEntityIdWithRegTypeForGstr1(
			@Param("entityId") Long entityId);
	
	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
	        + " WHERE g.entityId IN :entityIds AND g.isDelete=false "
	        + " AND g.registrationType IN ('REGULAR','SEZU','SEZD','SEZ') ")
	public List<String> findgstinByEntityIdWithRegTypeForGstr1(
	        @Param("entityIds") List<Long> entityIds);
	
	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
	        + "WHERE g.entityId IN :entityIds AND g.isDelete = false "
	        + "AND g.registrationType IN ('REGULAR','SEZU','SEZD','SEZ') ")
	public List<String> findgstinByEntityIdsWithRegTypeForGstr1(
	        @Param("entityIds") List<Long> entityIds);


	@Query("SELECT g.id FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId AND g.isDelete=false ")
	public List<Long> findgstinIdByEntityId(@Param("entityId") Long entityId);

	@Query("SELECT g FROM GSTNDetailEntity g WHERE g.entityId IN (:entities) "
			+ "AND g.isDelete=false ")
	public List<GSTNDetailEntity> getGstinByEntites(
			@Param("entities") List<Long> entities);

	@Query("SELECT gstin FROM GSTNDetailEntity WHERE id=:id AND isDelete=false")
	public String getGstinById(@Param("id") Long id);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId AND g.isDelete=false "
			+ "AND g.registrationType <> 'ISD'")
	public List<String> findgstinByEntityIdWithoutISD(
			@Param("entityId") Long entityId);

	/**
	 * @param groupCode
	 * @return
	 */

	@Query("SELECT g.id FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId AND g.isDelete=false")
	public List<Long> findidByEntityId(@Param("entityId") Long entityId);

	@Query(" FROM GSTNDetailEntity g  WHERE g.gstin in (:gstins)")
	public List<GSTNDetailEntity> findRegTypeByGstinList(
			@Param("gstins") List<String> gstins);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId IN (:entityId) AND g.isDelete=false "
			+ "AND g.registrationType  IN ('ISD') ORDER BY g.gstin ")
	public List<String> findgstinByEntityIdWithISD(
			@Param("entityId") List<Long> entityId);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId IN (:entityId) AND g.isDelete=false "
			+ "AND g.registrationType  IN ('REGULAR','SEZ','SEZU','SEZD') ORDER BY g.gstin ")
	public List<String> findgstinByEntityIdWithOutISD(
			@Param("entityId") List<Long> entityId);

	@Query("SELECT MAX(g.registrationType) FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND registrationType = 'ISD'")
	public String findIsdRegTypeByGstin(@Param("gstin") String gstin);

	@Query("SELECT g.registrationType FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND registrationType "
			+ "NOT IN ('ISD','TDS') AND g.isDelete=false ")
	public List<String> findRegTypeByGstinNotInIsdTds(
			@Param("gstin") String gstin);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId = :entityId "
			+ "AND g.registrationType  IN ('REGULAR','SEZ','SEZU','SEZD') ")
	public List<String> findRegGstinByEntityId(
			@Param("entityId") Long entityId);

	@Query("select g from GSTNDetailEntity g where g.gstin in :gstnsList "
			+ "and g.registrationType in ('REGULAR','SEZD') and g.isDelete=false "
			+ "order by g.gstin")
	public List<GSTNDetailEntity> getRegandSezGstins(
			@Param("gstnsList") List<String> gstnsList);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ " WHERE g.entityId=:entityId AND g.isDelete=false "
			+ " AND g.registrationType IN ('REGULAR','SEZU','SEZD') ")
	public List<String> findgstinByEntityIdWithRegTypeForItc04(
			@Param("entityId") Long entityId);

	@Query("select g.gstin FROM GSTNDetailEntity g WHERE g.gstin in :gstnsList "
			+ "AND g.isDelete=false AND g.registrationType IN ('REGULAR','SEZ','ISD')")
	public List<String> findgstinByEntityIdWitISD(
			@Param("gstnsList") List<String> gstnsList);

	// For PDF I am using this method

	@Query("SELECT g.registeredName FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin AND g.isDelete=false ")
	public String findRegTypeByGstinForPdf(@Param("gstin") String gstin);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.entityId=:entityId"
			+ " AND g.registrationType IN ('REGULAR','SEZ','SEZU','SEZD') "
			+ "AND g.isDelete=false ")
	public List<String> filterGstinBasedOnRegTypeforACD(
			@Param("entityId") Long entityId);

	@Query("SELECT g.gstin,g.registrationType FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId"
			+ " AND g.registrationType in (:registrationTypes) "
			+ "AND g.isDelete=false ")
	public List<Object[]> getGstinBasedOnRegTypeforACD(
			@Param("entityId") Long entityId,
			@Param("registrationTypes") List<String> registrationTypes);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g WHERE g.groupCode=:groupCode"
			+ " AND g.registrationType IN ('REGULAR','SEZ','SEZU','SEZD', 'ISD')"
			+ " AND g.isDelete=false ")
	public List<String> filterGstinBasedOnRegTypeAndgroupCode(
			@Param("groupCode") String groupCode);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.entityId=:entityId"
			+ " AND g.registrationType in (:registrationTypes) "
			+ "AND g.isDelete=false ")
	public List<String> getGstinBasedOnRegType(@Param("entityId") Long entityId,
			@Param("registrationTypes") List<String> registrationTypes);

	@Query("SELECT g.registrationType FROM GSTNDetailEntity g"
			+ " WHERE g.gstin = :gstin ")
	public String findRegTypeForUserGstin(@Param("gstin") String gstin);
	
	@Modifying
	@Transactional
	@Query("UPDATE GSTNDetailEntity e SET e.legalName = :legalName, e.tradeName = :tradeName WHERE "
	        + "e.gstin = :gstin AND e.entityId = :entityId AND e.groupCode = :groupCode ")
	public void updateLegalAndTradeName(
	        @Param("legalName") String legalName,
	        @Param("tradeName") String tradeName,
	        @Param("gstin") String gstin,
	        @Param("entityId") Long entityId,
	        @Param("groupCode") String groupCode);

	@Query("SELECT g.gstin FROM GSTNDetailEntity g "
			+ "WHERE g.registrationType in (:registrationTypes) "
			+ "AND g.isDelete=false ")
	public List<String> getGstinBasedOnRegistraionType(
			@Param("registrationTypes") List<String> registrationTypes);
	
	
	public List<GSTNDetailEntity> findByGstinInAndIsDeleteFalse(
			@Param("gstin") List<String> gstin);
	
}
