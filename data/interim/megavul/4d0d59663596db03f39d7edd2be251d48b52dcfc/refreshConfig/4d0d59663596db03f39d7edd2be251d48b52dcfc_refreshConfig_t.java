class refreshConfig {
public void refreshConfig() {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        StringWriter writer = new StringWriter();
        yaml.dump(configObj, writer);
        configStr = writer.toString();
        try {
            Files.write(Paths.get("config.yaml"),
                    configStr.getBytes(StandardCharsets.UTF_8));
            Files.write(Paths.get(configPath),
                    configStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }
}
