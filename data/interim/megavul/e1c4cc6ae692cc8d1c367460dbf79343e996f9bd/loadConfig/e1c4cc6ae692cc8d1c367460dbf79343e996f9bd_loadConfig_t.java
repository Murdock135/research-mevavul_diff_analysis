class loadConfig {
public static void loadConfig() {

		Properties props = loadProperties();
		if (props.isEmpty()) {
			return;
		}

		Config cfg = Config.getInstance();

		cfg.setProgramVersion(getStringProperty(props, PROGRAM_VERSION, Program.getInstance().getVersion()));
		cfg.setDefaultFontsize(getIntProperty(props, DEFAULT_FONTSIZE, cfg.getDefaultFontsize()));
		cfg.setPropertiesPanelFontsize(getIntProperty(props, PROPERTIES_PANEL_FONTSIZE, cfg.getPropertiesPanelFontsize()));
		cfg.setDefaultFontFamily(getStringProperty(props, DEFAULT_FONTFAMILY, cfg.getDefaultFontFamily()));
		SharedConfig.getInstance().setShow_stickingpolygon(getBoolProperty(props, SHOW_STICKINGPOLYGON, SharedConfig.getInstance().isShow_stickingpolygon()));
		cfg.setShow_grid(getBoolProperty(props, SHOW_GRID, cfg.isShow_grid()));
		cfg.setEnable_custom_elements(getBoolProperty(props, ENABLE_CUSTOM_ELEMENTS, cfg.isEnable_custom_elements()));
		cfg.setUiManager(getStringProperty(props, UI_MANAGER, cfg.getUiManager()));
		cfg.setPrintPadding(getIntProperty(props, PRINT_PADDING, cfg.getPrintPadding()));
		cfg.setPdfExportFont(getStringProperty(props, PDF_EXPORT_FONT, cfg.getPdfExportFont()));
		cfg.setPdfExportFontBold(getStringProperty(props, PDF_EXPORT_FONT_BOLD, cfg.getPdfExportFontBold()));
		cfg.setPdfExportFontItalic(getStringProperty(props, PDF_EXPORT_FONT_ITALIC, cfg.getPdfExportFontItalic()));
		cfg.setPdfExportFontBoldItalic(getStringProperty(props, PDF_EXPORT_FONT_BOLDITALIC, cfg.getPdfExportFontBoldItalic()));
		cfg.setCheckForUpdates(getBoolProperty(props, CHECK_FOR_UPDATES, cfg.isCheckForUpdates()));
		cfg.setSecureXmlProcessing(getBoolProperty(props, SECURE_XML_PROCESSING, cfg.isSecureXmlProcessing()));
		cfg.setOpenFileHome(getStringProperty(props, OPEN_FILE_HOME, cfg.getOpenFileHome()));
		cfg.setSaveFileHome(getStringProperty(props, SAVE_FILE_HOME, cfg.getSaveFileHome()));
		SharedConfig.getInstance().setDev_mode(getBoolProperty(props, DEV_MODE, SharedConfig.getInstance().isDev_mode()));
		cfg.setLastUsedPalette(getStringProperty(props, LAST_USED_PALETTE, cfg.getLastUsedPalette()));
		cfg.setMain_split_position(getIntProperty(props, MAIN_SPLIT_POSITION, cfg.getMain_split_position()));
		cfg.setRight_split_position(getIntProperty(props, RIGHT_SPLIT_POSITION, cfg.getRight_split_position()));
		cfg.setMail_split_position(getIntProperty(props, MAIL_SPLIT_POSITION, cfg.getMail_split_position()));
		cfg.setStart_maximized(getBoolProperty(props, START_MAXIMIZED, cfg.isStart_maximized()));

		// In case of start_maximized=true we don't store any size or location information
		if (!cfg.isStart_maximized()) {
			cfg.setProgram_size(getDimensionProperty(props, PROGRAM_SIZE, cfg.getProgram_size()));
			cfg.setProgram_location(getPointProperty(props, PROGRAM_LOCATION, cfg.getProgram_location()));
		}

		String recentFiles = props.getProperty(RECENT_FILES);
		if (recentFiles != null) {
			RecentlyUsedFilesList.getInstance().addAll(Arrays.asList(props.getProperty(RECENT_FILES).split("\\|")));
		}

		/* Mail */
		ConfigMail cfgMail = ConfigMail.getInstance();
		cfgMail.setMail_smtp(getStringProperty(props, MAIL_SMTP, cfgMail.getMail_smtp()));
		cfgMail.setMail_smtp_auth(getBoolProperty(props, MAIL_SMTP_AUTH, cfgMail.isMail_smtp_auth()));
		cfgMail.setMail_smtp_user(getStringProperty(props, MAIL_SMTP_USER, cfgMail.getMail_smtp_user()));
		cfgMail.setMail_smtp_pw_store(getBoolProperty(props, MAIL_SMTP_PW_STORE, cfgMail.isMail_smtp_pw_store()));
		cfgMail.setMail_smtp_pw(getStringProperty(props, MAIL_SMTP_PW, cfgMail.getMail_smtp_pw()));
		cfgMail.setMail_from(getStringProperty(props, MAIL_FROM, cfgMail.getMail_from()));
		cfgMail.setMail_to(getStringProperty(props, MAIL_TO, cfgMail.getMail_to()));
		cfgMail.setMail_cc(getStringProperty(props, MAIL_CC, cfgMail.getMail_cc()));
		cfgMail.setMail_bcc(getStringProperty(props, MAIL_BCC, cfgMail.getMail_bcc()));
		cfgMail.setMail_xml(getBoolProperty(props, MAIL_XML, cfgMail.isMail_xml()));
		cfgMail.setMail_gif(getBoolProperty(props, MAIL_GIF, cfgMail.isMail_gif()));
		cfgMail.setMail_pdf(getBoolProperty(props, MAIL_PDF, cfgMail.isMail_pdf()));

		/* Generate Class Element Options */
		ConfigClassGen genCfg = ConfigClassGen.getInstance();
		genCfg.setGenerateClassPackage(getBoolProperty(props, GENERATE_CLASS_PACKAGE, genCfg.isGenerateClassPackage()));
		genCfg.setGenerateClassFields(FieldOptions.getEnum(getStringProperty(props, GENERATE_CLASS_FIELDS, genCfg.getGenerateClassFields().toString())));
		genCfg.setGenerateClassMethods(MethodOptions.getEnum(getStringProperty(props, GENERATE_CLASS_METHODS, genCfg.getGenerateClassMethods().toString())));
		genCfg.setGenerateClassSignatures(SignatureOptions.getEnum(getStringProperty(props, GENERATE_CLASS_SIGNATURES, genCfg.getGenerateClassSignatures().toString())));
		genCfg.setGenerateClassSortings(SortOptions.getEnum(getStringProperty(props, GENERATE_CLASS_SORTINGS, genCfg.getGenerateClassSortings().toString())));

	}
}
