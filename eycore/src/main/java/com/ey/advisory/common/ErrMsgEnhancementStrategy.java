package com.ey.advisory.common;

/**
 * When we log error messages to a database against an activity, it can be
 * beneficial to the developers to know the reason for the exception.
 *
 */
public enum ErrMsgEnhancementStrategy {
	NO_CHANGE,
	APPEND_LAST_EXCEPTION,
	APPEND_LAST_APPEXCEPTION,
	APPEND_FIRST_NON_APP_EXCEPTION
}