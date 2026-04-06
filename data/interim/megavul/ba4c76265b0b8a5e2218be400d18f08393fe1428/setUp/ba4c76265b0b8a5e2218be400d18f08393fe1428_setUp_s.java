class setUp {
public static void setUp(TestComponentManager tcm) throws Exception
    {
        LocalizationScriptService lss = mock(LocalizationScriptService.class);
        tcm.registerComponent(ScriptService.class, "localization", lss);
        
        // The translations are mocked by returning the translation, suffixed with the list of the String.valueOf
        // values of the translation parameters if they exist.
        // We mock the translations instead of using their actual values because they are subject to change from
        // Weblate, possibly making the build fail unexpectedly.
        when(lss.render(anyString())).thenAnswer(
            (Answer<String>) invocationOnMock -> {
                // Return the translation key as the value
                return invocationOnMock.getArgument(0);
            }
        );
        when(lss.render(anyString(), anyCollection())).thenAnswer((Answer<String>) invocationOnMock -> {
            // Displays the comma-separated list of parameters between squared brackets after the translation key as
            // the value, so that they can be verified in tests.
            // For instance: my.key [paramA, paramB]
            Object key = invocationOnMock.getArgument(0);
            Collection<?> parameters = invocationOnMock.getArgument(1);
            return parameters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", key + " [", "]"));
        });
    }
}
