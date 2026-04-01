class loadBaseFilterChain {
public static void loadBaseFilterChain(Map<String, String> filterChainDefinitionMap) {

        filterChainDefinitionMap.put("/resource/md/get/", "anon");
        filterChainDefinitionMap.put("/*.worker.js", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/signin", "anon");
        filterChainDefinitionMap.put("/ldap/signin", "anon");
        filterChainDefinitionMap.put("/ldap/open", "anon");
        filterChainDefinitionMap.put("/signout", "anon");
        filterChainDefinitionMap.put("/isLogin", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/display/info", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/display/file/**", "anon");
        filterChainDefinitionMap.put("/jmeter/download/**", "anon");
        filterChainDefinitionMap.put("/jmeter/ping", "anon");
        filterChainDefinitionMap.put("/jmeter/ready/**", "anon");
        filterChainDefinitionMap.put("/authsource/list/allenable", "anon");
        filterChainDefinitionMap.put("/sso/signin", "anon");
        filterChainDefinitionMap.put("/sso/callback", "anon");
        filterChainDefinitionMap.put("/license/valid", "anon");
        filterChainDefinitionMap.put("/api/jmeter/download", "anon");
        filterChainDefinitionMap.put("/api/jmeter/download/files", "anon");
        filterChainDefinitionMap.put("/api/jmeter/download/jar", "anon");
        filterChainDefinitionMap.put("/api/jmeter/download/plug/jar", "anon");

        // for swagger
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs/**", "anon");

        filterChainDefinitionMap.put("/403", "anon");
        filterChainDefinitionMap.put("/anonymous/**", "anon");

        //分享相关接口
        filterChainDefinitionMap.put("/share/info/generateShareInfoWithExpired", "anon");
        filterChainDefinitionMap.put("/share/info/selectApiInfoByParam", "anon");
        filterChainDefinitionMap.put("/share/info/selectHistoryReportById", "anon");
        filterChainDefinitionMap.put("/share/get/**", "anon");
        filterChainDefinitionMap.put("/share/info", "apikey, csrf, authc"); // 需要认证
        filterChainDefinitionMap.put("/document/**", "anon");
        filterChainDefinitionMap.put("/echartPic/**", "anon");
        filterChainDefinitionMap.put("/share/**", "anon");
        filterChainDefinitionMap.put("/sharePlanReport", "anon");

        filterChainDefinitionMap.put("/system/theme", "anon");
        filterChainDefinitionMap.put("/system/save/baseurl/**", "anon");
        filterChainDefinitionMap.put("/system/timeout", "anon");

        filterChainDefinitionMap.put("/v1/catalog/**", "anon");
        filterChainDefinitionMap.put("/v1/agent/**", "anon");
        filterChainDefinitionMap.put("/v1/health/**", "anon");
        //mock接口
        filterChainDefinitionMap.put("/mock/**", "anon");
        filterChainDefinitionMap.put("/ws/**", "anon");

        filterChainDefinitionMap.put("/plugin/**", "anon");

    }
}
