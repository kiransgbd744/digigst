/*package com.ey.advisory.sap.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.B2CSRespDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.jobs.entities.B2CSEntity;
import com.ey.advisory.jobs.entities.B2csViewPersistEntity;
import com.ey.advisory.jobs.repositories.client.B2CSViewRepository;
import com.ey.advisory.jobs.repositories.client.B2csViewPersistRepository;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*

@RestController
public class B2csController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(B2csController.class);

	@Autowired
	@Qualifier("b2CSViewRepository")
	private B2CSViewRepository b2CSViewRepository;

	@Autowired
	private B2CSRespDto b2CSRespDto;

	@Autowired
	@Qualifier("b2csViewPersistRepository")
	private B2csViewPersistRepository b2csViewPersistRepository;

	@PostMapping(value = "/V1/CalculationView/{returnPeriod}", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public B2CSRespDto getAndPersistFromView(@PathVariable int returnPeriod) {
		LOGGER.info("Triggered the persistOutwardSupply");
		List<B2CSEntity> repEntity = null;
		List<B2csViewPersistEntity> list = new ArrayList<>();
		String groupCode = TestController.staticTenantId();
		if (returnPeriod > 0) {
			try {
				LOGGER.info("groupCode is {}", groupCode);
				TenantContext.setTenantId(groupCode);
				repEntity = b2CSViewRepository.findByReturnPeriod(returnPeriod);
				LOGGER.info("{} B2CS documents found from Calculation View.", 
						repEntity.size());

				for (int i = 0; i < repEntity.size(); i++) {
					B2csViewPersistEntity b2csViewPersistEntity = new 
							B2csViewPersistEntity();

					// b2csViewPersistEntity.setId(repEntity.get(i).getId());
					b2csViewPersistEntity
							.setCgstAmt(repEntity.get(i).getCgstAmt());
					b2csViewPersistEntity
							.setSgstAmt(repEntity.get(i).getSgstAmt());
					b2csViewPersistEntity
							.setIgstAmt(repEntity.get(i).getIgstAmt());
					b2csViewPersistEntity.setCessAmtAdavlorem(
							repEntity.get(i).getCessAmtAdavlorem());
					b2csViewPersistEntity.setCessAmtSpecific(
							repEntity.get(i).getCessAmtSpecific());
					b2csViewPersistEntity.setCessRateAdavlorem(
							repEntity.get(i).getCessRateAdavlorem());
					b2csViewPersistEntity.setCessRateSpecific(
							repEntity.get(i).getCessRateSpecific());
					b2csViewPersistEntity
							.setCustGstn(repEntity.get(i).getCustGstn());
					b2csViewPersistEntity.setEcomCustGstn(
							repEntity.get(i).getEcomCustGstn());
					b2csViewPersistEntity
							.setInvHeader(repEntity.get(i).getInvHeader());
					b2csViewPersistEntity
							.setInvType(repEntity.get(i).getInvType());
					b2csViewPersistEntity
							.setItemType(repEntity.get(i).getItemType());
					b2csViewPersistEntity
							.setSupplyType(repEntity.get(i).getSupplyType());
					b2csViewPersistEntity.setPos(repEntity.get(i).getPos());
					b2csViewPersistEntity.setReturnPeriod(
							repEntity.get(i).getReturnPeriod());
					b2csViewPersistEntity.setSupplierGstn(
							repEntity.get(i).getSupplierGstn());
					b2csViewPersistEntity
							.setTaxableAmt(repEntity.get(i).getTaxableAmt());
					b2csViewPersistEntity
							.setTaxRate(repEntity.get(i).getTaxRate());
					b2csViewPersistEntity
							.setTaxDocType(repEntity.get(i).getTaxDocType());
					b2csViewPersistEntity.setUinorcopmposition(
							repEntity.get(i).getUinorcopmposition());
					list.add(b2csViewPersistEntity);
				}
				int size = list.size();
				b2csViewPersistRepository.saveAll(list);
				b2CSRespDto.setStatus("success total size" + size);
			} catch (Exception ex) {
				b2CSRespDto.setStatus("failure");
			} finally {
				TenantContext.clearTenant();
			}
		} else {
			b2CSRespDto.setStatus("Invalid Input");
		}
		return b2CSRespDto;
	}
}
*/