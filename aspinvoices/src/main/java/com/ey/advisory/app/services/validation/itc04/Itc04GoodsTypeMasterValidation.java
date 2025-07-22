/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.Itc04TypesOfGoodsCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04GoodsTypeMasterValidation")
public class Itc04GoodsTypeMasterValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("DefaultItc04TypesOfGoodsCache")
	private Itc04TypesOfGoodsCache goodsTypeCache;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {
	
		List<ProcessingResult> errors = new ArrayList<>();
		if(GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		List<Itc04ItemEntity> items = document.getLineItems();
		IntStream.range(0, items.size()).forEach(idx -> {
			Itc04ItemEntity item = items.get(idx);

			if (item.getTypeOfGoods() != null
					&& !item.getTypeOfGoods().isEmpty()) {

				goodsTypeCache = StaticContextHolder.getBean(
						"DefaultItc04TypesOfGoodsCache",
						Itc04TypesOfGoodsCache.class);
				int n = goodsTypeCache.findTypesOfGoods(
						item.getTypeOfGoods().trim().toUpperCase());

				if (n <= 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.TYPES_OF_GOODS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5828",
							"Invalid TypeOfGoods.", location));
				}
			}
			String tableNum = document.getTableNumber();
			 if(GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(tableNum.trim())) {
				if (item.getTypeOfGoods() == null
						|| item.getTypeOfGoods().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.TYPES_OF_GOODS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5827",
							"TypeOfGoods cannot be left blank", location));
				}
			}
		});
		return errors;
	}

}