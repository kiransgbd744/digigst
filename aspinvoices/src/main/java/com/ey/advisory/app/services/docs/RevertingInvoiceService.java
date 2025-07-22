/**
 * 
 */
package com.ey.advisory.app.services.docs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.RevertingInvReqDto;
import com.ey.advisory.app.docs.dto.RevertingInvResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("RevertingInvoiceService")
@Slf4j
public class RevertingInvoiceService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository InwarRepository;

	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";

	public RevertingInvResponseDto revertingInv(RevertingInvReqDto request) {

		Long oldId = request.getId();
		String dataType = request.getDataType();
		Long newId = null;
		RevertingInvResponseDto resp = new RevertingInvResponseDto();
		if (dataType != null && !dataType.isEmpty()) {
			if (dataType.equalsIgnoreCase(OUTWARD)) {
				String docKey = docRepository.findDocKeyById(oldId);
				if (docKey != null) {
					List<Object> newIds = docRepository.findIdByDocKey(docKey);

					if (newIds != null && !newIds.isEmpty()) {
						newId = (Long) newIds.get(0);

						docRepository.updateOldProcessedInv(newId);
						docRepository.updateNewErrorInv(oldId);
					}
				}
			}
			if (dataType.equalsIgnoreCase(INWARD)) {
				String docKey = InwarRepository.findDocKeyById(oldId);
				if (docKey != null) {
					List<Object> newIds = InwarRepository
							.findIdByDocKey(docKey);

					if (newIds != null && !newIds.isEmpty()) {
						newId = (Long) newIds.get(0);

						InwarRepository.updateOldProcessedInv(newId);
						InwarRepository.updateNewErrorInv(oldId);
					}
				}
			}
			resp.setNewId(newId);
			resp.setOldId(oldId);
		}
		return resp;
	}
}
