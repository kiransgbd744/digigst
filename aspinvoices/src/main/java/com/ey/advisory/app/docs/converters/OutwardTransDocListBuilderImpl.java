/*package com.ey.advisory.app.docs.converters;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardSupplyEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.OutwardSupplyRepository;
import com.ey.advisory.app.docs.dto.OutwardSupplyReqDto;
import com.ey.advisory.common.multitenancy.TenantContext;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Component("outwardTransDocListBuilderImpl")
public class OutwardTransDocListBuilderImpl
		implements OutwardTransDocListBuilder {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardTransDocListBuilderImpl.class);

	@Autowired
	@Qualifier("outwardSupplyRepository")
	private OutwardSupplyRepository outwardSupplyRepository;

	@Autowired
	@Qualifier("outwardSupplyToOutwardDocConverterImpl")
	private OutwardSupplyToOutwardDocConverter converter;

	public List<OutwardTransDocument> buildOutwardDocs(String groupCode) {
		
		OutwardTransDocument outwardTransDocument=new OutwardTransDocument();
		ModelMapper modelMapper = new ModelMapper();
		List<OutwardSupplyEntity> docItems = new ArrayList<>();
		List<OutwardTransDocument> outwardTransDocumentList = new ArrayList<>();
		
		try {
			// set the tenantId which indicates the client
			LOGGER.info("groupCode is " + groupCode);
			TenantContext.setTenantId(groupCode);
			// method to get the new list of Entities
			List<OutwardSupplyEntity> outwardSupplyEntities = outwardSupplyRepository
					.findNewInvoices();

		//	List<OutwardSupplyEntity> outwardSupplyEntities =new ArrayList<OutwardSupplyEntity>();
			OutwardSupplyReqDto prev = modelMapper.map(
					outwardSupplyEntities.get(0), OutwardSupplyReqDto.class);

			int count1 = 0,count2=1;
			for (OutwardSupplyEntity entity : outwardSupplyEntities) {
				// Model mapper to convert the entity to dto
				OutwardSupplyReqDto curObj = modelMapper.map(entity,
						OutwardSupplyReqDto.class);

				if ((!isSameAsPrev(curObj, prev)) || (count2==outwardSupplyEntities.size())) {
					docItems = outwardSupplyEntities.subList(count1,
							outwardSupplyEntities.indexOf(entity));

					if (docItems.size() > 0) {
						outwardTransDocument = converter.convert(docItems);
						outwardTransDocumentList.add(outwardTransDocument);
					}
					count1 = outwardSupplyEntities.indexOf(entity);
				}
				prev = curObj;
				count2++;
			}

			LOGGER.info("successfully attamted to read new OutwardSupply Data");
			return outwardTransDocumentList;

		} catch (Exception ex) {
			return outwardTransDocumentList;
		} finally {
			TenantContext.clearTenant();
		}

	}

	*//**
	 * Assuming that NULL validations has been done at HCI level and The
	 * previous and the current objects obtained here will have the correct
	 * values for GSTIN, DocType, DocDate and DocNum.
	 * 
	 * @param curObj
	 * @param prevObj
	 * @return
	 *//*
	private boolean isSameAsPrev(OutwardSupplyReqDto curObj,
			OutwardSupplyReqDto prevObj) {

		boolean isSame = false;

		if (!(prevObj.getSupplierGSTIN()
				.equalsIgnoreCase(curObj.getSupplierGSTIN())
				|| prevObj.getDocumentType()
						.equalsIgnoreCase(curObj.getDocumentType())
				|| prevObj.getDocumentDate()
						.equalsIgnoreCase(curObj.getDocumentDate())
				|| prevObj.getDocumentNumber()
						.equalsIgnoreCase(curObj.getDocumentNumber()))) {
			isSame = false;

		} else {
			isSame = true;
		}

		return isSame;
	}

}
*/