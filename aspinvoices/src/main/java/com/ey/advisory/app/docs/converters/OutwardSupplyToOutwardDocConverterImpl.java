/*package com.ey.advisory.app.docs.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardSupplyEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.OutwardSupplyReqDto;
import com.ey.advisory.common.AppException;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Component("outwardSupplyToOutwardDocConverterImpl")
public class OutwardSupplyToOutwardDocConverterImpl
		implements OutwardSupplyToOutwardDocConverter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSupplyToOutwardDocConverterImpl.class);

	public OutwardTransDocument convert(List<OutwardSupplyEntity> docItems) {

		if (docItems == null || docItems.size() == 0) {
			String msg = "The input line "
					+ "Items list cannot be null or empty.";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		OutwardTransDocument doc = new OutwardTransDocument();
		ModelMapper modelMapper = new ModelMapper();

		// Extract the first item from the input list so that we can use this
		// to set header level information to the document.
		// Modelmapper to convert the dto to entity

		OutwardSupplyReqDto firstDto = modelMapper.map(docItems.get(0),
				OutwardSupplyReqDto.class);

		doc.setOrigCgstin(firstDto.getOriginalCustomerGSTIN());
		doc.setBillToState(firstDto.getBillToState());
		doc.setShipToState(firstDto.getShipToState());
		//doc.setShippingBillDate(firstDto.getShippingBillDate());
		doc.setShippingBillNo(firstDto.getShippingBillNumber());
		doc.setExportDuty(firstDto.getExportDuty());
		doc.setFob(firstDto.getFob());
		doc.setTcsFlag(firstDto.getTcsFlag());
		doc.setUinOrComposition(firstDto.getUinOrComposition());
		doc.setItcFlag(firstDto.getItcFlag());
		
		 List<OutwardTransDocLineItem> lineItems=new ArrayList<OutwardTransDocLineItem>();

		
		//forEach loop to iterate the line items through IntStream to get the index values.
		IntStream.range(0, docItems.size()).forEach(i -> {
			OutwardTransDocLineItem lineItem = new OutwardTransDocLineItem();
			// Model mapper to convert the entity to dto
			OutwardSupplyReqDto dto = modelMapper.map(docItems.get(i),
					OutwardSupplyReqDto.class);

			// set the order in which we get the list of items from HCI
			lineItem.setCessAmountAdvalorem(dto.getCessAmountAdvalorem());
			lineItem.setCessAmountSpecific(dto.getCessAmountSpecific());
			lineItem.setCessRateAdvalorem(dto.getCessRateAdvalorem());
			lineItem.setCessRateSpecific(dto.getCessRateSpecific());
			lineItem.setCgstAmount(dto.getCentralTaxAmount());
			lineItem.setHsnSac(dto.getHsnOrSAC());
			lineItem.setIgstAmount(dto.getIntegratedTaxAmount());
			lineItem.setItemCategory(dto.getCategoryOfProduct());
			lineItem.setItemCode(dto.getProductCode());
			lineItem.setItemDescription(dto.getProductDescription());
			lineItem.setLineNo(Integer.parseInt(dto.getLineNumber()));
			lineItem.setQty(dto.getQuantity());
			lineItem.setSgstAmount(dto.getStateUtTaxAmount());
			lineItem.setTaxableValue(dto.getTaxableValue());
			lineItem.setTaxRate(dto.getTaxRate());

			// total amount is the addition of IntegratedTaxAmount,
			// StateUtTaxAmount and CentralTaxAmount
			lineItem.setTotalAmt(dto.getTotalAmt());
			lineItem.setUom(dto.getUnitOfMeasurement());

			//doc.addLienItem(lineItem);
			lineItems.add(lineItem);
		});
doc.setLineItems(lineItems);
		return doc;
	}

}
*/