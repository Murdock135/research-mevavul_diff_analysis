class getConfiguration {
public static Configuration getConfiguration(AMWTemplateExceptionHandler templateExceptionHandler) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        // prevents Server-Side Template Injection
        cfg.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        cfg.setAPIBuiltinEnabled(false);
        cfg.setTemplateExceptionHandler(templateExceptionHandler);
        cfg.setShowErrorTips(false);
        cfg.setLogTemplateExceptions(false);
        // because otherwise freemarker renders numbers by default like 1,000,000 http://freemarker.org/docs/app_faq.html#faq_number_grouping
        cfg.setNumberFormat("0.######");
        return cfg;
    }
}
