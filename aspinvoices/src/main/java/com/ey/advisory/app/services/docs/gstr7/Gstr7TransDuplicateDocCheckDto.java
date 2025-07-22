/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;

import lombok.Data;

/**
 * This class represents transferring the duplicate Doc Ids after checking for
 * them
 * 
 * @author Laxmi.Salukuti
 *
 */
@Data
public class Gstr7TransDuplicateDocCheckDto {

	private List<Gstr7TransDocHeaderEntity> docs;

}
