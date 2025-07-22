package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;

@Component("GstinElRegRepository")
public interface GstinElRegRepository extends 
                              JpaRepository<GSTNDetailEntity,Long> ,
                              JpaSpecificationExecutor<GSTNDetailEntity>{

	
	@Query("SELECT e.id FROM GSTNDetailEntity e WHERE e.gstin =:gstin AND e.entityId = :entityId AND isDelete = false")
	public List<Long> getByGstin(@Param("gstin") String gstin, @Param("entityId") Long entityId);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.gstin=:gstin")
	public int gstincount(
			@Param("gstin") String gstin);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.gstnUsername=:gstnUsername")
	public int gstnUsernamecount(
			@Param("gstnUsername") String gstnUsername);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.registeredEmail=:registeredEmail")
	public int registeredEmailcount(
			@Param("registeredEmail") String registeredEmail);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.registeredMobileNo=:registeredMobileNo")
	public int registeredMobileNocount(
			@Param("registeredMobileNo") String registeredMobileNo);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.primaryAuthEmail=:primaryAuthEmail")
	public int primaryAuthEmailNocount(
			@Param("primaryAuthEmail") String primaryAuthEmail);
	
	@Query("SELECT count(e) FROM GSTNDetailEntity e WHERE "
			+ "e.primaryContactEmail=:primaryContactEmail")
	public int primaryContactEmailcount(
			@Param("primaryContactEmail") String primaryContactEmail);
	
}
