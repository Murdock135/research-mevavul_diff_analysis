class checkSqlAnnotation {
public static void checkSqlAnnotation(String str){
		Matcher matcher = SQL_ANNOTATION.matcher(str);
		if(matcher.find()){
			String error = "请注意，值可能存在SQL注入风险---> \\*.*\\";
			log.error(error);
			throw new RuntimeException(error);
		}
		
		// issues/4737 sys/duplicate/check SQL注入 #4737
		Matcher sleepMatcher = FUN_SLEEP.matcher(str);
		if(sleepMatcher.find()){
			String error = "请注意，值可能存在SQL注入风险---> sleep";
			log.error(error);
			throw new RuntimeException(error);
		}
	}
}
