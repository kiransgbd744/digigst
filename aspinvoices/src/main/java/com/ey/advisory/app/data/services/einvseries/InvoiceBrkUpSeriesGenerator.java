package com.ey.advisory.app.data.services.einvseries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.LongStreamEx;

/**
 * @author Bajjuri Mahendar Reddy
 *
 */
@Slf4j
@Component
@Scope("prototype")
public class InvoiceBrkUpSeriesGenerator {

	private static final String CLASS_NAME = InvoiceBrkUpSeriesGenerator.class
			.getName();

	private List<InvoiceSeriesDTO> invoiceSeriesReponseList = null;

	private List<String> delimiterList = new ArrayList<>();

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
		List<String> concurrentList1 = new CopyOnWriteArrayList<>(list1);
		List<String> concurrentList2 = new CopyOnWriteArrayList<>(list2);
		concurrentList1.removeAll(concurrentList2);
		return concurrentList1;
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
			Long totalDocumentsIssued = null;
			Long totalCancelledDocuments = null;
			Long netNumber = null;
			List<Long> minAndMaxDelimiterList = null;
			list = list.stream().distinct().collect(Collectors.toList());
			if (list.size() == 1) {
				prepareInvoiceSeriesForOneElement(list, invoiceType, gstinId,
						taxPeriod, delimterType.toString());
			} else {
				InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto = new InvoiceSeriesAlphaPattrenDTO();
				list = filterInvoicesByDelimiter(list, invsAlphaPattrenDto);
				LOGGER.debug(CLASS_NAME + "::"
						+ "After filtering element details ::" + list);
				List<String> filteredList = list.stream()
						.filter(inputValue -> inputValue.matches("^\\D+$"))
						.collect(Collectors.toList());
				if (hasListEmpty(filteredList)) {
					prepareInvoiceSeriesForAlphaPattren(gstinId, taxPeriod,
							invoiceType, invsAlphaPattrenDto, filteredList,
							delimterType.toString());
				} else {
					List<Long> getNumberList = convertInvoiceNumberFromStringToLong(
							list);
					LongUnaryOperator next = i -> i + 1;
					List<List<Long>> result = LongStreamEx.of(getNumberList)
							.boxed()
							.groupRuns((i1, i2) -> next.applyAsLong(i1) == i2)
							.toList();
					for (List<Long> getNumbersList : result) {

						StringBuilder maxBuilder = new StringBuilder();
						StringBuilder minBuilder = new StringBuilder();
						LongSummaryStatistics longSummaryStatistics = getNumbersList
								.stream().collect(Collectors
										.summarizingLong(Long::valueOf));
						Long minValue = longSummaryStatistics.getMin();
						System.out.println("Min value:" + minValue);
						Long maxValue = longSummaryStatistics.getMax();
						System.out.println("Max value:" + maxValue);
						String delimiter = invsAlphaPattrenDto
								.getAlphaPattren() == null
										? (String) delimterType
										: invsAlphaPattrenDto.getAlphaPattren();
						if (!delimiter.trim().isEmpty()) {
							minBuilder.append(delimiter);
						}
						if (minValue != 0) {
							minBuilder.append(minValue);
						}
						if (Optional
								.ofNullable(invsAlphaPattrenDto
										.getAlphaPattrenDelimiter())
								.isPresent()) {
							minBuilder.append(invsAlphaPattrenDto
									.getAlphaPattrenDelimiter());
							if (Optional
									.ofNullable(invsAlphaPattrenDto
											.getAlphaPattrenDelimitNumber())
									.isPresent()
									&& !invsAlphaPattrenDto
											.getAlphaPattrenDelimitNumber()
											.isEmpty()) {
								minAndMaxDelimiterList = convertInvoiceNumberFromStringToLong(
										invsAlphaPattrenDto
												.getAlphaPattrenDelimitNumber());
								minBuilder.append(
										invsAlphaPattrenDto.getMinDelimitValue(
												minAndMaxDelimiterList));
							}
						}
						if (!delimiter.trim().isEmpty()) {
							maxBuilder.append(delimiter);
						}
						if (maxValue != 0) {
							maxBuilder.append(maxValue);
						}
						if (Optional
								.ofNullable(invsAlphaPattrenDto
										.getAlphaPattrenDelimiter())
								.isPresent()) {
							maxBuilder.append(invsAlphaPattrenDto
									.getAlphaPattrenDelimiter());
							if (Optional
									.ofNullable(invsAlphaPattrenDto
											.getAlphaPattrenDelimitNumber())
									.isPresent()
									&& !invsAlphaPattrenDto
											.getAlphaPattrenDelimitNumber()
											.isEmpty()) {
								maxBuilder.append(
										invsAlphaPattrenDto.getMaxDelimitValue(
												minAndMaxDelimiterList));
							}
						}
						delimiterList = new ArrayList<>();
						totalDocumentsIssued = getTotalValue(maxValue,
								minValue);
						if (totalDocumentsIssued == 1
								&& Optional.ofNullable(minAndMaxDelimiterList)
										.isPresent()) {
							longSummaryStatistics = minAndMaxDelimiterList
									.stream().collect(Collectors
											.summarizingLong(Long::valueOf));
							minValue = longSummaryStatistics.getMin();
							System.out.println("Min value:" + minValue);
							maxValue = longSummaryStatistics.getMax();
							System.out.println("Max value:" + maxValue);
							totalDocumentsIssued = getTotalValue(maxValue,
									minValue);
							System.out.println("Total issued items count :"
									+ totalDocumentsIssued);
							totalCancelledDocuments = getCancelledInvoicesCount(
									minAndMaxDelimiterList, minValue, maxValue);
							// totalCancelledDocuments = cancelledList.stream()
							// .count();
							System.out.println("Total Cancel items Count::"
									+ totalCancelledDocuments);
							netNumber = (totalDocumentsIssued
									- totalCancelledDocuments);
							System.out.println(
									"Total NetNumber Count::" + netNumber);
						} else {
							System.out.println("Total issued items count :"
									+ totalDocumentsIssued);
							totalCancelledDocuments = getCancelledInvoicesCount(
									getNumbersList, minValue, maxValue);
							// totalCancelledDocuments = cancelledList.stream()
							// .count();
							System.out.println("Total Cancel items Count::"
									+ totalCancelledDocuments);
							netNumber = (totalDocumentsIssued
									- totalCancelledDocuments);
							System.out.println(
									"Total NetNumber Count::" + netNumber);
						}
						prepareFinalinvoiceSeries(gstinId, taxPeriod,
								minBuilder.toString(), maxBuilder.toString(),
								totalDocumentsIssued, totalCancelledDocuments,
								netNumber, invoiceType,
								delimterType.toString());

					}

				}
			}
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
	private void prepareInvoiceSeriesForAlphaPattren(String gstinId,
			String taxPeriod, String invoiceType,
			InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto, List<String> list,
			String pattern) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into prepareInvoiceSeriesForOneElement method , invoice serices list ::"
				+ list + ", and Documnet type:: " + invoiceType);
		try {
			StringBuilder maxBuilder = new StringBuilder();
			StringBuilder minBuilder = new StringBuilder();
			if (Optional.ofNullable(invsAlphaPattrenDto.getAlphaPattren())
					.isPresent()) {
				minBuilder.append(invsAlphaPattrenDto.getAlphaPattren());
			}
			minBuilder.append(list.get(0));
			if (Optional
					.ofNullable(invsAlphaPattrenDto.getAlphaPattrenDelimiter())
					.isPresent()) {
				minBuilder
						.append(invsAlphaPattrenDto.getAlphaPattrenDelimiter());
			}
			LOGGER.debug(
					CLASS_NAME + "::" + "Min value:" + list.stream().count());
			if (Optional.ofNullable(invsAlphaPattrenDto.getAlphaPattren())
					.isPresent()) {
				maxBuilder.append(invsAlphaPattrenDto.getAlphaPattren());
			}
			maxBuilder.append(list.get(0));
			if (Optional
					.ofNullable(invsAlphaPattrenDto.getAlphaPattrenDelimiter())
					.isPresent()) {
				maxBuilder
						.append(invsAlphaPattrenDto.getAlphaPattrenDelimiter());
			}
			LOGGER.debug(
					CLASS_NAME + "::" + "Max value:" + list.stream().count());
			Long totalDocumentsIssued = list.stream().count();
			LOGGER.debug(CLASS_NAME + "::" + "Total issued items count :"
					+ totalDocumentsIssued);
			Long totalCancelledDocuments = 0L;
			LOGGER.debug(CLASS_NAME + "::" + "Total Cancel items Count::"
					+ totalCancelledDocuments);
			Long netNumber = list.stream().count();
			LOGGER.debug(
					CLASS_NAME + "::" + "Total NetNumber Count::" + netNumber);
			delimiterList = new ArrayList<>();
			prepareFinalinvoiceSeries(gstinId, taxPeriod, minBuilder.toString(),
					maxBuilder.toString(), totalDocumentsIssued,
					totalCancelledDocuments, netNumber, invoiceType, pattern);
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesForAlphaPattren for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * prepareInvoiceSeriesForOneElement
	 * 
	 * @param list
	 * @param invoiceType
	 * @param gstinId
	 * @param taxPeriod
	 */
	private void prepareInvoiceSeriesForOneElement(List<String> list,
			String invoiceType, String gstinId, String taxPeriod,
			String pattern) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into prepareInvoiceSeriesForOneElement method , invoice serices list ::"
				+ list + ", and Documnet type:: " + invoiceType);
		try {
			StringBuilder maxBuilder = new StringBuilder();
			StringBuilder minBuilder = new StringBuilder();
			minBuilder.append(list.get(0));
			LOGGER.debug(
					CLASS_NAME + "::" + "Min value:" + list.stream().count());
			maxBuilder.append(list.get(0));
			LOGGER.debug(
					CLASS_NAME + "::" + "Max value:" + list.stream().count());
			Long totalDocumentsIssued = list.stream().count();
			LOGGER.debug(CLASS_NAME + "::" + "Total issued items count :"
					+ totalDocumentsIssued);
			Long totalCancelledDocuments = 0L;
			LOGGER.debug(CLASS_NAME + "::" + "Total Cancel items Count::"
					+ totalCancelledDocuments);
			Long netNumber = list.stream().count();
			LOGGER.debug(
					CLASS_NAME + "::" + "Total NetNumber Count::" + netNumber);
			delimiterList = new ArrayList<>();
			prepareFinalinvoiceSeries(gstinId, taxPeriod, minBuilder.toString(),
					maxBuilder.toString(), totalDocumentsIssued,
					totalCancelledDocuments, netNumber, invoiceType, pattern);
		} catch (Exception e) {
			String errMsg = String.format(
					"Getting error while prepareInvoiceSeriesForOneElement for gstin %s and taxPeriod %s",
					gstinId, taxPeriod);
			throw new AppException(errMsg);
		}
	}

	/**
	 * Total Count
	 *
	 * @param MaxValue
	 * @param MinValue
	 * @return long
	 */
	private long getTotalValue(Long maxValue, Long minValue) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getTotalValue method , MaxValue ::" + maxValue
				+ ", and MinValue:: " + minValue);
		long totalCount = (maxValue - minValue) + 1;
		LOGGER.debug(CLASS_NAME + "::" + "Total Count::" + totalCount);
		return totalCount;
	}

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
			String fromSeries, String toSeries, Long totalDocumentsIssued,
			Long totalCancelledDocuments, Long netNumber, String invoiceType,
			String pattern) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into prepareFinalinvoiceSeries method , invoice serices gstinId ::"
				+ gstinId + ", and Documnet type:: " + invoiceType);
		InvoiceSeriesDTO invoiceSeriesDTO = new InvoiceSeriesDTO();
		try {
			String natureOfDocument = getNatureOfDocument(invoiceType);
			String serialNo = getDocumentSerialNo(invoiceType);
			invoiceSeriesDTO.setGstin(gstinId);
			invoiceSeriesDTO.setTaxPeriod(taxPeriod);
			invoiceSeriesDTO.setInvoiceseriesID(serialNo);
			invoiceSeriesDTO.setNatureOfSupp(natureOfDocument);
			invoiceSeriesDTO.setFromSeries(fromSeries);
			invoiceSeriesDTO.setToSeries(toSeries);
			invoiceSeriesDTO.setTotalNumber(totalDocumentsIssued.toString());
			invoiceSeriesDTO.setCancelled(totalCancelledDocuments.toString());
			invoiceSeriesDTO.setNetNumber(netNumber.toString());
			invoiceSeriesDTO.setPattern(pattern);
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while prepareFinalinvoiceSeries ::"
					+ e);
			throw new AppException(e.getMessage());
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
	private long getCancelledInvoicesCount(List<Long> list, Long minValue,
			Long maxValue) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into getCancelledInvoicesCount method, MinValue == "
				+ minValue + " , and  maxValue==" + maxValue);

		AtomicLong cancelInvoiceCount = new AtomicLong(0);
		try {
			LongStream longStream = LongStream.range(minValue, maxValue);
			longStream.parallel().forEach(inputValue -> {
				if (!list.contains(inputValue)) {
					cancelInvoiceCount.incrementAndGet();
				}
			});
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while getCancelledInvoicesCount ::"
					+ e);
		}
		return cancelInvoiceCount.get();
	}

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
	private List<Long> convertInvoiceNumberFromStringToLong(List<String> list) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into convertInvoiceNumberFromStringToLong method, InvoiceList ::"
				+ list);
		List<Long> response = null;
		try {
			List<Long> longCollection = list.stream()
					.map(element -> element.replaceAll("\\D", ""))
					.map(inputValue -> Long.parseLong(inputValue)).sorted()
					.collect(Collectors.toList());
			response = longCollection.stream().sorted()
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(CLASS_NAME
					+ " :: Getting error while convertInvoiceNumberFromStringToLong ::"
					+ e);
		}
		return response;
	}

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
		String isOnlyNumberRegExp = "(?<=[a-zA-Z\\D]*+)";
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

	/**
	 * Get the DocumentSerialNo
	 *
	 * @param invoiceType
	 * @return String
	 */
	private String getDocumentSerialNo(String invoiceType) {
		String serialNo = null;
		switch (invoiceType.toUpperCase()) {
		case "INV":
			serialNo = "1";
			break;
		case "BOS":
			serialNo = "1";
			break;
		case "SLF":
			serialNo = "2";
			break;
		case "RNV":
			serialNo = "3";
			break;
		case "DR":
			serialNo = "4";
			break;
		case "CR":
			serialNo = "5";
			break;
		case "DLC":
			serialNo = "9";
			break;
		case "DLCOTH":
			serialNo = "12";
			break;
		default:
			serialNo = "0";
			break;
		}
		return serialNo;
	}

	/**
	 * Get the NatureOfDocument
	 *
	 * @param invoiceType
	 * @return String
	 */
	private String getNatureOfDocument(String invoiceType) {
		String natureOfDocument = null;
		switch (invoiceType.toUpperCase()) {
		case "INV":
			natureOfDocument = "Invoices for outward supply";
			break;
		case "BOS":
			natureOfDocument = "Invoices for outward supply";
			break;
		case "CR":
			natureOfDocument = "Credit Note";
			break;
		case "DR":
			natureOfDocument = "Debit Note";
			break;
		case "SLF":
			natureOfDocument = "Invoices for inward supply from unregistered person";
			break;
		case "RNV":
			natureOfDocument = "Revised Invoice";
			break;
		case "DLC":
			natureOfDocument = "Delivery Challan for job work";
			break;
		case "DLCOTH":
			natureOfDocument = "Delivery Challan in cases other than by way of"
					+ " supply (excluding at serial number 9 to 11)";
			break;
		default:
			break;
		}
		return natureOfDocument;
	}

	/**
	 * Get the filterInvoicesByDelimiter
	 *
	 * @param invsAlphaPattrenDto
	 *
	 * @param filteredItem
	 * @return List
	 */
	private List<String> filterInvoicesByDelimiter(List<String> inputlist,
			InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto) {
		LOGGER.debug(CLASS_NAME + "::"
				+ "Entering into filterInvoicesByDelimiter method "
				+ inputlist);
		List<String> list = new ArrayList<>();
		inputlist.stream().forEach(input -> list
				.add(parseInvoicesByDelimiter(input, invsAlphaPattrenDto)));
		return list;
	}

	/**
	 * Get the parseInvoicesByDelimiter
	 *
	 * @param input
	 * @param invsAlphaPattrenDto
	 * @return string
	 */
	private String parseInvoicesByDelimiter(String input,
			InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto) {
		LOGGER.debug(CLASS_NAME
				+ ":: Entering into parseInvoicesByDelimiter method, input is ::"
				+ input);
		String regExp = "(?<=\\D)(?=\\d+\\b)";
		int arraySize = input.split(regExp).length;
		String[] stringArray = input.split(regExp);
		String element = invoicesTokenization(stringArray, arraySize,
				invsAlphaPattrenDto);
		return element;
	}

	/**
	 * Get the invoicesTokenization.
	 *
	 * @param input
	 * @param size
	 * @param invsAlphaPattrenDto
	 * @return string
	 */
	private String invoicesTokenization(String[] input, int size,
			InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto) {
		LOGGER.debug(CLASS_NAME
				+ ":: Entering into invoicesTokenization method, input is ::"
				+ input[0] + ", and array-size is ::" + size);
		String output = null;
		String pattren = null;
		String delimiter = null;
		String regExp = "(?<=[0]*+)";
		String regExp1 = "^\\d$";
		String delimiterNumber = null;
		boolean hasStartsWithNumber = false;
		try {
			switch (size) {
			case 6:
				if (input[size - 1].matches(regExp1)) {
					delimiter = parseDelimiter(input[4]);
					delimiterNumber = input[size - 1];
					pattren = input[0] + input[1] + input[2] + input[3]
							+ (input[4].startsWith("0")
									? input[4].split(regExp)[0] : "");
					char ch = input[4].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[4], pattren, delimiter, delimiterNumber);
					} else {
						output = input[4];
					}
				} else {
					pattren = input[0] + input[1] + input[2] + input[3]
							+ input[4] + (input[5].startsWith("0")
									? input[5].split(regExp)[0] : "");
					char ch = input[5].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[5], pattren, delimiter, delimiterNumber);
					} else {
						output = input[5];
					}
				}
				break;
			case 5:
				if (input[size - 1].matches(regExp1)) {
					delimiter = parseDelimiter(input[3]);
					delimiterNumber = input[size - 1];
					pattren = input[0] + input[1] + input[2]
							+ (input[3].startsWith("0")
									? input[3].split(regExp)[0] : "");
					char ch = input[3].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[3], pattren, delimiter, delimiterNumber);
					} else {
						output = input[3];
					}
				} else {
					pattren = input[0] + input[1] + input[2] + input[3]
							+ (input[4].startsWith("0")
									? input[4].split(regExp)[0] : "");
					char ch = input[4].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[4], pattren, delimiter, delimiterNumber);
					} else {
						output = input[4];
					}
				}
				break;
			case 4:
				if (input[size - 1].matches(regExp1)) {
					delimiter = parseDelimiter(input[2]);
					delimiterNumber = input[size - 1];
					pattren = input[0] + input[1] + (input[2].startsWith("0")
							? input[2].split(regExp)[0] : "");
					char ch = input[2].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[2], pattren, delimiter, delimiterNumber);
					} else {
						output = input[2];
					}
				} else {
					pattren = input[0] + input[1] + input[2]
							+ (input[3].startsWith("0")
									? input[3].split(regExp)[0] : "");
					char ch = input[3].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[3], pattren, delimiter, delimiterNumber);
					} else {
						output = input[3];
					}
				}
				break;
			case 3:
				if (input[size - 1].matches(regExp1)) {
					delimiter = parseDelimiter(input[1]);
					delimiterNumber = input[size - 1];
					pattren = input[0] + (input[1].startsWith("0")
							? input[1].split(regExp)[0] : "");
					char ch = input[1].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[1], pattren, delimiter, delimiterNumber);
					} else {
						output = input[1];
					}
				} else {
					pattren = input[0] + input[1] + (input[2].startsWith("0")
							? input[2].split(regExp)[0] : "");
					char ch = input[2].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[2], pattren, delimiter, delimiterNumber);
					} else {
						output = input[2];
					}
				}
				break;
			case 2:
				if (input[size - 1].matches(regExp1)) {
					delimiter = parseDelimiter(input[0]);
					delimiterNumber = input[size - 1];
					pattren = input[0].startsWith("0")
							? input[0].split(regExp)[0] : "";
					char ch = input[0].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[0], pattren, delimiter, delimiterNumber);
					} else {
						output = input[0];
					}
				} else {
					pattren = input[0] + (input[1].startsWith("0")
							? input[1].split(regExp)[0] : "");
					char ch = input[1].charAt(0);
					if (ch >= '1' && ch <= '9') {
						hasStartsWithNumber = true;
						output = parseNumberFromString(invsAlphaPattrenDto,
								input[1], pattren, delimiter, delimiterNumber);
					} else {
						output = input[1];
					}
				}
				break;
			case 1:
				pattren = input[0].startsWith("0") ? input[0].split(regExp)[0]
						: "";
				char ch = input[0].charAt(0);
				if (ch >= '1' && ch <= '9') {
					hasStartsWithNumber = true;
					output = parseNumberFromString(invsAlphaPattrenDto,
							input[0], pattren, delimiter, delimiterNumber);
				} else {
					output = input[0];
				}
				break;
			default:
				break;
			}
			if (!hasStartsWithNumber) {
				invsAlphaPattrenDto.setAlphaPattren(pattren);
				invsAlphaPattrenDto.setAlphaPattrenDelimiter(delimiter);
				if (Optional.ofNullable(delimiterNumber).isPresent()) {
					delimiterList.add(delimiterNumber);
					invsAlphaPattrenDto
							.setAlphaPattrenDelimitNumber(delimiterList);
				}
			}
		} catch (Exception e) {
			String errMsg = String
					.format("Getting error while invoicesTokenization", e);
			throw new AppException(errMsg);
		}
		return output;
	}

	/**
	 * parseDelimiterFrom given input.
	 *
	 *
	 * @param input
	 * @return String
	 */
	private String parseDelimiter(String input) {
		return input.substring(input.length() - 1);
	}

	/**
	 * parseNumberFromString
	 * 
	 * @param invsAlphaPattrenDto
	 * @param inputElement
	 * @param delimiterNumber
	 * @param delimiterType
	 * @param pattren
	 * @return String
	 */
	private String parseNumberFromString(
			InvoiceSeriesAlphaPattrenDTO invsAlphaPattrenDto,
			String inputElement, String inputPattren, String delimiterType,
			String delimiterNumber) {
		LOGGER.debug(
				CLASS_NAME
						+ ":: Entering into parseNumberFromString method, inputElement is ::"
						+ inputElement + ", and inputPattren::" + inputPattren
						+ " and delimiterType --->{}  and delimiterNumber --->{}",
				delimiterType, delimiterNumber);
		String response = null;
		try {
			if (inputElement.matches("\\d+")) {
				invsAlphaPattrenDto.setAlphaPattren(inputPattren);
				invsAlphaPattrenDto.setAlphaPattrenDelimiter(delimiterType);
				if (Optional.ofNullable(delimiterNumber).isPresent()) {
					delimiterList.add(delimiterNumber);
					invsAlphaPattrenDto
							.setAlphaPattrenDelimitNumber(delimiterList);
				}
				response = inputElement;
			} else {
				if (inputElement.matches("\\d+\\D")) {
					invsAlphaPattrenDto.setAlphaPattren(inputPattren);
					invsAlphaPattrenDto.setAlphaPattrenDelimiter(delimiterType);
					if (Optional.ofNullable(delimiterNumber).isPresent()) {
						delimiterList.add(delimiterNumber);
						invsAlphaPattrenDto
								.setAlphaPattrenDelimitNumber(delimiterList);
					}
					response = inputElement;
				} else {
					if (inputElement.matches("^\\D+$")) {
						invsAlphaPattrenDto.setAlphaPattren(inputPattren);
						invsAlphaPattrenDto
								.setAlphaPattrenDelimiter(delimiterType);
						if (Optional.ofNullable(delimiterNumber).isPresent()) {
							delimiterList.add(delimiterNumber);
							invsAlphaPattrenDto.setAlphaPattrenDelimitNumber(
									delimiterList);
						}
						response = inputElement;
					} else {
						if (inputElement.contains("/")) {
							String delimiter = prepareDelimiterString(
									inputElement, "/");
							if (!delimiter.trim().isEmpty()) {
								if (!Optional
										.ofNullable(invsAlphaPattrenDto
												.getAlphaPattren())
										.isPresent()) {
									invsAlphaPattrenDto
											.setAlphaPattren(inputPattren);
									if (!Optional
											.ofNullable(invsAlphaPattrenDto
													.getAlphaPattrenDelimiter())
											.isPresent()) {
										invsAlphaPattrenDto
												.setAlphaPattrenDelimiter(
														delimiter.trim());
										if (Optional.ofNullable(delimiterNumber)
												.isPresent()) {
											delimiterList.add(delimiterNumber);
											invsAlphaPattrenDto
													.setAlphaPattrenDelimitNumber(
															delimiterList);
										}
									}
								}
							}
						} else if (inputElement.contains("-")) {
							String delimiter = prepareDelimiterString(
									inputElement, "-");
							if (!delimiter.trim().isEmpty()) {
								if (!Optional
										.ofNullable(invsAlphaPattrenDto
												.getAlphaPattren())
										.isPresent()) {
									invsAlphaPattrenDto
											.setAlphaPattren(inputPattren);
									if (!Optional
											.ofNullable(invsAlphaPattrenDto
													.getAlphaPattrenDelimiter())
											.isPresent()) {
										invsAlphaPattrenDto
												.setAlphaPattrenDelimiter(
														delimiter.trim());
										if (Optional.ofNullable(delimiterNumber)
												.isPresent()) {
											delimiterList.add(delimiterNumber);
											invsAlphaPattrenDto
													.setAlphaPattrenDelimitNumber(
															delimiterList);
										}
									}
								}
							}
						}
					}
					response = inputElement;
				}
			}
		} catch (Exception e) {
			String errMsg = String
					.format("Getting error while parseNumberFromString", e);
			throw new AppException(errMsg);

		}
		return response;
	}

	/**
	 * @param inputArray
	 * @param type
	 * @return string
	 */
	private String prepareDelimiterString(String inputElement, String regExp) {
		LOGGER.debug(CLASS_NAME
				+ ":: Entering into prepareDelimiterString method, regExp is ::"
				+ regExp + " and inputElement is ::" + inputElement);
		String delimiter = "";
		try {
			String[] inputArray = inputElement.split(regExp);
			for (String inputIndex : inputArray) {
				Pattern pattern = Pattern.compile("[0-9]");
				Matcher matcher = pattern.matcher(inputIndex);
				if (!matcher.find()) {
					delimiter += regExp + inputIndex;
				}
			}
		} catch (Exception e) {
			String errMsg = String
					.format("Getting error while prepareDelimiterString", e);
			throw new AppException(errMsg);

		}
		return delimiter;
	}

}
