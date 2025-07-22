/**
 * 
 */
package com.ey.advisory.app.services.gstr1fileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CrossItcProcessEntity;
import com.ey.advisory.app.data.repositories.client.CrossItcProcessRepository;
import com.ey.advisory.app.docs.dto.Gstr6CrossItcRequestDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Balakrishna.S
 *
 */
@Component("Gstr6CrossItcUpdateService")
@Slf4j
public class Gstr6CrossItcUpdateService {

	@Autowired
	@Qualifier("CrossItcProcessRepository")
	CrossItcProcessRepository repo;
	
	private final static String WEB_UPLOAD_KEY = "|";
	
	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(
			List<Gstr6CrossItcRequestDto> list) {
				
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr6CrossItcRequestDto> crossItcList = new ArrayList<>(); 
		
		JsonObject resp = new JsonObject();
		
		for(Gstr6CrossItcRequestDto dto : list){
			
			
			CrossItcProcessEntity crossEntity = new CrossItcProcessEntity();
			
			crossEntity.setTaxPeriod(dto.getTaxPeriod());
			crossEntity.setIsdGstin(dto.getIsdGstin());
			crossEntity.setIgstUsedAsIgst(dto.getUserIgstigst());
			crossEntity.setSgstUsedAsIgst(dto.getUserSgstigst());
			crossEntity.setCgstUsedAsIgst(dto.getUserCgstigst());
			crossEntity.setSgstUsedAsSgst(dto.getUserSgstSgst());
			crossEntity.setIgstUsedAsSgst(dto.getUserIgstSgst());
			crossEntity.setCgstUsedAsCgst(dto.getUserCgstCgst());
			crossEntity.setIgstUsedAsCgst(dto.getUserIgstCgst());
			crossEntity.setCessUsedAsCess(dto.getUserCesscess());
			crossEntity.setCreatedBy("SYSTEM");
			crossEntity.setCreatedOn(LocalDateTime.now());
			
			if (dto.getDocKey() != null) {
				// hsnData = repo.getHsnData(dto.getDocKey());
				// entity.setDesc(dto.getUiDesc());
				repo.UpdateId(dto.getDocKey());
				crossEntity.setCrossItcDocKey(dto.getDocKey());
			} else {
				String docKey = null;
				  docKey = getInvKey(dto.getIsdGstin(),dto.getTaxPeriod());
				 crossEntity.setCrossItcDocKey(docKey);
				}

			crossEntity.setDerivedRetPeriod(GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod()));
			
			CrossItcProcessEntity crossItcData = repo
					.findingActiveRecordsByDocKey(crossEntity.getCrossItcDocKey());

			if (crossItcData != null) {
				repo.UpdateId(crossEntity.getCrossItcDocKey());
				aggregateCrossItc(crossItcData, crossEntity);
			}

			CrossItcProcessEntity save = repo.save(crossEntity);
			
			Gstr6CrossItcRequestDto convertEntityToDto = convertEntityToDto(save);
			crossItcList.add(convertEntityToDto);
			
			
		}
		
		JsonElement respBody = gson.toJsonTree(crossItcList);

		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);

		return resp;
		
		
	}


	/**
	 * @param save
	 */
	private Gstr6CrossItcRequestDto convertEntityToDto(CrossItcProcessEntity save) {
	
		
		Gstr6CrossItcRequestDto dtoResp = new Gstr6CrossItcRequestDto();
		dtoResp.setDigigstCesscess(save.getCessUsedAsCess());
		dtoResp.setDigigstCgstCgst(save.getCgstUsedAsCgst());
		dtoResp.setDigigstCgstigst(save.getCgstUsedAsIgst());
		dtoResp.setDigigstIgstCgst(save.getIgstUsedAsCgst());
		dtoResp.setDigigstIgstigst(save.getIgstUsedAsIgst());
		dtoResp.setDigigstIgstSgst(save.getIgstUsedAsSgst());
		dtoResp.setDigigstSgstigst(save.getSgstUsedAsIgst());
		dtoResp.setDigigstSgstSgst(save.getSgstUsedAsSgst());
		dtoResp.setTaxPeriod(save.getTaxPeriod());
		dtoResp.setIsdGstin(save.getIsdGstin());
		dtoResp.setDocKey(save.getCrossItcDocKey());
	//	dtoResp.setDigigstComputeStatus(save.get);
	
		
		return dtoResp;
		// TODO Auto-generated method stub
		
	}


	/**
	 * @param crossItcData
	 * @param crossEntity
	 */
	public  void aggregateCrossItc(CrossItcProcessEntity crossItcData,
			CrossItcProcessEntity crossEntity) {
		
	crossEntity.setIgstUsedAsCgst(crossItcData.getIgstUsedAsCgst().add(crossEntity.getIgstUsedAsCgst()));
	crossEntity.setIgstUsedAsSgst(crossItcData.getIgstUsedAsSgst().add(crossEntity.getIgstUsedAsSgst()));
	crossEntity.setIgstUsedAsIgst(crossItcData.getIgstUsedAsIgst().add(crossEntity.getIgstUsedAsIgst()));
	crossEntity.setSgstUsedAsIgst(crossItcData.getSgstUsedAsIgst().add(crossEntity.getSgstUsedAsIgst()));
	crossEntity.setSgstUsedAsSgst(crossItcData.getSgstUsedAsSgst().add(crossEntity.getSgstUsedAsSgst()));
	crossEntity.setCgstUsedAsIgst(crossItcData.getCgstUsedAsIgst().add(crossEntity.getCgstUsedAsIgst()));
	crossEntity.setCgstUsedAsCgst(crossItcData.getCgstUsedAsCgst().add(crossEntity.getCgstUsedAsCgst()));
	crossEntity.setCessUsedAsCess(crossItcData.getCessUsedAsCess().add(crossEntity.getCessUsedAsCess()));
				
	}
	
	/**
	 * Creating docKey For Adding New Records
	 * @param obj
	 * @return
	 */
	public String getInvKey(String gstn,String taxPeriod) {
		String isdGstin = (gstn != null) ? String.valueOf(gstn).trim() : "";
		String retPeriod = (taxPeriod != null) ? String.valueOf(taxPeriod).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(isdGstin).add(retPeriod)
				.toString();
	}

	
}
