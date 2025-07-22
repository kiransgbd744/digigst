package com.ey.advisory.app.data.services.einvseries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Bajjuri Mahendar Reddy
 *
 */
@Slf4j
@Component
@Scope("prototype")
public class Invoice2BSeriesGenerator {

	private static final String CLASS_NAME = Invoice2BSeriesGenerator.class
			.getName();

	private List<InvoiceSeriesDTO> invoiceSeriesReponseList = null;

	/**
	 * Generate the InvoiceSeries
	 *
	 * @param taxPeriod
	 * @param gstinId
	 * @param returnType
	 * @param groupCode
	 * @return string
	 */
	public List<InvoiceSeriesDTO> generateInvoiceSeries(
			List<InvoiceSeries> invoiceSerieList, String taxPeriod,
			String gstinId) {
		try {
			invoiceSeriesReponseList = new ArrayList<>();
			prepareInvoiceSeriesList(invoiceSerieList, taxPeriod, gstinId);
			return invoiceSeriesReponseList;
		} catch (Exception e) {
			LOGGER.error("Exception while generating Invoice Series", e);
			throw new AppException(e.getMessage());
		}
	}

	/**
	 * prepareInvoiceSeriesList
	 *
	 * @param invoiceSerieList
	 * @param taxPeriod
	 * @param gstinId
	 */
	private void prepareInvoiceSeriesList(List<InvoiceSeries> invoiceSerieList,
			String taxPeriod, String gstin) {
		LOGGER.debug(CLASS_NAME
				+ "::  Entring into prepareInvoiceSeriesList method");
		try {
			Map<String, List<InvoiceSeries>> groupByDocType = invoiceSerieList
					.stream()
					.collect(Collectors.groupingBy(InvoiceSeries::getDocType));
			Set<String> invoiceTypeKeySet = groupByDocType.keySet().stream()
					.collect(Collectors.toSet());
			for (String invoiceType : invoiceTypeKeySet) {
				List<String> listofDocNumbersList = getListofDocNumbersbyInvoiceSeriesType(
						groupByDocType, invoiceType);
				prepareFilterNonDelimiterDetials(taxPeriod, gstin, invoiceType,
						listofDocNumbersList);
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesList for gstin %s and taxPeriod %s",
					gstin, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareFilterNonDelimiterDetials
	 *
	 * @param taxPeriod
	 * @param gstinId
	 * @param invoiceType
	 * @param list
	 */
	private void prepareFilterNonDelimiterDetials(String taxPeriod,
			String gstinId, String invoiceType, List<String> list) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareFilterNonDelimiterDetials method , invoice serices list ::"
					+ list + ", and taxPeriod :: " + taxPeriod
					+ ", and Documnet type:: " + invoiceType);
			Map<Integer, List<String>> listofElementsByLength = getElemenetsListByLength(
					list);
			Set<Integer> numberTypeKeySet = listofElementsByLength.keySet();
			for (Integer numberType : numberTypeKeySet) {
				List<String> listofDocumentsListByFirstLetter = listofDocumentsListByNumber(
						listofElementsByLength, numberType);
				for (String element : listofDocumentsListByFirstLetter) {
					if (listofDocumentsListByFirstLetter.contains(element)) {
						List<String> fliteredListbyOnlyNumbers = filterElementsWithRegExp(
								listofDocumentsListByFirstLetter, "[\\d]+");
						if (hasListEmpty(fliteredListbyOnlyNumbers)) {
							LOGGER.debug(CLASS_NAME
									+ " :: FilteredList by OnlyNumberPattren ::"
									+ fliteredListbyOnlyNumbers);
							listofDocumentsListByFirstLetter = removeFilterListFromMainList(
									listofDocumentsListByFirstLetter,
									fliteredListbyOnlyNumbers);
							prepareElementsList(taxPeriod, gstinId, invoiceType,
									fliteredListbyOnlyNumbers, true, false,
									false, false);
						} else {
							List<String> filteredOnlyAlphabetsList = filterElementsWithRegExp(
									listofDocumentsListByFirstLetter, "^\\D+$");
							if (hasListEmpty(filteredOnlyAlphabetsList)) {
								LOGGER.debug(CLASS_NAME
										+ " :: FilteredList by OnlyAlphabetsPattren ::"
										+ filteredOnlyAlphabetsList);
								listofDocumentsListByFirstLetter = removeFilterListFromMainList(
										listofDocumentsListByFirstLetter,
										filteredOnlyAlphabetsList);
								prepareElementsList(taxPeriod, gstinId,
										invoiceType, filteredOnlyAlphabetsList,
										false, false, false, true);
							} else {
								List<String> fliteredListbyDelimterNumbers = getFliteredListbyDelimterNumbers(
										listofDocumentsListByFirstLetter, "",
										invoiceType, gstinId, taxPeriod);
								if (hasListEmpty(
										fliteredListbyDelimterNumbers)) {
									LOGGER.debug(CLASS_NAME
											+ " :: FilteredList by NumbersWithSpecialLettersPattren ::"
											+ fliteredListbyDelimterNumbers);
									listofDocumentsListByFirstLetter = removeFilterListFromMainList(
											listofDocumentsListByFirstLetter,
											fliteredListbyDelimterNumbers);
									prepareElementsList(taxPeriod, gstinId,
											invoiceType,
											fliteredListbyDelimterNumbers,
											false, false, true, false);
								} else {
									List<String> fliteredListbyAlphaNumbers = filterElementsWithRegExp(
											listofDocumentsListByFirstLetter,
											"[a-zA-Z0-9]+");
									if (hasListEmpty(
											fliteredListbyAlphaNumbers)) {
										LOGGER.debug(CLASS_NAME
												+ " :: FilteredList by OnlyAlphaNumaricLettersPattren ::"
												+ fliteredListbyAlphaNumbers);
										listofDocumentsListByFirstLetter = removeFilterListFromMainList(
												listofDocumentsListByFirstLetter,
												fliteredListbyAlphaNumbers);
										prepareElementsList(taxPeriod, gstinId,
												invoiceType,
												fliteredListbyAlphaNumbers,
												false, true, false, false);
									} else {
										LOGGER.debug(CLASS_NAME
												+ " :: Remining all lettersPattren ::"
												+ listofDocumentsListByFirstLetter);
										List<String> tempList = listofDocumentsListByFirstLetter;
										listofDocumentsListByFirstLetter = removeFilterListFromMainList(
												listofDocumentsListByFirstLetter,
												listofDocumentsListByFirstLetter);
										prepareElementsList(taxPeriod, gstinId,
												invoiceType, tempList, false,
												false, false, false);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareFilterNonDelimiterDetials for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * @param fliteredListbyOnlyNumbers
	 * @param list
	 * @return list
	 */
	private List<String> removeFilterListFromMainList(List<String> list1,
			List<String> list2) {
		return list1.stream().filter(element -> !list2.contains(element))
				.collect(Collectors.toList());
	}

	/**
	 * filterElementsWithRegExp
	 *
	 * @param list
	 * @param regExp
	 * @return List
	 */
	private List<String> filterElementsWithRegExp(List<String> list,
			String regExp) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into filterElementsWithRegExp method , invoice serices list ::"
				+ list + ", and regExp::" + regExp);
		return list.stream().filter(input -> input.matches(regExp))
				.collect(Collectors.toList());
	}

	/**
	 * getElemenetsListByLength
	 *
	 * @param list
	 * @return list
	 */
	private Map<Integer, List<String>> getElemenetsListByLength(
			List<String> list) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getElemenetsListByLength method , invoice serices list ::"
				+ list);
		return list.stream().collect(Collectors
				.groupingBy(element -> element.length(), Collectors.toList()));
	}

	/**
	 * listofDocumentsListByNumber
	 *
	 * @param list
	 * @param numberType
	 * @return list
	 */
	private List<String> listofDocumentsListByNumber(
			Map<Integer, List<String>> list, Integer numberType) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into listofDocumentsListByNumber method , invoice serices list ::"
				+ list + ", and numberType:: " + numberType);
		return list.entrySet().stream()
				.filter(element -> element.getKey().equals(numberType))
				.map(Map.Entry::getValue).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	/**
	 * prepareElementsList
	 *
	 * @param taxPeriod
	 * @param gstinId
	 * @param invoiceType
	 * @param list
	 * @param isNumbers
	 */
	private void prepareElementsList(String taxPeriod, String gstinId,
			String invoiceType, List<String> list, boolean isOnlyNumbers,
			boolean isAlphaNumarics, boolean isNumarics, boolean isOnlyAlpha) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareElementsList method , invoice serices list ::"
					+ list + ", and taxPeriod :: " + taxPeriod
					+ ", and Documnet type:: " + invoiceType
					+ " and isNumbers ==>" + isOnlyNumbers
					+ "&& isAlphaNumarics ===>" + isAlphaNumarics
					+ " && isNumarics ==> " + isNumarics
					+ "&& isAlphaPattrenOnly ===>" + isOnlyAlpha);
			Map<Object, List<String>> groupByAlphaPattrenMap = groupingByAlphaPattren(
					list, isOnlyNumbers, isAlphaNumarics, isNumarics,
					isOnlyAlpha);
			Set<Object> groupByAlphaPattren = groupByAlphaPattrenMap.keySet();
			for (Object pattrenType : groupByAlphaPattren) {
				List<String> listofDocumentsListByAlphaPattren = getListofDocumentsListByAlphaPattren(
						groupByAlphaPattrenMap, pattrenType);
				prepareInvoiceSeriesByAlphaPattrenFirstLetter(pattrenType,
						invoiceType, listofDocumentsListByAlphaPattren, gstinId,
						taxPeriod, isOnlyNumbers, isOnlyAlpha);
			}
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while prepareElementsList ::" + e);
			String errMsg = String.format(
					"Getting error while prepareElementsList for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareInvoiceSeriesByAlphaPattrenFirstLetter
	 *
	 *
	 * @param type
	 * @param invoiceType
	 * @param list
	 * @param gstinId
	 * @param taxPeriod
	 * @param isNumbers
	 * @param isOnlyAlpha
	 */
	private void prepareInvoiceSeriesByAlphaPattrenFirstLetter(Object type,
			String invoiceType, List<String> list, String gstinId,
			String taxPeriod, boolean isOnlyNumbers, boolean isOnlyAlpha) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareInvoiceSeriesByAlphaPattrenFirstLetter method , invoice serices list ::"
					+ list + ", and object type:: " + type
					+ ", and Documnet type:: " + type + " isNumbers ===>"
					+ isOnlyNumbers + "  && isOnlyAlpha ===>" + isOnlyAlpha);
			Pattern pattern = Pattern.compile("[0-9]");
			Matcher matcher = pattern.matcher(type.toString());
			if (matcher.find()) {
				List<String> filteredByStartsWithNumber = getInvoiceSeriesByType(
						list, type);
				if (isOnlyNumbers) {
					prepareInvoiceSeriesDetails(filteredByStartsWithNumber, "",
							invoiceType, gstinId, taxPeriod);
				} else {
					prepareInvoiceSeriesWithNumbers(filteredByStartsWithNumber,
							invoiceType, gstinId, taxPeriod);
				}
			} else {
				List<String> filteredByStartsWithAlphabets = getInvoiceSeriesByType(
						list, type);
				if (isOnlyAlpha) {
					prepareInvoiceSeriesDetails(filteredByStartsWithAlphabets,
							"", invoiceType, gstinId, taxPeriod);
				} else {
					prepareInvoiceSeriesByAlphaPattren(type,
							filteredByStartsWithAlphabets, invoiceType, gstinId,
							taxPeriod);
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesByAlphaPattrenFirstLetter for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareInvoiceSeriesWithNumbers
	 *
	 * @param list
	 * @param invoiceType
	 * @param taxPeriod
	 * @param gstinId
	 */
	private void prepareInvoiceSeriesWithNumbers(List<String> list,
			String invoiceType, String gstinId, String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareInvoiceSeriesWithNumbers method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType);
			prepareInvoiceSeriesByAlphaPattren("", list, invoiceType, gstinId,
					taxPeriod);
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesWithNumbers for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}

	}

	/**
	 * prepareInvoiceSeriesByAlphaPattren
	 *
	 * @param pattrenType
	 * @param list
	 * @param invoiceType
	 * @param taxPeriod
	 * @param gstinId
	 */
	private void prepareInvoiceSeriesByAlphaPattren(Object pattrenType,
			List<String> list, String invoiceType, String gstinId,
			String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareInvoiceSeriesByAlphaPattren method , invoice serices list ::"
					+ list + ", and PattrenType:: " + pattrenType
					+ ", and Documnet type:: " + invoiceType);
			List<String> fliteredListbyDelimter = filetrElementsByDelimiters(
					list, "/");
			if (hasListEmpty(fliteredListbyDelimter)) {
				List<String> fliteredListbyDelimterNumbers = getFliteredListbyDelimterNumbers(
						fliteredListbyDelimter, pattrenType, invoiceType,
						gstinId, taxPeriod);
				if (hasListEmpty(fliteredListbyDelimterNumbers)) {
					prepareInvoiceSeriesDetails(fliteredListbyDelimterNumbers,
							pattrenType, invoiceType, gstinId, taxPeriod);
				} else {
					prepareElementsByDelimiter(fliteredListbyDelimter,
							invoiceType, gstinId, taxPeriod);
				}
			}
			List<String> fliteredList = list.stream()
					.filter(input -> !input.contains("/"))
					.collect(Collectors.toList());
			prepareInvoiceSeriesByAlphaPattrenWithhyphen(fliteredList,
					pattrenType, invoiceType, gstinId, taxPeriod);
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesByAlphaPattren for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareInvoiceSeriesByAlphaPattrenWithhyphen
	 *
	 * @param list
	 * @param pattrenType
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */
	private void prepareInvoiceSeriesByAlphaPattrenWithhyphen(List<String> list,
			Object pattrenType, String invoiceType, String gstinId,
			String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareInvoiceSeriesByAlphaPattrenWithhyphen method , invoice serices list ::"
					+ list + ", and PattrenType:: " + pattrenType
					+ ", and Documnet type:: " + invoiceType);
			List<String> fliteredListbyDelimter = filetrElementsByDelimiters(
					list, "-");
			if (hasListEmpty(fliteredListbyDelimter)) {
				List<String> fliteredListbyDelimterNumbers = getFliteredListbyDelimterNumbers(
						fliteredListbyDelimter, pattrenType, invoiceType,
						gstinId, taxPeriod);
				if (hasListEmpty(fliteredListbyDelimterNumbers)) {
					prepareInvoiceSeriesDetails(fliteredListbyDelimterNumbers,
							pattrenType, invoiceType, gstinId, taxPeriod);
				} else {
					prepareElementsByDelimiter(fliteredListbyDelimter,
							invoiceType, gstinId, taxPeriod);
				}

			}
			List<String> fliteredList = list.stream()
					.filter(input -> !input.contains("-"))
					.collect(Collectors.toList());
			if (hasListEmpty(fliteredList)) {
				List<String> fliteredListbyDelimterNumbers = getFliteredListbyDelimterNumbers(
						fliteredListbyDelimter, pattrenType, invoiceType,
						gstinId, taxPeriod);
				if (hasListEmpty(fliteredListbyDelimterNumbers)) {
					prepareInvoiceSeriesDetails(fliteredList, pattrenType,
							invoiceType, gstinId, taxPeriod);
				} else {
					prepareElementsByDelimiterNoSpecial(fliteredList,
							invoiceType, gstinId, taxPeriod);
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesByAlphaPattrenWithhyphen for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}

	}

	/**
	 * prepareElementsByDelimiterNoSpecial
	 *
	 * @param list
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */
	private void prepareElementsByDelimiterNoSpecial(List<String> list,
			String invoiceType, String gstinId, String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareInvoiceSeriesByAlphaPattren method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType);
			Map<String, List<String>> filterByDelimiterMap = fliteredDocListbyDelimter(
					list);
			Set<String> delimiterPattrenType = filterByDelimiterMap.keySet();
			for (Object type : delimiterPattrenType) {
				List<String> listOfDocumentsDelimiter = getListOfDelimiter(type,
						filterByDelimiterMap);
				prepareInvoiceSeriesDetails(listOfDocumentsDelimiter, type,
						invoiceType, gstinId, taxPeriod);
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareElementsByDelimiterNoSpecial for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareElementsByDelimiter
	 *
	 * @param list
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */
	private void prepareElementsByDelimiter(List<String> list,
			String invoiceType, String gstinId, String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareElementsByDelimiter method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType);
			Map<String, List<String>> filterByDelimiterMap = fliteredDocListbyDelimter(
					list);
			Set<String> delimiterPattrenType = filterByDelimiterMap.keySet();
			for (Object type : delimiterPattrenType) {
				List<String> listOfDocumentsDelimiter = getListOfDelimiter(type,
						filterByDelimiterMap);
				prepareInvoiceSeriesDetailsOfDelimiter(listOfDocumentsDelimiter,
						type, invoiceType, gstinId, taxPeriod);
			}
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while prepareElementsByDelimiter ::"
					+ e);
		}
	}

	/**
	 * prepareInvoiceSeriesDetailsOfDelimiter
	 *
	 * @param list
	 * @param pattrenType
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */
	private void prepareInvoiceSeriesDetailsOfDelimiter(List<String> list,
			Object pattrenType, String invoiceType, String gstinId,
			String taxPeriod) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into prepareElementsByDelimiter method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType);
			String regExp = "(?<=\\D)(?=\\d+\\b)";
			Map<String, List<String>> filterByDelimiterMap = list.stream()
					.collect(Collectors.groupingBy(
							input -> input.split(
									regExp)[input.split(regExp).length - 1],
							Collectors.toList()));
			Set<Object> delimiterPattren = filterByDelimiterMap.keySet()
					.stream().sorted(Comparator.reverseOrder())
					.collect(Collectors.toSet());
			for (Object type : delimiterPattren) {
				String regExp1 = "\\d$";
				if (type.toString().matches(regExp1)) {
					List<String> listOfDocumentsDelimiter = getListOfDelimiter(
							type, filterByDelimiterMap);
					parseAlphaNumaricSeries(listOfDocumentsDelimiter,
							pattrenType, invoiceType, gstinId, taxPeriod, false,
							type.toString());
				} else {
					parseAlphaNumaricSeries(list, pattrenType, invoiceType,
							gstinId, taxPeriod, true, type.toString());
					break;
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesDetailsOfDelimiter for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}

	}

	/**
	 * parseAlphaNumaricSeries
	 *
	 * @param list
	 * @param pattrenType
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 * @param isMoreThenOne
	 * @param elementType
	 */
	private void parseAlphaNumaricSeries(List<String> list, Object pattrenType,
			String invoiceType, String gstinId, String taxPeriod,
			boolean isMoreThenOne, String elementType) {
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into parseAlphaNumaricSeries method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType
					+ " , && isMoreThenOne::" + isMoreThenOne
					+ ", ElementType==>" + elementType + " and list size-->{}",
					list.size());
			if (list.size() == 1) {
				prepareInvoiceSeriesDetails(list, pattrenType, invoiceType,
						gstinId, taxPeriod);
			} else {
				String regExp1 = "(?<=[a-zA-Z0\\D]*+)";
				String regExp2 = "(?<=[a-zA-Z\\W]*+)";
				String regExp3 = "(?<=[a-zA-Z\\W]*+)";
				String regExpPattren = isMoreThenOne
						? elementType.length() <= 3 ? regExp1 : regExp3
						: regExp2;
				Map<String, List<String>> filterByDelimiterMap = list.stream()
						.collect(Collectors.groupingBy(
								input -> input.split(regExpPattren)[0]
										+ input.split(regExpPattren)[1],
								Collectors.toList()));
				List<Object> delimiterPattren = filterByDelimiterMap.keySet()
						.stream().sorted().collect(Collectors.toList());
				for (Object type : delimiterPattren) {
					List<String> listOfDocumentsDelimiter = getListOfDelimiter(
							type, filterByDelimiterMap);
					prepareInvoiceSeriesDetails(listOfDocumentsDelimiter,
							pattrenType, invoiceType, gstinId, taxPeriod);
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while parseAlphaNumaricSeries for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	private List<String> getListOfDelimiter(Object type,
			Map<String, List<String>> docsMap) {
		return docsMap.entrySet().stream().filter(e -> e.getKey().equals(type))
				.map(Map.Entry::getValue).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private Map<String, List<String>> fliteredDocListbyDelimter(
			List<String> list) {
		String regExp = "(?<=\\D)(?=\\d+\\b)";
		return list.stream().collect(Collectors.groupingBy(
				input -> input.split(regExp)[0], Collectors.toList()));
	}

	/**
	 * filetrElementsByDelimiters
	 *
	 * @param list
	 * @param regExp
	 * @return list
	 */
	private List<String> filetrElementsByDelimiters(List<String> list,
			String regExp) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into filetrElementsByDelimiters method , invoice serices list ::"
				+ list);
		return list.stream().filter(input -> input.contains(regExp))
				.collect(Collectors.toList());
	}

	/**
	 * getFliteredListbyDelimterNumbers
	 *
	 * @param list
	 * @param pattrenType
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 * @return
	 */

	private List<String> getFliteredListbyDelimterNumbers(List<String> list,
			Object pattrenType, String invoiceType, String gstinId,
			String taxPeriod) {
		List<String> fliteredListbyDelimterNumbers = null;
		try {
			LOGGER.debug(CLASS_NAME + "::"
					+ "Entering into getFliteredListbyDelimterNumbers method , invoice serices list ::"
					+ list + ", and Documnet type:: " + invoiceType);
			String regExp = "[^a-zA-Z]+";
			fliteredListbyDelimterNumbers = list.stream()
					.filter(input -> input.matches(regExp))
					.collect(Collectors.toList());
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesDetailsOfDelimiter for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);

		}
		return fliteredListbyDelimterNumbers;
	}

	/**
	 * prepareInvoiceSeriesDetails
	 *
	 * @param list
	 * @param delimterType
	 * @param invoiceType
	 * @param taxPeriod
	 * @param gstinId
	 */
	private void prepareInvoiceSeriesDetails(List<String> list,
			Object delimterType, String invoiceType, String gstinId,
			String taxPeriod) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into prepareInvoiceSeriesDetails method , invoice serices list ::"
				+ list + ", and Documnet type:: " + invoiceType);
		try {

			prepareFinalinvoiceSeries(gstinId, taxPeriod,
					delimterType.toString(), String.valueOf(list.size()));

		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesDetails for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareInvoiceSeriesForAlphaPattren
	 * 
	 * @param gstinId
	 * @param taxPeriod
	 * @param invoiceType
	 * @param invsAlphaPattrenDto
	 * @param list
	 */

	/**
	 * prepareInvoiceSeriesForOneElement
	 * 
	 * @param list
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */

	/**
	 * prepareFinalinvoiceSeries
	 *
	 * @param gstinId
	 * @param taxPeriod
	 * @param fromSeries
	 * @param toSeries
	 * @param totalDocumentsIssued
	 * @param totalCancelledDocuments
	 * @param netNumber
	 * @param invoiceType
	 */
	private void prepareFinalinvoiceSeries(String gstinId, String taxPeriod,
			String pattern, String netNumber) {
		InvoiceSeriesDTO invoiceSeriesDTO = new InvoiceSeriesDTO();
		try {
			invoiceSeriesDTO.setGstin(gstinId);
			invoiceSeriesDTO.setTaxPeriod(taxPeriod);
			invoiceSeriesDTO.setPattern(pattern);
			invoiceSeriesDTO.setNetNumber(netNumber);
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while prepareFinalinvoiceSeries ::"
					+ e);
		}
		invoiceSeriesReponseList.add(invoiceSeriesDTO);
	}

	/**
	 * Validate the list
	 *
	 * @param inputList
	 * @return Boolean value
	 */
	private boolean hasListEmpty(List<String> inputList) {
		return inputList.size() == 0 && inputList.isEmpty() ? false : true;
	}

	/**
	 * Collect the cancelled Invoices count.
	 *
	 * @param list
	 * @param minValue
	 * @param maxValue
	 * @return List
	 */

	/**
	 * Get the InvoiceSeriesByType
	 *
	 * @param list
	 * @param type
	 * @return List
	 */
	private List<String> getInvoiceSeriesByType(List<String> list,
			Object type) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getInvoiceSeriesByType method , invoice serices list ::"
				+ list + ", and object type:: " + type);
		return list.stream().filter(e -> e.startsWith(type.toString()))
				.collect(Collectors.toList());
	}

	/**
	 * convertStringToLong
	 *
	 * @param list
	 * @return List
	 */

	/**
	 * Get the ListofDocumentsListByAlphaPattren
	 *
	 * @param alphaPattrenMap
	 * @param pattrenType
	 * @return List
	 */
	private List<String> getListofDocumentsListByAlphaPattren(
			Map<Object, List<String>> alphaPattrenMap, Object pattrenType) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getListofDocumentsListByAlphaPattren method, DocumnetMap ::"
				+ alphaPattrenMap + ", And alpha-pattren type ::"
				+ pattrenType);
		return alphaPattrenMap.entrySet().stream()
				.filter(element -> element.getKey().equals(pattrenType))
				.map(Map.Entry::getValue).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	/**
	 * GroupingByAlphaPattren
	 *
	 * @param list
	 * @param isNumbers
	 * @param isNumarics
	 * @param isOnlyAlpha
	 * @return Map
	 */
	private Map<Object, List<String>> groupingByAlphaPattren(List<String> list,
			boolean isOnlyNumbers, boolean isAlphaNumarics, boolean isNumarics,
			boolean isOnlyAlpha) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into groupingByAlphaPattren method, InvoiceList ::"
				+ list + " and isNumbersOnly ==>" + isOnlyNumbers
				+ "&& isAlphaNumaricsOnly ===>" + isAlphaNumarics
				+ " && isNumaricsOnly ==> " + isNumarics
				+ "&& isAlphaPattrenOnly ===>" + isOnlyAlpha);
		String isOnlyNumberRegExp = "(?<=[a-zA-Z[1-9]\\D]*+)";
		String isNumaricsRegExp = "(?<=[a-zA-Z0\\D]*+)";
		String isOnlyAlphaRegExp = "(?<=[a-zA-Z\\D]*+)";
		String isNotNumberRegExp = "(?<=[a-zA-Z0\\D]*+)";
		String isAlphaNumaricRegExp = "(?<=[a-zA-Z0\\D]*+)";
		String regexPattren = isOnlyNumbers ? isOnlyNumberRegExp
				: isAlphaNumarics ? isAlphaNumaricRegExp
						: isNumarics ? isNumaricsRegExp
								: isOnlyAlpha ? isOnlyAlphaRegExp
										: isNotNumberRegExp;
		return list.stream().collect(Collectors.groupingBy(
				input -> input.split(regexPattren)[0], Collectors.toList()));
	}

	/**
	 * Get the ListofDocNumbersbyInvoiceSeriesType
	 *
	 * @param documentMap
	 * @param invoiceType
	 * @return List
	 */
	private List<String> getListofDocNumbersbyInvoiceSeriesType(
			Map<String, List<InvoiceSeries>> documentMap, String invoiceType) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getListofDocNumbersbyInvoiceSeriesType method, DocumnetMap ::"
				+ documentMap + ", And Document Type ::" + invoiceType);
		return documentMap.entrySet().stream()
				.filter(element -> element.getKey()
						.equalsIgnoreCase(invoiceType))
				.map(Map.Entry::getValue).flatMap(List::stream)
				.map(InvoiceSeries::getDocNum).collect(Collectors.toList());
	}

}
