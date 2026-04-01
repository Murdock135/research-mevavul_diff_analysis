class handleVmArgs {
private static int handleVmArgs(JavaConfig javaConfig) {
		if (javaConfig.getLaunchProperties() == null) {
			System.out.println("Launch properties file was not specified!");
			return EXIT_FAILURE;
		}

		javaConfig.getLaunchProperties().getVmArgList().forEach(arg -> System.out.println(arg));
		return EXIT_SUCCESS;
	}
}
