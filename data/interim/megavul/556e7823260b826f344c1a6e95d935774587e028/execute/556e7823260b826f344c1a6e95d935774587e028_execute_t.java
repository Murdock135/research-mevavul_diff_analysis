class execute {
@Override
    public List<Block> execute(LiveDataMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        // Load the JavaScript code of the Live Data widget.
        Map<String, Object> skinExtensionParameters = singletonMap("forceSkinAction", Boolean.TRUE);
        this.jsfx.use("uicomponents/widgets/liveData.js", skinExtensionParameters);

        GroupBlock output = new GroupBlock();
        output.setParameter("class", "liveData loading");
        if (parameters.getId() != null) {
            output.setParameter("id", parameters.getId());
        }
        try {
            // Compute the live data configuration based on the macro parameters.
            LiveDataConfiguration liveDataConfig = this.liveDataMacroConfiguration.getLiveDataConfiguration(content,
                parameters);
            // Add the default values.
            liveDataConfig = this.defaultLiveDataConfigResolver.resolve(liveDataConfig);
            // Serialize as JSON.
            ObjectMapper objectMapper = new ObjectMapper();
            output.setParameter("data-config", objectMapper.writeValueAsString(liveDataConfig));
            // The content is trusted if the author has script right, or if no advanced configuration is used (i.e., 
            // no macro content), and we are running in a trusted context.
            boolean trustedContent =
                StringUtils.isBlank(content) || (this.liveDataMacroRights.authorHasScriptRight()
                    && !context.getTransformationContext().isRestricted());
            output.setParameter("data-config-content-trusted", Boolean.toString(trustedContent));
        } catch (Exception e) {
            throw new MacroExecutionException("Failed to generate live data configuration from macro parameters.", e);
        }
        return singletonList(output);
    }
}
