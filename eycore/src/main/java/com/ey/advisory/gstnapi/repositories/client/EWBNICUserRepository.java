/**
 * 
 */
package com.ey.advisory.gstnapi.repositories.client;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.common.Gstindto;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;

/**
 * @author Sai.Pakanati
 *
 */
@Repository
@Transactional
public interface EWBNICUserRepository extends JpaRepository<EWBNICUser, Long> {

	public EWBNICUser findByGstin(String gstin);

	@Query("select distinct gstin from EWBNICUser")
	public List<String> getDistinctGstins();

	@Modifying
	@Query("UPDATE EWBNICUser ewbuser set ewbuser.nicUserName = :userName,"
			+ "ewbuser.nicPassword = :password, ewbuser.updatedOn = CURRENT_TIMESTAMP,"
			+ "ewbuser.updatedBy = :updatedBy where ewbuser.gstin = :gstin")
	public int updateEWBNICUser(@Param("gstin") String gstin,
			@Param("userName") String userName,
			@Param("password") String password,
			@Param("updatedBy") String updatedBy);

	
	@Query("select distinct new com.ey.advisory.common.Gstindto(ewbuser.gstin) from EWBNICUser ewbuser")
	public List<Gstindto> getGstins();
	
	
	public List<EWBNICUser> findByGstinIn(List<String> gstin);


}
