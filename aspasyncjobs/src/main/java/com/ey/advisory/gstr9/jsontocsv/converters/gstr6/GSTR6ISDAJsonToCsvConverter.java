/**
 * 
 */
package com.ey.advisory.gstr9.jsontocsv.converters.gstr6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDetailsDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDocListItems;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdElglstDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component("GSTR6ISDAJsonToCsvConverter")
@Slf4j
public class GSTR6ISDAJsonToCsvConverter implements JsonToCsvConverter {

	private static final String GSTR6_ISDA_HEADER = "Category,ReturnPeriod,IsdGstin,"
			+ "RecipentGstin,StateCode,OriginalRecipeintGSTIN,OriginalStatecode,"
			+ "DocumentType,SupplyType,DocumentNumber,DocumentDate,"
			+ "OriginalDocumentNumber,OriginalDocumentDate,OriginalCreditNoteNumber,"
			+ "OriginalCreditNoteDate,EligibleIndicator,IgstAsIgst,IgstAsSgst,"
			+ "IgstAsCgst,SgstAsSgst,SgstAsIgst,CgstAsCgst,CgstAsIgst,CessAmount\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		Gson gson = GsonUtil.newSAPGsonInstance();

		BufferedWriter csvWriter = csvWriters[0];
		Gstr6IsdDetailsDto gstr6IsdaDetailsDto = gson.fromJson(reader,
				Gstr6IsdDetailsDto.class);

		writeDataToCSV(gstr6IsdaDetailsDto, csvWriter);
	}

	private void writeDataToCSV(Gstr6IsdDetailsDto gstr6IsdaDetailsDto,
			BufferedWriter csvWriter) throws IOException {

		LOGGER.debug("Entering Gstr6ISDA writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");

		List<Gstr6IsdElglstDto> elglstDtos = gstr6IsdaDetailsDto.getElglst();
		List<Gstr6IsdElglstDto> inElglstDtos = gstr6IsdaDetailsDto
				.getInelglst();

		if (CollectionUtils.isNotEmpty(elglstDtos)) {
			elglstDtos.forEach(elgstDto -> {

				List<Gstr6IsdDocListItems> docListItems = elgstDto.getDoclst();
				docListItems.forEach(lstItm -> {
					StringJoiner joiner = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("Re-Distribution")); // category
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString("'" + CommonContext.getTaxPeriod()));// return
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(CommonContext.getGstin())); // ISDgstin
					if(elgstDto.getTyp()!=null && elgstDto.getTyp().equalsIgnoreCase("R")){
						GenUtil.appendStringToJoiner(joiner,
								GenUtil.toCsvString(lstItm.getCpty())); // recipient gstin
					}
					else{
						GenUtil.appendStringToJoiner(joiner,
								GenUtil.toCsvString("URD")); // doubt
					}
					
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(elgstDto.getStatecd())); // statecode
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // original recipient
														// gstin
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // original state code
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getIsdDocty())); // isd
																		// doctype
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // supply type
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getDocnum())); // doc num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getDocdt() != null
									? "'" + LocalDate.parse(lstItm.getDocdt(),
											DateUtil.SUPPORTED_DATE_FORMAT2)
									: "'" + lstItm.getDocdt())); // doc date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOdocnum())); // org
																		// doc
																		// num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOdocdt())); // org doc
																		// date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOcrdnum())); // org
																		// credit
																		// note
																		// num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOcrddt())); // org
																		// credit
																		// nore
																		// date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("E")); // eligible indicator
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamts()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamtc()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getSamts()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getSamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCamtc()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCsamt()));

					finalJoiner.add(joiner.toString());

				});
			});
		}

		if (CollectionUtils.isNotEmpty(inElglstDtos))

		{
			inElglstDtos.forEach(inElgstDto -> {

				List<Gstr6IsdDocListItems> docListItems = inElgstDto
						.getDoclst();

				docListItems.forEach(lstItm -> {

					StringJoiner joiner = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("Re-Distribution")); // category
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString("'" + CommonContext.getTaxPeriod()));// return
					// period
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(CommonContext.getGstin())); // ISDgstin
					
					if(inElgstDto.getTyp()!=null && inElgstDto.getTyp().equalsIgnoreCase("R")){
						GenUtil.appendStringToJoiner(joiner,
								GenUtil.toCsvString(lstItm.getCpty())); // recipient gstin
					}
					else{
						GenUtil.appendStringToJoiner(joiner,
								GenUtil.toCsvString("URD")); // doubt
					}
					
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(inElgstDto.getStatecd())); // statecode
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // original recipient
														// gstin
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // original state code
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getIsdDocty())); // isd
																		// doctype
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("")); // supply type
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getDocnum())); // doc num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getDocdt() != null
									? "'" + LocalDate.parse(lstItm.getDocdt(),
											DateUtil.SUPPORTED_DATE_FORMAT2)
									: "'" + lstItm.getDocdt())); // doc date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOdocnum())); // org
																		// doc
																		// num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOdocdt())); // org doc
																		// date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOcrdnum())); // org
																		// credit
																		// note
																		// num
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(lstItm.getOcrddt())); // org
																		// credit
																		// nore
																		// date
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("IE")); // eligible indicator
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamts()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getIamtc()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getSamts()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getSamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCamtc()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCamti()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString("'" + lstItm.getCsamt()));

					finalJoiner.add(joiner.toString());

				});
			});
		}

		csvWriter.write(finalJoiner.toString());
		csvWriter.write("\r\n");
		LOGGER.debug("Exiting Gstr6ISDA writeDataToCSV");

	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { GSTR6_ISDA_HEADER };
	}

}
