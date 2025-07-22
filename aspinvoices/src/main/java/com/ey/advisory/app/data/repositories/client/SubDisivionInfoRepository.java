package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.SubDivisionInfoEntity;
@Repository("SubDisivionInfoRepository")
public interface SubDisivionInfoRepository extends 
JpaRepository<SubDivisionInfoEntity, Long> , 
JpaSpecificationExecutor<SubDivisionInfoEntity>{

	
	
	@Query("SELECT count(g.division) FROM SubDivisionInfoEntity g"
			+ " WHERE g.division = :s")
	public int findDivision(@Param("s") String s);
	
	@Query("SELECT count(g.subDivision) FROM SubDivisionInfoEntity g"
			+ " WHERE g.subDivision = :s")
	public int findSubDivision(@Param("s") String s);
}
