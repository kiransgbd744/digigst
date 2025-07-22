/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1CatalogErrInfoResponseDto;
import com.ey.advisory.app.docs.dto.Anx1CatalogErrInfoReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;
import com.ey.advisory.core.async.repositories.master.MasterErrorCatalogEntityRepository;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1OutwardErrInfoCatalogDaoImpl")
public class Anx1OutwardErrInfoCatalogDaoImpl
		implements Anx1OutwardErrInfoCatalogDao {

	@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	private MasterErrorCatalogEntityRepository masterErrorCatalogRepository;

	@Override
	public List<Anx1CatalogErrInfoResponseDto> getErrInfoCatalog(
			Anx1CatalogErrInfoReportsReqDto request) {

		String type = request.getType();
		String dataType = request.getDataType();
		String qType = type.toUpperCase();
		String qDataType = dataType.toUpperCase();

		List<Anx1CatalogErrInfoResponseDto> errList = new ArrayList<>();
		if ((dataType.equalsIgnoreCase(DownloadReportsConstant.INWARD))
				|| (dataType.equalsIgnoreCase(DownloadReportsConstant.OTHERS))) {

			List<MasterErrorCatalogEntity> errorInfo = masterErrorCatalogRepository
					.findErrorCodeByType(qType, qDataType);

			if (errorInfo != null) {
				if ((type.equalsIgnoreCase(DownloadReportsConstant.ERR)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.OTHERS))
						|| (type.equalsIgnoreCase(DownloadReportsConstant.ERR)
								&& dataType.equalsIgnoreCase(
										DownloadReportsConstant.INWARD))) {
					for (MasterErrorCatalogEntity object : errorInfo) {
						Anx1CatalogErrInfoResponseDto resp = new Anx1CatalogErrInfoResponseDto();
						resp.setErrorCode(object.getErrorCode());
						resp.setErrorDesc(object.getErrorDesc());
						errList.add(resp);
					}
				}
				if ((type.equalsIgnoreCase(DownloadReportsConstant.INFO)
						&& dataType.equalsIgnoreCase(
								DownloadReportsConstant.OTHERS))
						|| (type.equalsIgnoreCase(DownloadReportsConstant.INFO)
								&& dataType.equalsIgnoreCase(
										DownloadReportsConstant.INWARD))) {
					for (MasterErrorCatalogEntity object : errorInfo) {
						Anx1CatalogErrInfoResponseDto resp = new Anx1CatalogErrInfoResponseDto();
						resp.setInfoCode(object.getErrorCode());
						resp.setInfoDesc(object.getErrorDesc());
						errList.add(resp);
					}
				}
			}
		}
		if (dataType.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {

			List<MasterErrorCatalogEntity> errorInfo = masterErrorCatalogRepository
					.findOUErrorCodeByType(qType, qDataType);

			if (errorInfo != null) {
				if (type.equalsIgnoreCase(DownloadReportsConstant.ERR)) {
					for (MasterErrorCatalogEntity object : errorInfo) {
						Anx1CatalogErrInfoResponseDto resp = new Anx1CatalogErrInfoResponseDto();
						resp.setErrorCode(object.getErrorCode());
						resp.setErrorDesc(object.getErrorDesc());
						errList.add(resp);
					}
				}
				if (type.equalsIgnoreCase(DownloadReportsConstant.INFO)) {
					for (MasterErrorCatalogEntity object : errorInfo) {
						Anx1CatalogErrInfoResponseDto resp = new Anx1CatalogErrInfoResponseDto();
						resp.setInfoCode(object.getErrorCode());
						resp.setInfoDesc(object.getErrorDesc());
						errList.add(resp);
					}
				}
			}
		}
		return errList;
	}
}