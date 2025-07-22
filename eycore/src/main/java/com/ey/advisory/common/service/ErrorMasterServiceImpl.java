package com.ey.advisory.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import jakarta.annotation.PostConstruct;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;
import com.ey.advisory.core.async.repositories.master.MasterErrorCatalogEntityRepository;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("ErrorMasterServiceImpl")
@Slf4j
public class ErrorMasterServiceImpl implements ErrorMasterService {

	@Autowired
	@Qualifier("MasterErrorCatalogEntityRepository")
	private MasterErrorCatalogEntityRepository errRepo;

	private Map<String, Pair<String, String>> errMap;

	private static final String ERR_MSG = "Error Master doesn't have any Records to Load";

	@PostConstruct
	public void init() {
		List<MasterErrorCatalogEntity> errList = errRepo.findAll();
		if (errList == null || errList.isEmpty()) {
			LOGGER.warn(ERR_MSG);
			return;
		}
		errMap = new HashMap<>();
		errList.forEach(err -> errMap.put(
				String.format("%s|%s", err.getErrorCode(), err.getTableType()),
				new Pair<String, String>(err.getColumnName(),
						err.getErrorDesc())));
	}

	@Override
	public String findErrDescByErrorCode(String errCode, String tableType) {
		MasterErrorCatalogEntity errEntity = errRepo
				.findByErrorCodeAndTableType(errCode, tableType);
		return errEntity != null ? errEntity.getErrorDesc() : "";
	}

	@Override
	public String findErrorInfoByErrorCodes(List<String> errcodes,
			String tableType) {
		if (errMap == null)
			throw new AppException(ERR_MSG);
		Set<String> errPairs = new HashSet<>();
		errcodes.forEach(errCode -> {
			if (errCode != null && errCode.trim().isEmpty()) {
				return;
			}
			if (Strings.isNullOrEmpty(errCode)) {
				LOGGER.error(
						"Error Code is blank {} and Table Type {} is Configured,"
								+ " Hence Skipping to next error",
						errCode, tableType);
				return;
			}
			Pair<String, String> errDetail = errMap
					.get(String.format("%s|%s", errCode, tableType));
			if (errDetail == null) {
				LOGGER.error(
						"Error Code {} and Table Type {} is Configured,"
								+ " Hence Skipping to next error",
						errCode, tableType);
				errPairs.add(String.format(
						"%s- Error Description is not available in the System",
						errCode));
				return;
			}
			String errDesc = errDetail.getValue1().replace(".", "");
			String errCol = errDetail.getValue0();
			errPairs.add(new StringJoiner("-")
					.add(String.format("%s(%s)", errCode, errCol)).add(errDesc)
					.toString());
		});

		return String.join(", ", errPairs);
	}

	@Override
	public String findErrorDescByCodes(List<String> errcodes,
			String tableType) {
		if (errMap == null)
			throw new AppException(ERR_MSG);
		List<String> errPairs = new ArrayList<>();
		errcodes.forEach(errCode -> {
			Pair<String, String> errDetail = errMap
					.get(String.format("%s|%s", errCode, tableType));
			if (errDetail == null) {
				LOGGER.error(
						"Error Code {} and Table Type {} is not Configured,"
								+ " Hence Skipping to next error",
						errCode, tableType);
				errPairs.add(String.format(
						"%s- Error Description is not available in the System",
						errCode));
				return;
			}
			errPairs.add(errDetail.getValue1());
		});
		return String.join(", ", errPairs);
	}

	@Override
	public String findHsnDescByCodes(List<String> hsncodes) {
		if (errMap == null)
			throw new AppException(ERR_MSG);
		List<String> hsnPairs = new ArrayList<>();
		hsncodes.forEach(
				hsnCode -> hsnPairs.add(errMap.get(hsnCode).getValue1()));

		return String.join(", ", hsnPairs);
	}

	@Override
	public String findInfoByErrorcodeWithoutIndex(List<String> errcodes,
			String tableType) {
		if (errMap == null)
			throw new AppException(ERR_MSG);
		Set<String> errPairs = new HashSet<>();
		errcodes.forEach(errCode -> {
			Pair<String, String> errDetail = errMap
					.get(String.format("%s|%s", errCode, tableType));
			if (errDetail == null) {
				LOGGER.error(
						"Error Code {} and Table Type {} is Configured,"
								+ " Hence Skipping to next error",
						errCode, tableType);
				errPairs.add(String.format(
						"%s- Error Description is not available in the System",
						errCode));
				return;
			}
			String errDesc = errDetail.getValue1();
			/* String errCol = errDetail.getValue0(); */
			errPairs.add(new StringJoiner("-").add(String.format("%s", errCode))
					.add(errDesc).toString());
		});

		return String.join(", ", errPairs);
	}

	@Override
	public String findDynamicErrorInfoByErrorCodes(List<String> errcodes,
			String tableType, Map<String, String> dynamicMap) {
		if (errMap == null)
			throw new AppException(ERR_MSG);
		Set<String> errPairs = new HashSet<>();
		errcodes.forEach(errCode -> {
			Pair<String, String> errDetail = errMap
					.get(String.format("%s|%s", errCode, tableType));
			if (errDetail == null) {
				LOGGER.error(
						"Error Code {} and Table Type {} is not Configured,"
								+ " Hence Skipping to next error",
						errCode, tableType);
				errPairs.add(String.format(
						"%s- Error Description is not available in the System",
						errCode));
				return;
			}

			String errDesc = errDetail.getValue1().replace(".", "");
			String errCol = errDetail.getValue0();
			String dynamicErrDesc = dynamicMap.containsKey(errCode)
					? String.format(errDesc, dynamicMap.get(errCode)) : errDesc;

			errPairs.add(new StringJoiner("-")
					.add(String.format("%s(%s)", errCode, errCol))
					.add(dynamicErrDesc).toString());
		});

		return String.join(", ", errPairs);
	}
}
