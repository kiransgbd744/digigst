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
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;

/**
 * @author Sai.Pakanati
 *
 */
@Repository
@Transactional
public interface EINVNICUserRepository
		extends JpaRepository<EINVNICUser, Long> {

	public EINVNICUser findByGstinAndIdentifier(String gstin,
			String identifier);
	
	public EINVNICUser findByGstin(String gstin);

	@Modifying
	@Query("UPDATE EINVNICUser einvuser set einvuser.nicUserName = :userName,"
			+ "einvuser.nicPassword = :password, einvuser.updatedOn = CURRENT_TIMESTAMP,"
			+ "einvuser.updatedBy = :updatedBy where einvuser.gstin = :gstin")
	public int updateEINVNICUser(@Param("gstin") String gstin,
			@Param("userName") String userName,
			@Param("password") String password,
			@Param("updatedBy") String updatedBy);

	@Query("select distinct new com.ey.advisory.common.Gstindto(einvuser.gstin) from EINVNICUser einvuser")
	public List<Gstindto> getDistinctGstin();

	public List<EINVNICUser> findByGstinIn(List<String> gstin);
}
