class getConfiguration {
public static Configuration getConfiguration(AMWTemplateExceptionHandler templateExceptionHandler) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateExceptionHandler(templateExceptionHandler);
        cfg.setShowErrorTips(false);
        cfg.setLogTemplateExceptions(false);
        // because otherwise freemarker renders numbers by default like 1,000,000 http://freemarker.org/docs/app_faq.html#faq_number_grouping
        cfg.setNumberFormat("0.######");
        return cfg;
    }
}
