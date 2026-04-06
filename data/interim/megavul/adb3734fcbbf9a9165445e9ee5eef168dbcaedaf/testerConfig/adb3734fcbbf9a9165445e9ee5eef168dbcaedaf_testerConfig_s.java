class testerConfig {
@Bean
	public TesterConfig testerConfig() {
		TesterConfig retVal = new TesterConfig();
		retVal
			.addServer()
				.withId("internal")
				.withFhirVersion(FhirVersionEnum.DSTU2)
				.withBaseUrl("http://localhost:8888/fhir")
				.withName("Localhost Server")
				.allowsApiKey()
			.addServer()
				.withId("hapi")
				.withFhirVersion(FhirVersionEnum.DSTU2)
				.withBaseUrl("http://fhirtest.uhn.ca/baseDstu2")
				.withName("Public HAPI Test Server")
				.allowsApiKey()
			.addServer()
				.withId("home3")
				.withFhirVersion(FhirVersionEnum.DSTU3)
				.withBaseUrl("http://fhirtest.uhn.ca/baseDstu3")
				.withName("Public HAPI Test Server (STU3)")
			.addServer()
				.withId("home")
				.withFhirVersion(FhirVersionEnum.DSTU2)
				.withBaseUrl("${serverBase}/baseDstu2")
				.withName("Local Tester");
		return retVal;
	}
}
