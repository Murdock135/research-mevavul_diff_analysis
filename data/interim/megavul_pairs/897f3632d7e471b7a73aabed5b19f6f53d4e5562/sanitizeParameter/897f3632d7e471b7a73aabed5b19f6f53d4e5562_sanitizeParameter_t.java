class sanitizeParameter {
public static String sanitizeParameter(String parameter){


		if(!UtilMethods.isSet(parameter)){//check if is not null
			return "";
		}

		for(String str : EVIL_SQL_WORDS){
			if(parameter.toLowerCase().contains(str)){//check if the order by requested have any other command
				Exception e = new DotStateException("Invalid or pernicious sql parameter passed in : " + parameter);
				Logger.error(SQLUtil.class, "Invalid or pernicious sql parameter passed in : " + parameter, e);
				SecurityLogger.logInfo(SQLUtil.class, "Invalid or pernicious sql parameter passed in : " + parameter);
				return "";
			}
		}

		return parameter;
	}
}
