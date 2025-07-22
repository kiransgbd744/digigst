package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.docs.dto.PermissibleITC10PerGstinConvertDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PerGstinDetailsDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PercentDto;
import com.ey.advisory.app.docs.dto.TaxBreakUpDetailsDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh NK
 *
 */
@Service("PermisibleITC10PercentServiceImpl")
@Slf4j
public class PermissibleITC10PercentServiceImpl
		implements PermissibleITC10PercentService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissibleITC10PercentDto> getPermissibleRecord(
			List<String> gstnsList, String toTaxPeriod, String fromTaxPeriod,
			List<String> docType, String reconType) {

		List<Object[]> list = null;

		Integer fromTaxPer = Integer.valueOf(fromTaxPeriod.substring(2)
				.concat(fromTaxPeriod.substring(0, 2)));

		Integer toTaxPer = Integer.valueOf(
				toTaxPeriod.substring(2).concat(toTaxPeriod.substring(0, 2)));

		String gstins = String.join(",", gstnsList);

		if (docType == null || docType.isEmpty()) {
			docType.add("R");
			docType.add("C");
			docType.add("D");
		}

		String docTypes = String.join(",", docType);

		LOGGER.debug(
				"Calling stored proc BEGIN for Permissible ITC10p {} PermissibleRecord ",
				reconType);

		StoredProcedureQuery storedProc = null;
		if ("2A_PR".equalsIgnoreCase(reconType)) {
			storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_ITC_PERM_SMRY");
		} else if ("2B_PR".equalsIgnoreCase(reconType)) {
			storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_2BPR_ITC_PERM_SMRY");
		}
		storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_GSTIN_LIST", gstins);

		storedProc.registerStoredProcedureParameter("P_FROM_TXPRD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_FROM_TXPRD", fromTaxPer);

		storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class,
				ParameterMode.IN);

		storedProc.setParameter("P_TO_TXPRD", toTaxPer);

		storedProc.registerStoredProcedureParameter("P_DOCTYPE", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_DOCTYPE", docTypes);

		list = storedProc.getResultList();

		long dbLoadStTime = System.currentTimeMillis();

		long dbLoadEndTime = System.currentTimeMillis();
		long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Time taken to load the Data"
							+ " from DB is '%d' millisecs Data count: '%s'",
					dbLoadTimeDiff, list.size());
			LOGGER.debug(msg);
		}

		LOGGER.debug("Converting Query And converting to List BEGIN");
		List<PermissibleITC10PercentDto> gstr2ReconSummary = list.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		LOGGER.debug("Converting Query And converting to List END");

		gstnsList.removeIf((gstr2ReconSummary.stream()
				.map(PermissibleITC10PercentDto::getGstin)
				.collect(Collectors.toList()))::contains);

		for (int i = 0; i < gstnsList.size(); i++) {
			PermissibleITC10PercentDto obj = new PermissibleITC10PercentDto();

			obj.setGstin(gstnsList.get(i));
			obj.setTaxAmount2a(BigDecimal.ZERO);
			obj.setPrTaxAmount(BigDecimal.ZERO);
			obj.setPrTaxAvlAmount(BigDecimal.ZERO);
			obj.setTotalItcAvailable(BigDecimal.ZERO);
			obj.setEligibleCredit(BigDecimal.ZERO);
			obj.setEligibleCrdPerc(BigDecimal.ZERO);
			obj.setMaxPermItc(BigDecimal.ZERO);
			obj.setExsCrdAvl(BigDecimal.ZERO);
			gstr2ReconSummary.add(obj);

		}
		Collections.sort(gstr2ReconSummary,
				(a, b) -> a.getGstin().compareToIgnoreCase(b.getGstin()));

		return gstr2ReconSummary;
	}

	private PermissibleITC10PercentDto convert(Object[] arr) {
		PermissibleITC10PercentDto obj = new PermissibleITC10PercentDto();

		obj.setGstin(arr[0] != null ? arr[0].toString() : "");
		obj.setTaxAmount2a(
				arr[1] != null ? (BigDecimal) arr[1] : BigDecimal.ZERO);
		obj.setPrTaxAmount(
				arr[2] != null ? (BigDecimal) arr[2] : BigDecimal.ZERO);
		obj.setPrTaxAvlAmount(
				arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setTotalItcAvailable(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setEligibleCredit(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setEligibleCrdPerc(
				arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setMaxPermItc(
				arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		obj.setExsCrdAvl(
				arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissibleITC10PerGstinDetailsDto> getPermissibleGstinDetails(
			List<String> gstinList, String toTaxPeriod, String fromTaxPeriod,
			List<String> docType, String reconType) {
		List<Object[]> list = null;

		if (docType == null || docType.isEmpty()) {
			docType.add("R");
			docType.add("C");
			docType.add("D");
		}

		String docTypes = String.join(",", docType);

		Integer fromTaxPer = Integer.valueOf(fromTaxPeriod.substring(2)
				.concat(fromTaxPeriod.substring(0, 2)));

		Integer toTaxPer = Integer.valueOf(
				toTaxPeriod.substring(2).concat(toTaxPeriod.substring(0, 2)));

		String gstins = String.join(",", gstinList);

		LOGGER.debug(
				"Calling stored proc BEGIN for Permissible ITC10p {} Gstin Details ",
				reconType);

		StoredProcedureQuery storedProc = null;
		if ("2A_PR".equalsIgnoreCase(reconType)) {
			storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_ITC_PERM_DETAIL");
		} else if ("2B_PR".equalsIgnoreCase(reconType)) {
			storedProc = entityManager.createStoredProcedureQuery(
					"USP_RECON_2BPR_ITC_PERM_DETAIL");
		}

		storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_GSTIN_LIST", gstins);

		storedProc.registerStoredProcedureParameter("P_FROM_TXPRD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_FROM_TXPRD", fromTaxPer);

		storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class,
				ParameterMode.IN);

		storedProc.setParameter("P_TO_TXPRD", toTaxPer);

		storedProc.registerStoredProcedureParameter("P_DOCTYPE", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_DOCTYPE", docTypes);

		LOGGER.debug("Converting Query And converting to List BEGIN");
		list = storedProc.getResultList();
		LOGGER.debug("Converting Query And converting to List END");

		long dbLoadStTime = System.currentTimeMillis();

		long dbLoadEndTime = System.currentTimeMillis();
		long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Time taken to load the Data"
							+ " from DB is '%d' millisecs Data count: '%s'",
					dbLoadTimeDiff, list.size());
			LOGGER.debug(msg);
		}

		List<PermissibleITC10PerGstinConvertDto> retList = list.stream()
				.map(o -> convertPer(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Collector<PermissibleITC10PerGstinConvertDto, ?, TaxBreakUpDetailsDto> collector3 = Collectors
				.reducing(new TaxBreakUpDetailsDto(), cpi -> convertDto(cpi),
						(cpt1, cpt2) -> merge(cpt1, cpt2));

		Collector<PermissibleITC10PerGstinConvertDto, ?, Map<String, TaxBreakUpDetailsDto>> collector2 = Collectors
				.groupingBy(obj -> obj.getType(), collector3);

		Collector<PermissibleITC10PerGstinConvertDto, ?, Map<String, Map<String, TaxBreakUpDetailsDto>>> collector1 = Collectors
				.groupingBy(o -> o.getGstin(), collector2);

		Map<String, Map<String, TaxBreakUpDetailsDto>> map1 = retList.stream()
				.collect(collector1);

		gstinList.removeIf((retList.stream()
				.map(PermissibleITC10PerGstinConvertDto::getGstin)
				.collect(Collectors.toList()))::contains);

		Map<String, TaxBreakUpDetailsDto> hashMap = new HashMap<>();
		hashMap.put("IGST", null);
		hashMap.put("CGST", null);
		hashMap.put("SGST", null);
		hashMap.put("CESS", null);
		hashMap.put("TOTAL", null);

		for (int i = 0; i < gstinList.size(); i++) {
			map1.putIfAbsent(gstinList.get(i), hashMap);
		}

		List<PermissibleITC10PerGstinDetailsDto> getPermissiableDetails = map1
				.entrySet().stream()
				.map(es -> createGstinDetails(es.getKey(), es.getValue()))
				.collect(Collectors.toList());

		Collections.sort(getPermissiableDetails,
				(a, b) -> a.getGstin().compareToIgnoreCase(b.getGstin()));

		return getPermissiableDetails;

	}

	private PermissibleITC10PerGstinDetailsDto createGstinDetails(String gstin,
			Map<String, TaxBreakUpDetailsDto> map) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin createGstinDetails in service "
					+ ".create PermissibleITC10PerGstinDetailsDto permissableGstin";
			LOGGER.debug(msg);
		}

		PermissibleITC10PerGstinDetailsDto permissableGstin = new PermissibleITC10PerGstinDetailsDto();

		permissableGstin.setGstin(gstin);
		List<TaxBreakUpDetailsDto> taxBreakUpDetails = map.entrySet().stream()
				.filter(e -> e.getValue() != null).map(es -> es.getValue())
				.collect(Collectors.toList());

		List<TaxBreakUpDetailsDto> taxBreakUpDetail = new ArrayList<>();
		taxBreakUpDetail.add(null);
		taxBreakUpDetail.add(null);
		taxBreakUpDetail.add(null);
		taxBreakUpDetail.add(null);

		if (CollectionUtils.isEmpty(taxBreakUpDetails)) {
			taxBreakUpDetails = new ArrayList<>();
			taxBreakUpDetails.add(null);
			taxBreakUpDetails.add(null);
			taxBreakUpDetails.add(null);
			taxBreakUpDetails.add(null);
			for (Map.Entry<String, TaxBreakUpDetailsDto> entry : map
					.entrySet()) {
				TaxBreakUpDetailsDto tax = new TaxBreakUpDetailsDto();

				tax.setType(entry.getKey());
				tax.setPrtaxAmount2a(BigDecimal.ZERO);
				tax.setPrTaxValue(BigDecimal.ZERO);
				tax.setPrTaxAvlAmount(BigDecimal.ZERO);
				tax.setTotalItcAvailable(BigDecimal.ZERO);
				tax.setEligibleCredit(BigDecimal.ZERO);
				tax.setEligibleCrdPerc(BigDecimal.ZERO);
				tax.setMaxPermItc(BigDecimal.ZERO);
				tax.setExsCreditAvl(BigDecimal.ZERO);
				if (entry.getKey().equalsIgnoreCase("IGST")) {
					taxBreakUpDetail.set(0, tax);
				} else if (entry.getKey().equalsIgnoreCase("CGST")) {
					taxBreakUpDetail.set(1, tax);
				} else if (entry.getKey().equalsIgnoreCase("SGST")) {
					taxBreakUpDetail.set(2, tax);
				} else if (entry.getKey().equalsIgnoreCase("CESS")) {
					taxBreakUpDetail.set(3, tax);
				} else if (entry.getKey().equalsIgnoreCase("TOTAL")) {
					permissableGstin.setPrtaxAmount2a(BigDecimal.ZERO);
					permissableGstin.setPrTaxValue(BigDecimal.ZERO);
					permissableGstin.setPrTaxAvlAmount(BigDecimal.ZERO);
					permissableGstin.setTotalItcAvailable(BigDecimal.ZERO);
					permissableGstin.setEligibleCredit(BigDecimal.ZERO);
					permissableGstin.setEligibleCrdPerc(BigDecimal.ZERO);
					permissableGstin.setMaxPermItc(BigDecimal.ZERO);
					permissableGstin.setExsCreditAvl(BigDecimal.ZERO);
					// taxBreakUpDetails.set(4, tax);
				}
			}
		} else {
			// TaxBreakUpDetailsDto total = null;
			TaxBreakUpDetailsDto igst = null;
			TaxBreakUpDetailsDto cgst = null;
			TaxBreakUpDetailsDto sgst = null;
			TaxBreakUpDetailsDto cess = null;

			for (int i = 0; i < taxBreakUpDetails.size(); i++) {

				if (taxBreakUpDetails.get(i).getType().equals("IGST")) {
					igst = taxBreakUpDetails.get(i);
				} else if (taxBreakUpDetails.get(i).getType().equals("CGST")) {
					cgst = taxBreakUpDetails.get(i);
				} else if (taxBreakUpDetails.get(i).getType().equals("SGST")) {
					sgst = taxBreakUpDetails.get(i);
				} else if (taxBreakUpDetails.get(i).getType().equals("CESS")) {
					cess = taxBreakUpDetails.get(i);
				} else if (taxBreakUpDetails.get(i).getType().equals("TOTAL")) {

					permissableGstin.setPrtaxAmount2a(
							taxBreakUpDetails.get(i).getPrtaxAmount2a());
					permissableGstin.setPrTaxValue(
							taxBreakUpDetails.get(i).getPrTaxValue());
					permissableGstin.setPrTaxAvlAmount(
							taxBreakUpDetails.get(i).getPrTaxAvlAmount());
					permissableGstin.setTotalItcAvailable(
							taxBreakUpDetails.get(i).getTotalItcAvailable());
					permissableGstin.setEligibleCredit(
							taxBreakUpDetails.get(i).getEligibleCredit());
					permissableGstin.setEligibleCrdPerc(
							taxBreakUpDetails.get(i).getEligibleCrdPerc());
					permissableGstin.setMaxPermItc(
							taxBreakUpDetails.get(i).getMaxPermItc());

					permissableGstin.setExsCreditAvl(
							taxBreakUpDetails.get(i).getExsCreditAvl());

					// total = taxBreakUpDetails.get(i);
				}
			}
			taxBreakUpDetail.set(0, igst);
			taxBreakUpDetail.set(1, cgst);
			taxBreakUpDetail.set(2, sgst);
			taxBreakUpDetail.set(3, cess);
			// taxBreakUpDetails.set(4, total);
		}

		permissableGstin.setTaxBreakUpDetails(taxBreakUpDetail);
		if (LOGGER.isDebugEnabled()) {
			String msg = "End createGstinDetails in service "
					+ ".create PermissibleITC10PerGstinDetailsDto permissableGstin";

			LOGGER.debug(msg);
		}
		return permissableGstin;
	}

	private TaxBreakUpDetailsDto convertDto(
			PermissibleITC10PerGstinConvertDto permi) {
		TaxBreakUpDetailsDto taxBreakUpDetails = new TaxBreakUpDetailsDto();
		taxBreakUpDetails.setType(permi.getType());
		taxBreakUpDetails.setTotalItcAvailable(permi.getTotalItcAvailable());
		taxBreakUpDetails.setPrTaxValue(permi.getPrTaxValue());
		taxBreakUpDetails.setPrtaxAmount2a(permi.getPrtaxAmount2a());
		taxBreakUpDetails.setPrTaxAvlAmount(permi.getPrTaxAvlAmount());
		taxBreakUpDetails.setMaxPermItc(permi.getMaxPermItc());
		taxBreakUpDetails.setEligibleCrdPerc(permi.getEligibleCrdPerc());
		taxBreakUpDetails.setEligibleCredit(permi.getEligibleCredit());
		taxBreakUpDetails.setExsCreditAvl(permi.getExsCreditAvl());
		return taxBreakUpDetails;

	}

	private TaxBreakUpDetailsDto merge(TaxBreakUpDetailsDto taxBrUp1,
			TaxBreakUpDetailsDto taxBrUp2) {

		String type = "";

		if (taxBrUp1.getType() != null && !taxBrUp1.getType().isEmpty()) {
			type = taxBrUp1.getType();
		} else {
			type = taxBrUp2.getType();
		}

		return new TaxBreakUpDetailsDto(type, taxBrUp2.getPrtaxAmount2a(),
				taxBrUp2.getPrTaxValue(), taxBrUp2.getPrTaxAvlAmount(),
				taxBrUp2.getTotalItcAvailable(), taxBrUp2.getEligibleCredit(),
				taxBrUp2.getEligibleCrdPerc(), taxBrUp2.getMaxPermItc(),
				taxBrUp2.getExsCreditAvl());
	}

	private PermissibleITC10PerGstinConvertDto convertPer(Object[] arr) {
		PermissibleITC10PerGstinConvertDto obj = new PermissibleITC10PerGstinConvertDto();
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin convertDto in service "
					+ ".converting the db response obj to "
					+ "PermissibleITC10PerGstinConvertDto obj";
			LOGGER.debug(msg);
		}

		obj.setGstin(arr[0] != null ? arr[0].toString() : "");
		obj.setType(arr[1] != null ? arr[1].toString() : "");
		obj.setPrtaxAmount2a(
				arr[2] != null ? (BigDecimal) arr[2] : BigDecimal.ZERO);
		obj.setPrTaxValue(
				arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setPrTaxAvlAmount(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setTotalItcAvailable(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setEligibleCredit(
				arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setEligibleCrdPerc(
				arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		obj.setMaxPermItc(
				arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		obj.setExsCreditAvl(
				arr[9] != null ? (BigDecimal) arr[9] : BigDecimal.ZERO);

		if (LOGGER.isDebugEnabled()) {
			String msg = "End convertDto in service. Converted to  "
					+ "PermissibleITC10PerGstinConvertDto obj";

			LOGGER.debug(msg);
		}
		return obj;
	}

}
