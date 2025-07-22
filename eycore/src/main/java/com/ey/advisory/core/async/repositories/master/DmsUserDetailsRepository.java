/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.DmsUserDetails;

/**
 * @author vishal.verma
 *
 */

@Repository("DmsUserDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DmsUserDetailsRepository extends CrudRepository<DmsUserDetails, Long> {

	Optional<DmsUserDetails> findByGroupCode(String groupCode);

	@Transactional
	@Modifying
	@Query("UPDATE DmsUserDetails d SET d.userName = :userName, "
	        + "d.password = :password, "
	        + "d.jwtToken = NULL, "
	        + "d.tokenGenTime = NULL, "
	        + "d.tokenExpTime = NULL, "
	        + "d.modifiedOn = :modifiedOn "
	        + "WHERE d.groupCode = :groupCode")
	void updateUserDetails(@Param("userName") String userName, 
	                       @Param("password") String password, 
	                       @Param("modifiedOn") LocalDateTime modifiedOn, 
	                       @Param("groupCode") String groupCode);



}
