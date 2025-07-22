/**
 * 
 */
package com.ey.advisory.common;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Laxmi.Salukuti
 *
 */
@Service("EInvoiceDefaultOutwardSupplyTypeResolver")
@Slf4j
public class EInvoiceDefaultOutwardSupplyTypeResolver
		implements EInvoiceOutwardSupplyTypeResolver {

	@Override
	public String resolve(OutwardTransDocument document) {
		// Get the list of distinct supply types present in the
		// line items.

		try {
			Map<String, Integer> map = document.getLineItems().stream()
					.collect(Collectors.toMap(o -> o.getSupplyType().trim(),
							o -> Integer.MIN_VALUE,
							(o1, o2) -> Integer.MIN_VALUE));

			// If all the line items have the same supply type, then the map
			// will have only one entry. hence, return the supply type of the
			// first line item.
			if (map.size() == 1)
				return document.getLineItems().get(0).getSupplyType().trim();

			// If the line items have different supply types, then we go by the
			// Outward doc rules, to determine the supply type. Since the
			// document
			// is already validated at the validation layer, we can assume that
			// only valid combinations are present at this point in time.

			// First check if 'TAX' is present. If so choose tax as the supply
			// type. TAX, EXT,NIL,NON category.
			if (map.containsKey("TAX"))
				return "TAX";

			if (map.containsKey("SEZWP"))
				return "SEZWP";

			if (map.containsKey("EXPT"))
				return "EXPT";

			// Then check if 'DTA' is present. If so, choose 'DTA' as the supply
			// type. This falls in the DTA, EXT, NIL, NON category.
			if (map.containsKey("DTA"))
				return "DTA";

			// map Contains Only NIL,NON,EXT then return as TAX, Irn to be
			// Generated.
			if (map.containsKey("NIL") || map.containsKey("NON")
					|| map.containsKey("EXT")) {

				Map<String, String> nilNonExmpMap = document.getLineItems()
						.stream()
						.collect(Collectors.toMap(
								o -> String.valueOf(o.getLineNo()),
								o -> o.getSupplyType().trim(), (o1, o2) -> o1));
				TreeMap<String, String> sortedMap = new TreeMap<>(
						nilNonExmpMap);
				return sortedMap.firstEntry().getValue();
			}

		} catch (Exception ex) {
			String errMsg = "Supply type should be mandatory for all line items";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}

		// supply type provided in first line item it shall be taken to
		// respective tables. This falls in EXT/NIL/NON
		return document.getLineItems().get(0).getSupplyType().trim();
	}

}
