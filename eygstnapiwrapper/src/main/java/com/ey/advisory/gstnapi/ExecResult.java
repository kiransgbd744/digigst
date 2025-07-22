package com.ey.advisory.gstnapi;

import java.util.List;

/**
 * This class represents the complete result of execution of a code block. 
 * Any code block execution can result in a success or a failure. If the 
 * execution is a success, we will have a result. (For code blocks that only 
 * perform side effects, we can use a boolean, as void cannot be a template 
 * parameter in java). In addition, we might have a success message in case of
 * success (in rare scenarios). In the case of a failure, we will have the
 * error codes (execution error code and/or external error code; see the data
 * member java doc comments for more details), error message and/or an exception
 * that resulted in the failure condition.
 *  
 * @author Khalid1.Khan
 * 
 */
public final class ExecResult<T> {
	
	/**
	 * The result will be set when the execution is successful. On failure,
	 * the result will be null.
	 */
	private T result;
	/**
	 * Success Message will be optionally set when the execution is successful.
	 * 
	 */
	private String successMsg;

	/**
	 * The execution error code is an error code used to identify a specific 
	 * error occurring at the time of execution. Unlike the external error 
	 * code, this value helps us understand the location and type of error that
	 * happened during an execution flow. This value can be used by a flow 
	 * control framework to determine what kind of error has occurred, in order
	 * to take appropriate actions.
	 */
	private String execErrCode;
	
	/**
	 * External error code is usually a domain specific error code that is 
	 * useful from a functionality point of view. These error codes are closer
	 * to the end user of an application.
	 * 
	 */
	private String extErrCode;

	/**
	 * The error message associated with the exceptional situation.
	 */
	private String errMsg;
	
	
	/**
	 * The actual java exception, if any, that resulted in an error scenario.
	 */
	private Exception ex;

	/**
	 * Depending on the factory method called to create an instance of this 
	 * class, this boolean value will be set.
	 */
	private boolean isSuccess;
	
	
	private List<String> failedFileNums;

	
	/**
	 * Make the constructor private in order to make this class as a value
	 * based class.
	 */
	private ExecResult() {}
	
	public static <T> ExecResult<T> successResult(T value) {		
		return successResult(value, null);
	}

	public static <T> ExecResult<T> successResult(T value, String msg) {
		ExecResult<T> res = new ExecResult<>();
		res.result = value;
		res.successMsg = msg;
		res.isSuccess =  true;
		return res;
	}

	public static <T> ExecResult<T> errorResult(
			String execErrCode, String errMsg) {
		return errorResult(execErrCode, null, errMsg, null);
	}
	
	public static <T> ExecResult<T> errorResult(
			String execErrCode, String errMsg, Exception ex) {
		return errorResult(execErrCode, null, errMsg, ex);
	}
	
	public static <T> ExecResult<T> errorResult(
			String execErrCode, String extErrCode, String errMsg) {
		return errorResult(execErrCode, extErrCode, errMsg, null);
	}
	
	public static <T> ExecResult<T> errorResult(String execErrCode, 
			String extErrCode, String errMsg, Exception ex) {
		ExecResult<T> res = new ExecResult<>();
		res.execErrCode = execErrCode;
		res.extErrCode = extErrCode;
		res.errMsg = errMsg;
		res.ex = ex;
		res.isSuccess =  false;
		return res;		
	}
		
	// The core methods used to determine whether the execution 
	// was a success or a failure.
	public boolean isError() { return !isSuccess; }
	public boolean isSuccess() { return isSuccess; }
	
	// The getter methods to get the result.
	public T getResult() { return result; }	
	public Exception getException() { return ex; }
	public String getErrMsg() { return errMsg; }
	public String getExecErrCode() { return execErrCode; }
	public String getExtErrCode() { return extErrCode; }
	
	/**
	 * This method converts the current execution result to a result which
	 * mentions whether the output was success OR not. This can be used in 
	 * situations where we don't care about the return value.
	 * 
	 * @return
	 */
	public ExecResult<Boolean> toBoolResult() {
		if (isSuccess) {
			return successResult(Boolean.TRUE, successMsg);
		} else {
			return errorResult(execErrCode, extErrCode, errMsg, ex);
		}
	}
	
	
	public List<String> getFailedFileNums() {
		return failedFileNums;
	}

	public void setFailedFileNums(List<String> failedFileNums) {
		this.failedFileNums = failedFileNums;
	}

	/**
	 * Return the string based on whether the execution is a scuccess 
	 * or failure.
	 */
	public String toString() {
		if (isSuccess) {
			String successStr = (successMsg != null) ?  
					String.format(", Msg = %s", successMsg) : "";
			return String.format(
					"[Success] - Result = %s; %s", result, successStr);
		} 
		return String.format(
				"[Failure] - [ExecErrCode: %s, ExtErrCode: %s]; ErrMsg: %s", 
				execErrCode, extErrCode, errMsg);
	}
	
}
 