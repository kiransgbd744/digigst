/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.GSTNAPICountConfig;

@Repository("GSTNAPICountConfigRepository")
@Transactional(value = "masterTransactionManager")
public interface GSTNAPICountConfigRepository
		extends JpaRepository<GSTNAPICountConfig, Long> {

	public GSTNAPICountConfig findByGroupCodeAndSummaryDate(String groupCode,
			LocalDate summaryDate);
}
