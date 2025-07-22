package com.ey.advisory.app.services.annexure1fileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredHsnEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAnn1VerticalWebErrorRepo;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("Gstr1aVerticalWebUploadErrorService")
public class Gstr1aVerticalWebUploadErrorService {

	@Autowired
	@Qualifier("Gstr1AAnn1VerticalWebErrorRepo")
	private Gstr1AAnn1VerticalWebErrorRepo gstr1AAnn1VerticalWebErrorRepo;

	public Map<String, List<Gstr1AAnn1VerticalWebError>> convertErrors(
			Map<String, List<ProcessingResult>> results, String valueType,
			Gstr1FileStatusEntity updateFileStatus) {

		Map<String, List<Gstr1AAnn1VerticalWebError>> map = new ConcurrentHashMap<>();
		results.keySet().parallelStream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			List<Gstr1AAnn1VerticalWebError> errors = new CopyOnWriteArrayList<>();
			pResults.parallelStream().forEach(pr -> {
				// Instantiate the ent
				Gstr1AAnn1VerticalWebError error = new Gstr1AAnn1VerticalWebError();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (updateFileStatus != null) {

					if (null != loc) { // In case of bifurcation failure, loc is
										// null
						Object[] arr = loc.getFieldIdentifiers();
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = StringUtils.join(fields, ',');
						error.setErrorField(errField);
					}
					String fileType = null;
					if (updateFileStatus.getFileType() != null
							&& !updateFileStatus.getFileType().trim()
									.isEmpty()) {
						fileType = updateFileStatus.getFileType();

					}
					String userName = null;
					if (updateFileStatus.getUpdatedBy() != null
							&& !updateFileStatus.getUpdatedBy().trim()
									.isEmpty()) {
						userName = updateFileStatus.getUpdatedBy();

					}
					String source = null;
					if (updateFileStatus.getSource() != null
							&& !updateFileStatus.getSource().trim().isEmpty()) {
						source = updateFileStatus.getSource();

					}
					long fileId = 0;
					if (updateFileStatus.getId() != 0) {
						fileId = updateFileStatus.getId();
					}

					error.setErrorSource(source);
					error.setTableType(fileType);
					error.setValueType(valueType);
					error.setErrorCode(pr.getCode());
					error.setErrorDesc(pr.getDescription());
					error.setCreatedBy(userName);
					error.setErrorType(pr.getType().toString());
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					error.setCreatedDate(convertNow);
					error.setFileId(fileId);

					errors.add(error);
				}
			});
			map.put(key, errors);
		});
		return map;
	}

	public void storedErrorRecords(List<OutwardB2cExcelEntity> saveStrucAll,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {

		saveStrucAll.parallelStream().forEach(errorRecords -> {
			String b2cKey = errorRecords.getB2cInvKey();
			Long commanId = errorRecords.getId();
			String key = b2cKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getB2cInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new CopyOnWriteArrayList<>();
		errorMap.entrySet().parallelStream().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.parallelStream().forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedTable4ErrorRecords(
			List<OutwardTable4ExcelEntity> saveStrucAll,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {

		saveStrucAll.forEach(errorRecords -> {
			String table4Key = errorRecords.getTable4Invkey();
			Long commanId = errorRecords.getId();
			String key = table4Key.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getTable4Invkey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}

	}

	/**
	 * @param saveAll
	 * @param errorMap
	 */
	public void storedTable3HErrorRecords(
			List<InwardTable3I3HExcelEntity> saveAll,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {

		saveAll.forEach(errorRecords -> {
			String table3h3i = errorRecords.getTable3h3iInvKey();
			Long commanId = errorRecords.getId();
			String key = table3h3i.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getTable3h3iInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorGstr1B2csRecords(
			List<Gstr1AAsEnteredB2csEntity> structuralErrorsRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		structuralErrorsRecords.forEach(errorRecords -> {
			String b2csInvKey = errorRecords.getInvB2csKey();
			Long commanId = errorRecords.getId();
			String key = b2csInvKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInvB2csKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorGstr1InvRecords(
			List<Gstr1AAsEnteredInvEntity> strErrRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strErrRecords.parallelStream().forEach(errorRecords -> {
			String invoiceKey = errorRecords.getInvoiceKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInvoiceKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new CopyOnWriteArrayList<>();
		;
		errorMap.entrySet().parallelStream().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}

	}

	public void storedErrorGstr1AtRecords(
			List<Gstr1AAsEnteredAREntity> structuralErrorsRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		structuralErrorsRecords.forEach(errorRecords -> {
			String atInvKey = errorRecords.getInvAtKey();
			Long commanId = errorRecords.getId();
			String key = atInvKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInvAtKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});
		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}

	}

	public void storedErrorGstr1TxpdRecords(
			List<Gstr1AAsEnteredTxpdFileUploadEntity> strErrRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strErrRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getTxpdInvKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getTxpdInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}

	}

	public void storedTableRet1And1ARecords(
			List<Ret1And1AExcelEntity> strucErrorRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getRet1And1AInvkey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getRet1And1AInvkey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedREFUNDErrorRecords(
			List<RefundsExcelEntity> strucErrorRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getRefundInvkey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getRefundInvkey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedINTERESTErrorRecords(
			List<InterestExcelEntity> strucErrorRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getInterestInvKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInterestInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedSetOffErrorRecords(
			List<SetOffAndUtilExcelEntity> strucErrorRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getSetOffInvKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getSetOffInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorGstr1NilRecords(
			List<Gstr1ANilNonExemptedAsEnteredEntity> strucErrorRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getNKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getNKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorGstr1HsnRecords(
			List<Gstr1AAsEnteredHsnEntity> strErrRecords,
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap) {
		strErrRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getInvHsnKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr1AAnn1VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInvHsnKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr1AAnn1VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr1AAnn1VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr1AAnn1VerticalWebErrorRepo.saveAll(outError);
		}
	}
}
