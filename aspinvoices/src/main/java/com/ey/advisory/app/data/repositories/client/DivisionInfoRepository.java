package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.DivisionInfoEntity;
@Repository("DivisionInfoRepository")
public interface DivisionInfoRepository extends 
JpaRepository<DivisionInfoEntity, Long> , 
JpaSpecificationExecutor<DivisionInfoEntity>{

	@Query("SELECT count(g.subDivision) FROM DivisionInfoEntity g"
			+ " WHERE g.subDivision = :s")
	int findSubDivision(@Param("s") String s);

}
