class checkSqlAnnotation {
public static void checkSqlAnnotation(String str){
		Matcher matcher = SQL_ANNOTATION.matcher(str);
		if(matcher.find()){
			String error = "请注意，值可能存在SQL注入风险---> \\*.*\\";
			log.error(error);
			throw new RuntimeException(error);
		}
	}
}
