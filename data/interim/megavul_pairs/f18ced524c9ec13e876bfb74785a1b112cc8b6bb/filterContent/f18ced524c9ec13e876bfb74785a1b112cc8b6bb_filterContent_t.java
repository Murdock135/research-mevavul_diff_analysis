class filterContent {
public static void filterContent(String value, String customXssString) {
		if (value == null || "".equals(value)) {
			return;
		}
		// 校验sql注释 不允许有sql注释
		checkSqlAnnotation(value);
		// 统一转为小写
		value = value.toLowerCase();
		//SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
		//value = value.replaceAll("/\\*.*\\*/","");

		String[] xssArr = XSS_STR.split("\\|");
		for (int i = 0; i < xssArr.length; i++) {
			if (value.indexOf(xssArr[i]) > -1) {
				log.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
				log.error("请注意，值可能存在SQL注入风险!---> {}", value);
				throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
			}
		}
		//update-begin-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
		if (customXssString != null) {
			String[] xssArr2 = customXssString.split("\\|");
			for (int i = 0; i < xssArr2.length; i++) {
				if (value.indexOf(xssArr2[i]) > -1) {
					log.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
					log.error("请注意，值可能存在SQL注入风险!---> {}", value);
					throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
				}
			}
		}
		//update-end-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
		if(Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)){
			throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
		}
		return;
	}
}
