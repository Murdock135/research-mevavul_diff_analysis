class createShowVersion2 {
public static PSystemVersion createShowVersion2(UmlSource source) {
		final List<String> strings = new ArrayList<>();
		strings.add("<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")");
		strings.add("(" + License.getCurrent() + " source distribution)");
		// :: uncomment when __CORE__
//		strings.add(" ");
//		strings.add("Compiled with CheerpJ 2.3");
//		strings.add("Powered by CheerpJ, a Leaning Technologies Java tool");
		// :: done
		// :: comment when __CORE__
		GraphvizCrash.checkOldVersionWarning(strings);
		if (OptionFlags.ALLOW_INCLUDE) {
			if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE)
				strings.add("Loaded from " + Version.getJarPath());

			if (OptionFlags.getInstance().isWord()) {
				strings.add("Word Mode");
				strings.add("Command Line: " + Run.getCommandLine());
				strings.add("Current Dir: " + new SFile(".").getAbsolutePath());
				strings.add("plantuml.include.path: " + PreprocessorUtils.getenv(SecurityUtils.PATHS_INCLUDES));
			}
		}
		strings.add(" ");

		GraphvizUtils.addDotStatus(strings, true);
		strings.add(" ");
		for (String name : OptionPrint.interestingProperties())
			strings.add(name);

		for (String v : OptionPrint.interestingValues())
			strings.add(v);

		// ::done

		return new PSystemVersion(source, true, strings);
	}
}
