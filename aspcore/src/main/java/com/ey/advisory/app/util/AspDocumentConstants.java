package com.ey.advisory.app.util;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class AspDocumentConstants {
	
	public static final String MONTH_YEAR_DATE_FORMATTER = "MMyyyy";
	
	
	public enum FormReturnTypes{
		GSTR1("GSTR1"),ANX1("ANX1"),GSTR2("GSTR2"),ANX2("ANX2");
		private String type;
		
		FormReturnTypes(String formType){
			this.type = formType;
		}
		public String getType() {
			return type;
		}
	}
	
	public enum TransDocTypes {
		INWARD("Inward"), OUTWARD("Outward");
		private String type;

		TransDocTypes(String transDocType) {
			this.type = transDocType;
		}

		public String getType() {
			return type;
		}

		/*private void setType(String type) {
			this.type = type;
		}*/
	}

}
