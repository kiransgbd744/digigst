/*package com.ey.advisory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.converters.OutwardTransDocListBuilder;
import com.ey.advisory.functions.OutwardSupplyHelper;

*//**
 * OutwardSupplyController class is responsible for APIs related to Save the
 * flat file coming from ERP with out any business validation and bifurcation
 *
 * @author Hemasundar.J
 *//*

@RestController
public class OutwardSupplyController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSupplyController.class);

	@Autowired
	@Qualifier("outwardSupplyHelper")
	private OutwardSupplyHelper outwardSupplyHelper;

	@Autowired
	@Qualifier("outwardTransDocListBuilderImpl")
	OutwardTransDocListBuilder outwardTransDocListBuilder;

	@RequestMapping(value = "/V1/persistOutwardSupply", method = RequestMethod.POST)

	public ResponseEntity<String> saveOutwardSupply(
			@RequestBody String jsonString,
			@RequestHeader("groupCode") String groupCode) {
		LOGGER.info("Triggered the persistOutwardSupply");
		ResponseEntity<String> repEntity = outwardSupplyHelper
				.saveOutwardSupply(jsonString, groupCode);
		return repEntity;

	}

	*//**
	 * this method only responsible for doing testing
	 * 
	 * @param groupCode
	 * @return
	 *//*
	@RequestMapping(value = "/V1/getNewOutwardSupplies", method = RequestMethod.GET)

	public List<OutwardTransDocument> getNewOutwardSupplies(
			@RequestHeader("groupCode") String groupCode) {
		LOGGER.info("Triggered the persistOutwardSupply");
		List<OutwardTransDocument> repEntity = outwardTransDocListBuilder
				.buildOutwardDocs(groupCode);

		outwardSupplyHelper.saveOutwardTransDocuments(repEntity, groupCode);
		return repEntity;

	}

}
*/