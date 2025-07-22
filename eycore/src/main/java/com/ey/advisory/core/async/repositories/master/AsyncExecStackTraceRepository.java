package com.ey.advisory.core.async.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.AsyncExecStackTrace;

@Repository("asyncExecStackTraceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AsyncExecStackTraceRepository extends 
			JpaRepository<AsyncExecStackTrace, Long>,
			JpaSpecificationExecutor<AsyncExecJob> {
}
