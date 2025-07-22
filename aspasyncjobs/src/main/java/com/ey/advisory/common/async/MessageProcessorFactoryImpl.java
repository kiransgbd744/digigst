package com.ey.advisory.common.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * This class extracts the value of the key named 'type' in the message JSON.
 * Depending on the 'type' value, the Spring Bean to be used as the 
 * TaskProcessor instance is returned. These values are hard coded in this
 * class for now, but later can be moved out to DB and loaded using the
 * ConfigManager class.
 * 
 * If 'type' key doesn't exist OR if the 'type' value represents an 
 * unknown message type, then a null value is returned as the TaskProcessor.
 * The caller of the getMessageTaskProcessor() needs to handle this null
 * condition and take necessary actions. 
 * 
 * @author Sai.Pakanati
 *
 */

@Component("MessageProcessorFactoryImpl")
public class MessageProcessorFactoryImpl implements MessageProcessorFactory {
	private static final Logger LOGGER =
			LoggerFactory.getLogger(MessageProcessorFactoryImpl.class);

	@Override
	public TaskProcessor getMessageTaskProcessor(Message message) {
		String taskType = message.getMessageType();
		TaskProcessor taskProcessor = null;
		try {
			switch (taskType) {
			case JobStatusConstants.DB_CHUNK_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"PRDBChunkProcessor",TaskProcessor.class);
				break;
			case JobStatusConstants.INWARD_SFTP_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"PRFileArrivalProcessor", TaskProcessor.class);
				break;
			case JobStatusConstants.INWARD_CHUNK_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"PRFileChunkProcessor", TaskProcessor.class);
				break;
			case JobStatusConstants.OUTWARD_SFTP_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"SRFileArrivalProcessor", TaskProcessor.class);
				break;
			case JobStatusConstants.OUTWARD_CHUNK_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"SRFileChunkProcessor", TaskProcessor.class);
				break;
			case JobStatusConstants.STAGING_TYPE:
				taskProcessor =
				(TaskProcessor) StaticContextHolder.getBean(
						"PRStagingLoadedProcessor", TaskProcessor.class);
				break;
			default:LOGGER.info(
					"Task is not available for the type specified for Job Id :" 
						+ message.getId());
			break;
			}
		} catch(Exception ex) {
			LOGGER.error("Exception while determining the task ",ex);
		}
		return taskProcessor;
	}

}
