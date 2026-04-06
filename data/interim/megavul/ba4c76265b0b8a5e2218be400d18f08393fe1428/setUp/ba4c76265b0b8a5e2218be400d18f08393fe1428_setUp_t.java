class setUp {
public static void setUp(TestComponentManager tcm) throws Exception
    {
        LocalizationScriptService lss = mock(LocalizationScriptService.class);
        tcm.registerComponent(ScriptService.class, "localization", lss);

        // The translations are mocked by returning the translation, suffixed with the list of the String.valueOf
        // values of the translation parameters if they exist.
        // We mock the translations instead of using their actual values because they are subject to change from
        // Weblate, possibly making the build fail unexpectedly.
        when(lss.render(anyString())).thenAnswer(invocationOnMock -> {
                // Return the translation key as the value
                return renderString(invocationOnMock.getArgument(0), new Object[] {});
            }
        );
        when(lss.render(anyString(), anyCollection())).thenAnswer(invocationOnMock -> {
            // Displays the comma-separated list of parameters between squared brackets after the translation key as
            // the value, so that they can be verified in tests.
            // For instance: my.key [paramA, paramB]
            Collection<?> parameters = invocationOnMock.getArgument(1);
            return renderString(invocationOnMock.getArgument(0), parameters.toArray());
        });

        TranslationBundleContext translationBundleContext = tcm.getInstance(TranslationBundleContext.class);
        TranslationBundle translationBundle = mock(TranslationBundle.class);
        when(translationBundle.getTranslation(any(), any()))
            .thenAnswer(invocationOnMockTranslation -> {
                Translation translation = mock(Translation.class);
                when(translation.getLocale()).thenReturn(Locale.ENGLISH);
                String translationKey = invocationOnMockTranslation.getArgument(0);
                when(translation.getKey()).thenReturn(translationKey);
                when(translation.render(any())).thenAnswer(invocationOnMockRender -> {
                    Object[] parameters = getVarArgs(invocationOnMockRender, 0);
                    return renderBlock(translationKey, parameters);
                });
                when(translation.render(any(), any())).thenAnswer(invocationOnMockRender -> {
                    Object[] parameters = getVarArgs(invocationOnMockRender, 1);
                    return renderBlock(translationKey, parameters);
                });
                return translation;
            });
        translationBundleContext.addBundle(translationBundle);
    }
}
