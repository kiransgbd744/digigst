package com.ey.advisory.app.recipientmasterupload;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.recipientmaster.dto.RecipientGstinDto;
import com.ey.advisory.app.recipientmaster.dto.RecipientMasterDataDto;

/**
 * 
 * @author Rajesh N K
 *
 */
public interface RecipientMasterDataService {

	Pair<List<RecipientMasterDataDto>, Integer> listRecipientMasterData(
			List<String> recipientPanList, List<String> recipientGstinsList,
			int pageSize, int pageNum);

	List<RecipientGstinDto> getListOfRecipientGstinList(Long entityId);

}
