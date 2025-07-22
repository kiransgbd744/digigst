package com.ey.advisory.app.service.ims.supplier;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("SupplierImsDupCheckJsonUtil")
public class SupplierImsDupCheckJsonUtil {

	public String supplierImsJsonDupCheck(String gstin, String taxPeriod,
			String section, String returnType, List<String> apiResponses) {

		List<String> outputList = new ArrayList<>();

		// from db need to fetch section and return type

		try {
			for (String apiResp : apiResponses) {

				long dbLoadStTimeBeforeObjectMapper = System
						.currentTimeMillis();

				JsonObject respObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				Gson gson = new Gson();

				SupplierImsSectionDtlsDto supplierImsSectionDtlsDto = gson
						.fromJson(respObject, SupplierImsSectionDtlsDto.class);

				long dbLoadStTimeAfterObjectMapper = System.currentTimeMillis();
				long dbLoadStTimeDiffObjectMapper = (dbLoadStTimeAfterObjectMapper
						- dbLoadStTimeBeforeObjectMapper);

				String msg1 = String.format(
						"Total Time taken in creating objects from json "
								+ " '%d' millisecs ",
						dbLoadStTimeDiffObjectMapper);
				LOGGER.error(msg1);
				if (supplierImsSectionDtlsDto != null) {

					Gstr1ImsDTO gstr1 = null;

					if (returnType.equalsIgnoreCase("GSTR1")) {
						gstr1 = supplierImsSectionDtlsDto.getGstr1();
					} else {

						gstr1 = supplierImsSectionDtlsDto.getGstr1a();
					}

					List<ImsDataDto> b2bList = new ArrayList<>();
					if (section.equalsIgnoreCase("ecom")) {
						b2bList = gstr1.getEcom().getB2b();
						b2bList.addAll(gstr1.getEcom().getUrp2b());

					} else if (section.equalsIgnoreCase("ecoma")) {

						b2bList = gstr1.getEcom().getB2ba();
						b2bList.addAll(gstr1.getEcom().getUrp2ba());
					} else if (section.equalsIgnoreCase("b2b")) {
						b2bList = gstr1.getB2b();
					} else if (section.equalsIgnoreCase("b2ba")) {
						b2bList = gstr1.getB2ba();
					} else if (section.equalsIgnoreCase("cdnr")) {
						b2bList = gstr1.getCdnr();
					} else if (section.equalsIgnoreCase("cdnra")) {
						b2bList = gstr1.getCdnra();
					}

					long dbLoadStTime = System.currentTimeMillis();

					if (section.equalsIgnoreCase("cdnr")
							|| section.equalsIgnoreCase("cdnra")) {

						getInvData(outputList, b2bList, true);
					} else {

						getInvData(outputList, b2bList, false);
					}

					long dbLoadEndTime = System.currentTimeMillis();
					long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

					String msg = String.format(
							"Total Time taken to create outputList "
									+ " from Inv obj is '%d' millisecs ",
							dbLoadTimeDiff);
					LOGGER.error(msg);

				}

			}

			long dbLoadStTimeBeforeSort = System.currentTimeMillis();

			outputList
					.sort(Comparator.comparing((String s) -> s.split(":")[0]));

			long dbLoadEndTimeAfterSort = System.currentTimeMillis();
			long dbLoadTimeDiffAfterSort = (dbLoadEndTimeAfterSort
					- dbLoadStTimeBeforeSort);

			String msg1 = String.format(
					"Total Time taken in sorting outputList "
							+ " from Inv obj is '%d' millisecs ",
					dbLoadTimeDiffAfterSort);
			LOGGER.error(msg1);

			long dbLoadStTimeBeforeFinalString = System.currentTimeMillis();

			String finalString = String.join("|", outputList);

			long dbLoadEndTimeAfterFinalString = System.currentTimeMillis();
			long dbLoadTimeDiffAfterFinalString = (dbLoadEndTimeAfterFinalString
					- dbLoadStTimeBeforeFinalString);

			String msg2 = String.format(
					"Total Time taken to create Final String from outputList "
							+ " from Inv obj is '%d' millisecs ",
					dbLoadTimeDiffAfterFinalString);
			LOGGER.error(msg2);

			long dbLoadStTimeBeforeSha256 = System.currentTimeMillis();
			String sha256Hash = generateSHA256Hash(finalString);

			long dbLoadStTimeAftereSha256 = System.currentTimeMillis();
			long dbLoadTimeDiffAfterSha256 = (dbLoadStTimeAftereSha256
					- dbLoadStTimeBeforeSha256);

			String msg3 = String.format(
					"Total Time taken to create SHA 256 Hash from Final String "
							+ "  is '%d' millisecs ",
					dbLoadTimeDiffAfterSha256);
			LOGGER.error(msg3);

			// save the sha256Hash into batchTable then next time on GET call
			// check and compare with the same

			return sha256Hash;

		} catch (Exception e) {
			String msg = "Error Occured while checking dup check in hash in java";
			LOGGER.error(msg, e);
			throw new AppException(e);
		}
	}

	/**
	 * @param outputList
	 * @param b2bList
	 */
	private void getInvData(List<String> outputList, List<ImsDataDto> b2bList,
			boolean flag) {
		if (b2bList != null && !b2bList.isEmpty()) {
			for (ImsDataDto b2b : b2bList) {
				List<Inv1> invList = new ArrayList<>();
				if (flag) {
					invList = b2b.getNt();
				} else {
					invList = b2b.getInv();
				}
				if (!invList.isEmpty()) {
					for (Inv1 inv : invList) {
						if (inv.getInum() != null && inv.getChksum() != null
								&& inv.getImsactn() != null) {
							outputList.add(inv.getInum() + ":" + inv.getChksum()
									+ ":"
									+ (inv.getImsactn() != null
											&& !inv.getImsactn().isEmpty()
													? inv.getImsactn()
													: "N"));
						}
					}
				}
			}
		}
	}

	private String generateSHA256Hash(String input)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(input.getBytes());
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}

}
