class getDefaultConfiguration {
@Override
    public HTMLCleanerConfiguration getDefaultConfiguration()
    {
        HTMLCleanerConfiguration configuration = new DefaultHTMLCleanerConfiguration();
        configuration.setFilters(Arrays.asList(
            this.controlFilter,
            this.bodyFilter,
            this.listItemFilter,
            this.listFilter,
            this.fontFilter,
            this.attributeFilter,
            this.linkFilter,
            this.sanitizerFilter));
        return configuration;
    }
}
