/**
 * 
 */
package com.ey.advisory.functions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */

@Component("EinvJsonSchemaValidatorHelper")
@Slf4j
public class EinvJsonSchemaValidatorHelper {

	public List<ErrorDetailsDto> validateInptJson(String jsonData,
			String jsonSchema) {
		JsonNode jsonSchemaNode;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream(jsonSchema);) {

			jsonSchemaNode = mapper.readTree(stream);

			final JsonNode data = mapper.readTree(jsonData);

			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

			final JsonSchema schema = factory.getJsonSchema(jsonSchemaNode);

			ProcessingReport report;

			report = schema.validate(data);

			if (!report.isSuccess()) {

				ProcessingMessage message;
				Iterator<ProcessingMessage> itr = report.iterator();

				while (itr.hasNext()) {
					ErrorDetailsDto errorDto = new ErrorDetailsDto();
					message = itr.next();
					String msg = message.asJson().get("message").asText();

					JsonNode obj = message.asJson().get("schema");
					String field = obj.get("pointer").asText()
							.replace("/properties", "");

					errorDto.setErrorField(field);
					errorDto.setErrorDesc(msg);
					errorList.add(errorDto);

				}
			}
		} catch (IOException | ProcessingException e) {
			String msg = "Exception occured while validating json schema";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
		return errorList;

	}
}