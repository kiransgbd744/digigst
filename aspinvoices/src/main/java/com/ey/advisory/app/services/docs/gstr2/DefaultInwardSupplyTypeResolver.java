package com.ey.advisory.app.services.docs.gstr2;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.common.GSTConstants;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultInwardSupplyTypeResolver")
public class DefaultInwardSupplyTypeResolver 
				implements InwardSupplyTypeResolver {

	
	@Override
	public String resolve(InwardTransDocument document) {
		
		// Get the list of distinct supply types present in the
		// line items.
		
		Map<String, Integer> map = document.getLineItems().stream()
				.collect(Collectors.toMap(
						o -> trimAndConvToUpperCase(o.getSupplyType()), 
						o -> Integer.MIN_VALUE,
						(o1, o2) -> Integer.MIN_VALUE
						));
		
		
		// If all the line items have the same supply type, then the map
		// will have only one entry. hence, return the supply type of the
		// first line item.
		if (map.size() == 1) return trimAndConvToUpperCase(document.getLineItems()
				.get(0).getSupplyType());
		
		// If the line items have different supply types, then we go by the
		// Inward doc rules, to determine the supply type. Since the document
		// is already validated at the validation layer, we can assume that
		// only valid combinations are present at this point in time. 
		
		// First check if 'TAX' is present. If so choose tax as the supply
		// type. TAX, EXT,NIL,NON category.
		if (map.containsKey(GSTConstants.TAX)) return GSTConstants.TAX;
		
		// Then check if 'DTA' is present. If so, choose 'DTA' as the supply
		// type. This falls in the DTA, EXT, NIL, NON category.
		if (map.containsKey(GSTConstants.DTA)) return GSTConstants.DTA;
		
		// Then check if 'DXP' is present. If so, choose 'DTA' as the supply
		// type. This falls in the DXP, EXT, NIL, NON category.
		if (map.containsKey(GSTConstants.DXP)) return GSTConstants.DXP;
		
		// Then check if 'NIL' is present. If so, choose 'NIL' as the supply
		// type. This falls in the EXT, NIL, NON category.
		if (map.containsKey(GSTConstants.NIL)) return GSTConstants.NIL;
		
		//Choose EXT if This falls in the EXT, NON category 		
		return GSTConstants.EXT;
		
	}

}
