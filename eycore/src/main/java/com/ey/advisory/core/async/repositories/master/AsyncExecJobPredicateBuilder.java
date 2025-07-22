package com.ey.advisory.core.async.repositories.master;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.async.JobsSearchParams;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

@Component("EWBJobsPredicateBuilder")
public class AsyncExecJobPredicateBuilder {

	public List<Predicate> buildAsycnExecJobSearchPredicates(
			JobsSearchParams jobSearchParams, Root<AsyncExecJob> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (jobSearchParams == null)
			return predicates;

		// status
		if (!jobSearchParams.getStatus().isEmpty()) {

			if (jobSearchParams.getStatus().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("status"),
						jobSearchParams.getStatus().get(0)));
			} else {
				predicates.add(
						root.get("status").in(jobSearchParams.getStatus()));
			}

		}

		// job start date
		if (jobSearchParams.getJobStartDate() != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(
					root.get("createdDate"),
					jobSearchParams.getJobStartDate()));
		}

		// job end date
		if (jobSearchParams.getJobEndDate() != null) {
			predicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
					(jobSearchParams.getJobEndDate())));
		}

		// category
		if (jobSearchParams.getCategory() != null) {
			if (jobSearchParams.getCategory().equalsIgnoreCase("all")) {
				// nothing
			} else {
				predicates.add(criteriaBuilder.equal(root.get("jobCategory"),
						(jobSearchParams.getCategory())));
			}
		}

		// groupCode
		if (jobSearchParams.getGroupCode() != null) {
			predicates.add(criteriaBuilder.equal(root.get("groupCode"),
					(jobSearchParams.getGroupCode())));
		}

		// userName
		if (jobSearchParams.getUserName() != null) {
			predicates.add(criteriaBuilder.equal(root.get("userName"),
					(jobSearchParams.getUserName())));
		}

		// jobId
		if (jobSearchParams.getJobId() != null) {
			predicates.add(criteriaBuilder.equal(root.get("jobId"),
					(new Long(jobSearchParams.getJobId()))));
		}

		return predicates;
	}

}
