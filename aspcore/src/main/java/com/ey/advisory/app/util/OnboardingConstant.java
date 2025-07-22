package com.ey.advisory.app.util;

public class OnboardingConstant {

	private OnboardingConstant(){
		
	}
	public static final String GSTIN = "GSTIN";
	public static final String PC = "PC";
	public static final String PC2 = "PC2";
	public static final String PLANT = "Plant";
	public static final String DIVISION = "D";
	public static final String SUB_DIVISION = "SD";
	public static final String SO = "SO";
	public static final String PO = "PO";
	public static final String DC = "DC";
	public static final String LOCATION = "L";
	public static final String UD1 = "UD1";
	public static final String UD2 = "UD2";
	public static final String UD3 = "UD3";
	public static final String UD4 = "UD4";
	public static final String UD5 = "UD5";
	public static final String UD6 = "UD6";
	public static final String SI = "SI";
	
	public static final String AT_O = "O";
	public static final String AT_M = "M";
	public static final String AT_N = "N";
	
	
	public static final String REGULAR_REG_TYPE = "REGULAR";
	/*
	 * Vendor files upload
	 */
	public static final String VENDOR_FOLDER_NAME = "vendorWebUpload";
	public static final String VENDOR_RAW_FILE_NAME = "VENDOR_RAW_FILE_NAME";
	public static final String VENDOR = "VENDOR";
	public static final String MASTER = "MASTER";
	
	//On-Boarding Config Parameters
	public enum CONFIG_PARAM_CATEGORY{
		GENERAL("GENERAL"),OUTWARD("OUTWARD"),INWARD("INWARD");
		
		String paramCategory;
		
		CONFIG_PARAM_CATEGORY(String paramCategory){
			this.paramCategory = paramCategory;
		}

		public String getParamCategory() {
			return paramCategory;
		}
	}
	
	//On-Boarding Config Parameters - OUTWARD Question Ids
	public enum CONFIG_PARAM_OUTWARD_QUE_KEY_ID{
		O1,O2,O3,O4,O5,O6,O7,O8,O9,O10,O11,O12,O15,O19,O34,O36,O43
	}
	
	//On-Boarding Config Parameters - INWARD Question Ids
	public enum CONFIG_PARAM_INWARD_QUE_KEY_ID {
		I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12,I15,I44,I46
	}
	
	// On-Boarding Config Parameters - General Question Ids
	public enum CONFIG_PARAM_GENERAL_QUE_KEY_ID {
		G1,G2,G3,G4,G5,G6,G7,G8,G9,G10,G11,G12,G13
	}
	
	//On-Boarding Config Parameters - OUTWARD Answer Ids
	public enum CONFIG_PARAM_OUTWARD_ANS_KEY_ID {
		A, B, C, D, E, F, G, H
	}

	//On-Boarding Config Parameters - INWARD Answer Ids
	public enum CONFIG_PARAM_INWARD_ANS_KEY_ID {
		A, B, C, D, E, F, G, H
	}

}
