/**
 * 
 */
package com.ey.advisory.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.domain.client.PerformanceStatistics;

/**
 * @author Sai.Pakanati
 *
 */
@Repository("PerformanceStstcsRepository")
@Transactional
public interface PerformanceStstcsRepository
		extends JpaRepository<PerformanceStatistics, Long> {

}
