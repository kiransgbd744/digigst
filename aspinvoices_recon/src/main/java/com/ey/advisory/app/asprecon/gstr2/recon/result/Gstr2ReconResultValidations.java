package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.common.base.Strings;

/**
 * @author Sakshi.jain
 *
 */
@Component("Gstr2ReconResultValidations")
public class Gstr2ReconResultValidations {

	public void validations(Gstr2ReconResultReqDto reqDto) {
		if (!reqDto.getVndrPans().isEmpty()) {
			List<String> vndrPans = reqDto.getVndrPans().stream()
					.filter(o -> o.length() == 10)
					.filter(o -> o.matches("[A-Z0-9]*"))
					.collect(Collectors.toList());
			if (!vndrPans.equals(reqDto.getVndrPans())) {
				throw new AppException("Invalid value in filter Vendor PAN");
			}

		}

		if (!reqDto.getVndrGstins().isEmpty())

		{
			List<String> vndrGstins = reqDto.getVndrGstins().stream()
					.filter(o -> o.length() == 15)
					.filter(o -> o.matches("[A-Z0-9]*"))
					.collect(Collectors.toList());
			if (!vndrGstins.equals(reqDto.getVndrGstins())) {
				throw new AppException("Invalid value in filter Vendor GSTIN");
			}

		}

		if ((Strings.isNullOrEmpty(reqDto.getFrmTaxPrd3b())
				&& !Strings.isNullOrEmpty(reqDto.getToTaxPrd3b()))
				|| (!Strings.isNullOrEmpty(reqDto.getFrmTaxPrd3b())
						&& Strings.isNullOrEmpty(reqDto.getToTaxPrd3b()))) {
			throw new AppException(
					"Kindly select value in both filters of 3B Tax Period");
		}

		if (reqDto.getVendorPans() != null
				&& !reqDto.getVendorPans().isEmpty()) {
			List<String> vndrPans = reqDto.getVendorPans().stream()
					.filter(o -> o.length() == 10)
					.filter(o -> o.matches("[A-Z0-9]*"))
					.collect(Collectors.toList());
			if (!vndrPans.equals(reqDto.getVendorPans())) {
				throw new AppException("Invalid value in filter Vendor PAN");
			}

		}

		if (reqDto.getVendorGstins() != null
				&& !reqDto.getVendorGstins().isEmpty())

		{
			List<String> vndrGstins = reqDto.getVendorGstins().stream()
					.filter(o -> o.length() == 15)
					.filter(o -> o.matches("[A-Z0-9]*"))
					.collect(Collectors.toList());
			if (!vndrGstins.equals(reqDto.getVendorGstins())) {
				throw new AppException("Invalid value in filter Vendor GSTIN");
			}

		}

/*		// 168474
		Optional<List<String>> optionalAccVoucherNos = Optional
				.ofNullable(reqDto.getAccVoucherNums());

		Optional<List<String>> optionalDocNumbers = Optional
				.ofNullable(reqDto.getDocNumberList());

		if (optionalAccVoucherNos.isPresent()) {

			List<String> accVoucherNos = optionalAccVoucherNos.get(); // This is
																		// a
																		// List<String>
			String accVoucherNoString = String.join(",", accVoucherNos);
			if (accVoucherNoString.length() > 2000) {

				throw new AppException(
						"Accounting Voucher Numbers have exceeded the limit of 2000 characters");
			}
		}

		if (optionalDocNumbers.isPresent()) {

			List<String> docNumber = optionalDocNumbers.get();
			String docNumberString = String.join(",", docNumber);
			if (docNumberString.length() > 2000) {
				throw new AppException(
						"Document Numbers have exceeded the limit of 2000 characters");
			}

		}*/

	}
}