package com.ey.advisory.app.data.entities.client;

import java.util.List;
import java.util.Optional;

public class OutwardTransDocSupplyTypeUtil {
	
	public static String getSupplyType(OutwardTransDocument doc) {
		
		List<OutwardTransDocLineItem> items = doc.getLineItems();
		
		
		Optional<OutwardTransDocLineItem> taxItem = items.stream().filter(
				item -> "TAX".equalsIgnoreCase(item.getSupplyType())).findAny();
		
		if (taxItem.isPresent()) return "TAX";
		
		// Assumes that there is at least one line item. If the items
		// collection is empty, the following line will result in an exception.
		return items.get(0).getSupplyType();
		
		
	}
	
}
