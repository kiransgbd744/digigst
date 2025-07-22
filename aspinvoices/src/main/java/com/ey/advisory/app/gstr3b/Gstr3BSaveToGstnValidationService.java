package com.ey.advisory.app.gstr3b;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BSaveToGstnValidationService")
public class Gstr3BSaveToGstnValidationService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userInputRepo;

	public String validateAspCopiedData(String gstin, String taxPeriod) {

		String errorMsg = null;
		List<String> sectionList = Arrays.asList("3.1(a)", "3.1(d)", "4a(3)",
				"4a(4)", "4a(5)", "4b(1)", "4d(1)", "4d(2)");
		try {
			List<Gstr3BGstinAspUserInputEntity> inputList = userInputRepo
					.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod, true);

			Gstr3BGstinAspUserInputEntity user3_2Data = inputList.stream()
					.filter(o -> (o.getSectionName().equalsIgnoreCase("3.2(a)"))
							|| (o.getSectionName().equalsIgnoreCase("3.2(b)"))
							|| (o.getSectionName().equalsIgnoreCase("3.2(c)")))
					.collect(Collectors.reducing(
							new Gstr3BGstinAspUserInputEntity(),
							(acc, ele) -> acc.merge(ele)));

			Gstr3BGstinAspUserInputEntity user3_1aData = inputList
					.stream()
					.filter(o -> o.getSectionName().equalsIgnoreCase("3.1(a)"))
					.collect(Collectors.reducing(
							new Gstr3BGstinAspUserInputEntity(),
							(acc, ele) -> acc.merge(ele)));

			// a.compareTo(b) == 1 ---> a>b
			if (user3_1aData != null && user3_2Data != null) {

				boolean flagTaxableVal = (user3_2Data.getTaxableVal() != null
						&& user3_1aData.getTaxableVal() != null)
								? (user3_2Data.getTaxableVal().compareTo(
										user3_1aData.getTaxableVal()) == 1)
								: false;
								
				boolean flagIgst = (user3_2Data.getIgst() != null
						&& user3_1aData.getIgst() != null)
								? (user3_2Data.getIgst()
										.compareTo(user3_1aData.getIgst()) == 1)
								: false;				
				if (flagTaxableVal) {
					return "Amount in table 3.2 Taxable value should not exceed amount "
							+ "in table 3.1 (a) Taxable Value";
				}if (flagIgst) {
					return "Amount in table 3.2 Igst should not exceed amount "
							+ "in table 3.1 (a) Igst";
				}

			} 
				for (Gstr3BGstinAspUserInputEntity userInput : inputList) {

					if (sectionList.contains(userInput.getSectionName())) {

						if (!userInput.getSgst().equals(userInput.getCgst())) {
							return String.format(
									"CGST and SGST cannot be "
											+ "different %s ",
									userInput.getSectionName());
						}
					}
				}
		

		} catch (Exception ex) {
			String msg = String.format(
					"error occured in " + "Gstr3BSaveToGstnValidationService ",
					ex);
			LOGGER.error(msg)	;
			throw new AppException(msg);
		}

		return errorMsg;

	}

}
