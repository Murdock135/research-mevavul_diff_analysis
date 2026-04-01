class sanitizeSortBy {
public static String sanitizeSortBy(String parameter){



		
		if(!UtilMethods.isSet(parameter)){//check if is not null
			return "";
		}


		String testParam=parameter.replaceAll(" asc", "").replaceAll(" desc", "").replaceAll("-", "").toLowerCase();
		if(ORDERBY_WHITELIST.contains(testParam)){
			return parameter;
		}

		Exception e = new DotStateException("Invalid or pernicious sql parameter passed in : " + parameter);
		Logger.error(SQLUtil.class, "Invalid or pernicious sql parameter passed in : " + parameter, e);

		SecurityLogger.logDebug(SQLUtil.class, "Invalid or pernicious sql parameter passed in : " + parameter);
		return "";
	}
}
