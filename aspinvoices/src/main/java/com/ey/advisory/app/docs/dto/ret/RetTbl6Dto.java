/**
 * 
 */
package com.ey.advisory.app.docs.dto.ret;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class RetTbl6Dto {

	@Expose
	@SerializedName("intrlatefiling")
	private RetItemDetailsDto intrlatefiling;

	@Expose
	@SerializedName("intrlatereport")
	private RetItemDetailsDto intrlatereport;

	@Expose
	@SerializedName("intrreja")
	private RetItemDetailsDto intrreja;

	@Expose
	@SerializedName("intritcrev")
	private RetItemDetailsDto intritcrev;

	@Expose
	@SerializedName("intrrev")
	private RetItemDetailsDto intrrev;

	@Expose
	@SerializedName("othrliab")
	private RetItemDetailsDto othrliab;

	///// GET/////

	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;

	@Expose
	@SerializedName("chksum")
	private String chksum;
}
