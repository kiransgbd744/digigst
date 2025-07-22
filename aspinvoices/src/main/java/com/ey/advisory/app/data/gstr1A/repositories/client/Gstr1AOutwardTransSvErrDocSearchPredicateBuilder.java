package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AOutwardTransSvErrDocSearchPredicateBuilder {

	public abstract List<Predicate> build(DocSearchReqDto criteria,
			Root<Gstr1AAnx1OutWardErrHeader> root,
			CriteriaBuilder criteriaBuilder);

}
