
package com.ey.advisory.app.inward.einvoice;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;

public interface InwardGetIrnDetailsDataParserService {

	public <K> K convertToHeaderList(EinvoiceRequestDto respDto,
			Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto irnDtlDto,
			Class<K> headerEntity) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, InstantiationException;

	public <T> List<T> convertToLineandNestedList(EinvoiceRequestDto respDto,
			Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto irnDtlDto,
			Long hdrEntityId, Class<T> itemEntity,
			List<GetIrnPreceedingDocDetailEntity> nestedList,
			List<GetIrnContractDetailEntity> contractList, List<GetIrnAdditionalSuppDocsEntity> additonalSuppDocsList, 
			List<GetIrnItemAttributeListEntity> itmAttList)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException,
			InstantiationException;

}
