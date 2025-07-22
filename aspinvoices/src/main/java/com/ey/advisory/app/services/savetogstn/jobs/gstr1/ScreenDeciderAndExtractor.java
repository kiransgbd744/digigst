package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.docs.dto.ret.RetSaveToGstnReqDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface ScreenDeciderAndExtractor {
	
	public List<Pair<String, String>> getGstr1CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode);
	
	public List<Pair<String, String>> getAnx1CombinationPairs(
			Anx1SaveToGstnReqDto dto, String groupCode);
	
	public List<Pair<String, String>> getRetCombinationPairs(
			RetSaveToGstnReqDto dto, String groupCode);

	public List<Pair<String, String>> getGstr7CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode);

	public List<Pair<String, String>> getItc04CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode);
	
	public List<Pair<String, String>> getGstr1ACombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode);
}