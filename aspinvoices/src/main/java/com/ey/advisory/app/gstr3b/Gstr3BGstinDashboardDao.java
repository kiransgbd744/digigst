/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Khalid1.Khan
 *
 */
public interface Gstr3BGstinDashboardDao {

	public List<Gstr3BGstinAspUserInputDto> getGstinComputeDtoList(String gstin,
			String taxPeriod) throws AppException;

	public List<Gstr3BGstinAspUserInputDto> getGstinUserInputDtoList(
			String gstin, String taxPeriod) throws AppException;

	public List<Gstr3BGstinAspUserInputDto> getGstinAutoCalDtoList(String gstin,
			String taxPeriod) throws AppException;

	public List<Gstr3BGstinAspUserInputDto> getGstnDtoList(String gstin,
			String taxPeriod) throws AppException;

	public List<Gstr3BSummaryDto> getAllGstinUserInputDtoList(
			List<String> gstin, String taxPeriod) throws AppException;

	public List<Gstr3BGstinAspUserInputDto> getUserInputDtoBySection(
			String taxPeriod, String gstin, List<String> sections);

	public List<Gstr3BGstinAspUserInputDto> getUserInputDtoforSavePst(
			String gstin, String taxPeriod, List<String> sections);

	public List<Gstr3BGstinAspUserInputDto> getComputeDtoBySection(
			String taxPeriod, String gstin, List<String> sections);

	public List<Gstr3BGstinAspUserInputDto> getAutoCalDtoBySection(
			String taxPeriod, String gstin, List<String> sections);

}
