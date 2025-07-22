package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr2aImpgSezDataParser {
	public Pair<List<GetGstr2aStagingImpgSezHeaderEntity>, Set<String>> parseImpgSezData(Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now);

}
