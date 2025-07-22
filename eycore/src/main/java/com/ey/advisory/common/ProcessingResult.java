package com.ey.advisory.common;

import lombok.Getter;
import lombok.ToString;

/**
 * This is a common class to store any processing the results of any processing.
 * Usually, after processing, we get either an error Code,
 * 
 * @author Sai.Pakanati
 *
 */
@Getter
@ToString
public class ProcessingResult {

	/**
	 * A source that identifies processing. Sometimes we need to record the
	 * source of validation as part of the result. For example, some of the
	 * validations may be done at the local application, but other validations
	 * can be done at GSTN. So, the source of result can be either 'LOCAL' or
	 * 'GSTN'.
	 */
	private String source;

	/**
	 * Result can be of type Error, Info, Warning etc.
	 */
	private ProcessingResultType type;
	private String errorField;

	/**
	 * The unique code given by the application/business to identify the result.
	 */
	private String code;

	/**
	 * The description associated with the code.
	 */
	private String description;

	/**
	 * The des
	 */
	private String descriptonForInfo;

	/**
	 * A unique path/row number where the error has occurred. This will be
	 * useful to identify the exact location of the associated error, within a
	 * composite object. For example, while validating an invoice, the error can
	 * be at a specific line item at a specific field. The processing logic can
	 * decide how to report this error. In this scenario, we can decide to have
	 * the line number of the invoice as the location.The client code should be
	 * aware of the object/format that the processing logic returns.
	 */
	private Object location;

	/**
	 * In some error scenarios, some additional data can be attached to the
	 * processing result, so that the caller can use it for meaningful purposes.
	 * For example, in case of duplicate documents check, we can attach the
	 * document id/doc key of the existing document so that the client can use
	 * it for further processing. (Use this meaningfully!!)
	 */
	private Object extraData;

	/**
	 * In several scenarios, we do not have to return a location of error.
	 * Hence, this constructor creates a ProcessingResult with the location as
	 * null and ResultType as ERROR.
	 * 
	 * @param source
	 * @param code
	 * @param description
	 */
	public ProcessingResult(String source, String code, String description) {
		super();
		this.source = source;
		this.type = ProcessingResultType.ERROR;
		this.code = code;
		this.description = description;
	}

	public ProcessingResult(String source, String code, String description, ProcessingResultType processingResultType) {
		super();
		this.source = source;
		this.type = processingResultType;
		this.code = code;
		if (processingResultType.equals(ProcessingResultType.ERROR)) {
			this.description = description;
		} else {
			this.descriptonForInfo = description;
		}

	}

	/**
	 * Utility constructor to create a ProcessingResult with the type as ERROR.
	 * 
	 * @param source
	 * @param code
	 * @param description
	 * @param location
	 */
	public ProcessingResult(String source, String code, String description, Object location) {
		super();
		this.source = source;
		this.type = ProcessingResultType.ERROR;
		this.code = code;
		this.description = description;
		this.location = location;
	}

	public ProcessingResult(String source, ProcessingResultType type, String code, String description,
			Object location) {
		super();
		this.source = source;
		this.type = type;
		this.code = code;
		this.description = description;
		this.location = location;
	}

	public ProcessingResult(String source, ProcessingResultType type, String code, String description, Object location,
			Object extraData) {
		super();
		this.source = source;
		this.type = type;
		this.code = code;
		this.description = description;
		this.location = location;
		this.extraData = extraData;
	}

	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

}
