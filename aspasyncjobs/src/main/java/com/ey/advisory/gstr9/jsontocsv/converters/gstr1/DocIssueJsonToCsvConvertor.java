/**
 * 
 */
package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.DocIssueDetails;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.DocIssueHeader;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("DocIssueJsonToCsvConvertor")
public class DocIssueJsonToCsvConvertor implements JsonToCsvConverter {

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;
	
	private static final String HEADER = "DocumentType,From serial number,"
			+ "To serial number,Total Number,Cancelled,Net issued\r\n";
	
	static Map<Long, String> hsnMap = new HashMap<Long, String>();

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriter) throws IOException {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}
		
		int colCount = HEADER.split(",").length;
		
		List<NatureOfDocEntity> hsnOrSacMasterEntities = natureDocTypeRepo
				.findAll();
		
		hsnOrSacMasterEntities.forEach(entity -> {
			hsnMap.put(entity.getId(), entity.getNatureDocType());
		});
		
		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			
			if (rootName.equals(JobStatusConstants.DOC_ISSUE_TYPE)) {
				reader.beginObject();
				while (reader.hasNext()) {
					String flag = "";
					String chksum = "";
					//reader.beginObject();
					
					List<String[]> list = new ArrayList<>();
					while (reader.hasNext()) {
						String name = reader.nextName();
						if (name.equals("flag")) {
							flag = reader.nextString();
						} else if (name.equals("chksum")) {
							chksum = reader.nextString();
						}else if (name.equals("doc_det")) {
							
							
							reader.beginArray();
							while (reader.hasNext()) {
								DocIssueHeader invoice = JsonUtil
										.newGsonInstance(false)
										.fromJson(reader, DocIssueHeader.class);
								
								List<String[]> arr = getLineItemList(invoice,
										colCount);
								list.addAll(arr);
						}
							
							reader.endArray();
						} else {
							reader.skipValue(); // avoid some unhandle
												// events
						}
					}
					// At this point, populate the elements in the array
					// with the ctin and cfs. Once this is done, we can
					// flush the array to the csvwriter and the mergedFile
					// writer.
					//populateCtinAndCfs(ctin, cfs, list);
					writeToCSV(list, colCount, csvWriter[0]);

					reader.endObject();
				}

				//reader.endArray();
			} else {
				reader.skipValue(); // avoid some unhandle events
			}
		}
		reader.endObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}
		
	}
	
	
	private List<String[]> getLineItemList(DocIssueHeader invoice,
			int colCount) {
		
		List<String[]> list = new ArrayList<>();
		List<DocIssueDetails> docDetailsList = invoice.getDocIssueDetails();
		
		
		
		for (DocIssueDetails docDetail : docDetailsList) {
			int i = 0;
			String[] arr = new String[colCount];
			
			arr[i++] = GenUtil.toCsvString(invoice.getDocnum() != null ? 
					(invoice.getDocnum() + "-" + hsnMap.get(Long.valueOf(invoice.getDocnum()))) : "" );
			arr[i++] = GenUtil.toCsvString(docDetail.getFrom());
			arr[i++] = GenUtil.toCsvString(docDetail.getTo());
			arr[i++] = GenUtil.toCsvString(docDetail.getTotnum());
			arr[i++] = GenUtil.toCsvString(docDetail.getCancel());
			arr[i++] = GenUtil.toCsvString(docDetail.getNet_issue());
					
			list.add(arr);	
		}
		
		
		return list;
	}
	
	private static void writeToCSV(List<String[]> list, int colCount,
			BufferedWriter bw) throws IOException {
		for (String[] arr : list) {
			StringJoiner joiner = new StringJoiner(",");
			for (int i = 0; i < colCount; i++) {
				GenUtil.appendStringToJoiner(joiner, arr[i]);
			}
			bw.write(joiner.toString());
			bw.write("\r\n");
		}
	}


	@Override
	public String[] getCsvHeaderStrings() {
		
		return new String[] { HEADER };
	}

}
