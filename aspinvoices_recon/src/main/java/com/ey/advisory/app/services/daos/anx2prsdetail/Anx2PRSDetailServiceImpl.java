package com.ey.advisory.app.services.daos.anx2prsdetail;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

@Component("Anx2PRSDetailServiceImpl")
public class Anx2PRSDetailServiceImpl implements Anx2PRSDetailService {

@Autowired
@Qualifier("Anx2PRSDetailDaoImpl")
Anx2PRSDetailDao anx2PRSDetailDao;

@Autowired
@Qualifier("Anx2PRSDetailsB2B")
Anx2PRSDetailsB2B anx2PRSDetailsB2B;

@Autowired
@Qualifier("Anx2PRSDetailsSEZWP")
Anx2PRSDetailsSEZWP anx2PRSDetailsSEZWP;

@Autowired
@Qualifier("Anx2PRSDetailsSEZWOP")
Anx2PRSDetailsSEZWOP anx2PRSDetailsSEZWOP;

@Autowired
@Qualifier("Anx2PRSDetailsISD")
Anx2PRSDetailsISD anx2PRSDetailsISD;

@Autowired
@Qualifier("Anx2PRSDetailsDeemedExports")
Anx2PRSDetailsDeemedExports anx2PRSDetailsDeemedExports;

@Autowired
@Qualifier("Anx2PRSDetailB2BDeafault")
private Anx2PRSDetailB2BDeafault anx2PRSDetailB2BDeafault;

@Autowired
@Qualifier("Anx2PRSDetailDEDeafault")
private Anx2PRSDetailDEDeafault anx2PRSDetailDEDeafault;

@Autowired
@Qualifier("Anx2PRSDetailISDDeafault")
private Anx2PRSDetailISDDeafault anx2PRSDetailISDDeafault;

@Autowired
@Qualifier("Anx2PRSDetailSEZWOPDeafault")
private Anx2PRSDetailSEZWOPDeafault anx2PRSDetailSEZWOPDeafault;

@Autowired
@Qualifier("Anx2PRSDetailSEZWPDeafault")
private Anx2PRSDetailSEZWPDeafault anx2PRSDetailSEZWPDeafault;

@Override
public List<Anx2PRSDetailHeaderDto> getAnx2PRSDetail(Anx2PRSProcessedRequestDto dto) {

	List<String> docType = dto.getDocType();

	List<Anx2PRSDetailResponseDto> entityResponse = new ArrayList<>();

	entityResponse = anx2PRSDetailDao.getAnx2PRSDetail(dto);

	List<Anx2PRSDetailHeaderDto> headerList = new ArrayList<>();

	List<String> recordType = dto.getRecordType();

	if (docType.size() > 0) {
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.B2B))) {
			Anx2PRSDetailHeaderDto b2bdetails = anx2PRSDetailsB2B
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(b2bdetails);
		}

		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.SEZWP))) {
			Anx2PRSDetailHeaderDto sezwpdetails = anx2PRSDetailsSEZWP
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(sezwpdetails);
		}

		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.SEZWOP))) {
			Anx2PRSDetailHeaderDto sezwopdetails = anx2PRSDetailsSEZWOP
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(sezwopdetails);
		}

		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.DXP))) {
			Anx2PRSDetailHeaderDto deemedExports = anx2PRSDetailsDeemedExports
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(deemedExports);
		}
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.ISD))) {
			Anx2PRSDetailHeaderDto isddetails = anx2PRSDetailsISD
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(isddetails);
		}

	}

	if (docType.size() <= 0) {
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.B2B))) {
			Anx2PRSDetailHeaderDto b2bdefault = anx2PRSDetailB2BDeafault
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(b2bdefault);
		}
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.SEZWP))) {
			Anx2PRSDetailHeaderDto sezwpdefault = anx2PRSDetailSEZWPDeafault
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(sezwpdefault);
		}
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.SEZWOP))) {
			Anx2PRSDetailHeaderDto sezwopdefault = anx2PRSDetailSEZWOPDeafault
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(sezwopdefault);
		}
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.DXP))) {
			Anx2PRSDetailHeaderDto dedefault = anx2PRSDetailDEDeafault
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(dedefault);
		}
		if (recordType.size() <= 0 || recordType
				.contains(trimAndConvToUpperCase(GSTConstants.ISD))) {
			Anx2PRSDetailHeaderDto isddefault = anx2PRSDetailISDDeafault
					.getAnx2PRSummaryDetail(entityResponse, docType);
			headerList.add(isddefault);
		}
	}

	return headerList;
}

}
