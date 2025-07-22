package com.ey.advisory.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class B2COnBoardingCommonUtility {

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	B2COnBoardingConfigRepo b2cOnBoardingRepo;

	private GenerateParamsForDeepLink setValuesfromEntity(
			B2COnBoardingConfigEntity b2cEntity) {

		GenerateParamsForDeepLink params = new GenerateParamsForDeepLink();

		params.setPayeeAddress(b2cEntity.getPayeeAddress());
		params.setPayeeMerCode(b2cEntity.getPayeeMerCode());
		params.setPayeeName(b2cEntity.getPayeeName());
		params.setTransMode(b2cEntity.getTransQRMed());
		params.setQrExpireTime(b2cEntity.getQrExpireTime());
		params.setTransQRMed(b2cEntity.getTransQRMed());
		params.setPaymentInfo(b2cEntity.getPaymentInfo());
		return params;
	}

	public GenerateParamsForDeepLink getValuesfromEntityBasedOnOption(
			String optionSelected, String plantCode, String profitCenter,
			String gstin, String pan) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Inside  getValuesfromEntityBasedOnOption for"
							+ " Pan {} and Option {}  in B2COnBoardingConfigEntity",
					optionSelected, pan);
		}
		if (optionSelected.equalsIgnoreCase("0")) {
			B2COnBoardingConfigEntity prority1Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeAndProfitCentreAndIsActiveTrue(pan,
							plantCode, profitCenter);

			if (prority1Entity != null) {
				return setValuesfromEntity(prority1Entity);
			}
			B2COnBoardingConfigEntity prority2Entity = b2cOnBoardingRepo
					.findByPanAndProfitCentreAndPlantCodeIsNullAndIsActiveTrue(
							pan, profitCenter);
			if (prority2Entity != null) {
				return setValuesfromEntity(prority2Entity);

			}
			B2COnBoardingConfigEntity prority3Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeAndProfitCentreIsNullAndIsActiveTrue(
							pan, plantCode);
			if (prority3Entity != null) {
				return setValuesfromEntity(prority3Entity);
			}

			B2COnBoardingConfigEntity prority4Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeIsNullAndProfitCentreIsNullAndIsActiveTrue(
							pan);
			if (prority4Entity != null) {
				return setValuesfromEntity(prority4Entity);
			}

			return null;
		} else if (optionSelected.equalsIgnoreCase("1")) {

			B2COnBoardingConfigEntity prority1Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeAndGstinAndIsActiveTrue(pan,
							plantCode, gstin);

			if (prority1Entity != null) {
				return setValuesfromEntity(prority1Entity);
			}
			B2COnBoardingConfigEntity prority2Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeAndGstinIsNullAndIsActiveTrue(pan,
							plantCode);
			if (prority2Entity != null) {
				return setValuesfromEntity(prority2Entity);

			}
			B2COnBoardingConfigEntity prority3Entity = b2cOnBoardingRepo
					.findByPanAndGstinAndPlantCodeIsNullAndIsActiveTrue(pan,
							gstin);
			if (prority3Entity != null) {
				return setValuesfromEntity(prority3Entity);
			}

			B2COnBoardingConfigEntity prority4Entity = b2cOnBoardingRepo
					.findByPanAndPlantCodeIsNullAndGstinIsNullAndIsActiveTrue(
							pan);
			if (prority4Entity != null) {
				return setValuesfromEntity(prority4Entity);
			}

			return null;
		} else if (optionSelected.equalsIgnoreCase("2")) {

			B2COnBoardingConfigEntity prority1Entity = b2cOnBoardingRepo
					.findByPanAndProfitCentreAndGstinAndIsActiveTrue(pan,
							profitCenter, gstin);

			if (prority1Entity != null) {
				return setValuesfromEntity(prority1Entity);
			}
			B2COnBoardingConfigEntity prority2Entity = b2cOnBoardingRepo
					.findByPanAndProfitCentreAndGstinIsNullAndIsActiveTrue(pan,
							profitCenter);
			if (prority2Entity != null) {
				return setValuesfromEntity(prority2Entity);

			}
			B2COnBoardingConfigEntity prority3Entity = b2cOnBoardingRepo
					.findByPanAndGstinAndProfitCentreIsNullAndIsActiveTrue(pan,
							gstin);
			if (prority3Entity != null) {
				return setValuesfromEntity(prority3Entity);
			}

			B2COnBoardingConfigEntity prority4Entity = b2cOnBoardingRepo
					.findByPanAndProfitCentreIsNullAndGstinIsNullAndIsActiveTrue(
							pan);
			if (prority4Entity != null) {
				return setValuesfromEntity(prority4Entity);
			}
			return null;
		} else {
			return null;

		}
	}

}
