/**
 * 
 */
package com.ey.advisory.controllers.upload.way3recon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.TestEwbUploadEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TestEwbUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class TestUploadController {

	private static String[] headerArray = { "EWB No", "EWB Date", "Supply Type" };

	private static List<String> HEADER_LIST = Arrays.asList(headerArray);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("TestEwbUploadRepository")
	private TestEwbUploadRepository repo;

	@PostMapping(value = "/ui/ewbTest", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewbUpload(@RequestBody String jsonString) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		InputStream targetStream = null;
		try {

			File folder = new File("C:\\Users\\QD194RK\\Desktop\\9A631000.xlsx");
			 targetStream = new FileInputStream(folder);
			String fileName = folder.getName();
			
			method(targetStream,  fileName);

			APIRespDto dto = new APIRespDto("Sucess",
					"File has been successfully uploaded");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Begining from uploadDocuments:{} ");
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}finally {
			// sonar obs
			if (targetStream != null) {
				try {
					targetStream.close();
				} catch (IOException e) {
				LOGGER.debug("An error occurred while closing targetStream", e);

				}
			}
		}

	}

	private void method(InputStream fin, String fileName) {

		try {

			TabularDataSourceTraverser traverser = traverserFactory.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(3);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler).getFileUploadList();

			if (fileList.isEmpty() || fileList == null) {

				String msg = "Failed Empty file..";
				LOGGER.error(msg);

				throw new AppException(msg);

			}

			// saving to table
			List<TestEwbUploadEntity> dumpReconds = fileList.stream().map(o -> convertEntity(o))
					.collect(Collectors.toList());
			repo.saveAll(dumpReconds);// saveAll(dumpReconds);

		} catch (Exception ex) {
//			ex.printStackTrace();
		}
	}

	private static ThreadLocal<NumberFormat> numberFormatter = ThreadLocal
			.withInitial(() -> new DecimalFormat("0"));
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");

	private TestEwbUploadEntity convertEntity(Object[] o) {
		TestEwbUploadEntity dto = new TestEwbUploadEntity();

		/*String[] splitedId = null;
		if(o[0] != null) {
			String id = o[0].toString().trim().replace(".", "");
			splitedId = id.split("E");
		}*/
		String trimVal = null;
		if (o[0] instanceof Number) {
			trimVal = numberFormatter.get().format(o[0]);
		} else
			trimVal = o[0].toString().trim();

		dto.setEwbNo(Long.valueOf(trimVal));
		dto.setSupplyType(o[2].toString());
		String ldt = o[1].toString();
		LocalDateTime dateTime = LocalDateTime.parse(ldt, formatter);
		dto.setEwbDate(dateTime);
		return dto;
	}
	
	public static void main(String[] args) {
		Object obj = "1.01413639633E11";
		
		System.out.println(numberFormatter.get().format(obj));
	}

}
