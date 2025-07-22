/**
 * 
 */
package com.ey.advisory.gstnapi.repositories.master;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.gstnapi.domain.master.NICError;

/**
 * @author Sachindra.S
 *
 */
@Repository
@Transactional
public interface NICErrorRepository extends JpaRepository<NICError, Long> {

	public NICError findByErrCategoryAndErrCode(String errCategory,
			Integer errCode);

}
