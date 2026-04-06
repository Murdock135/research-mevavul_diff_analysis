class yamlPathToJson {
private JsonNode yamlPathToJson(Path path) throws IOException {
        Yaml reader = new Yaml(new SafeConstructor());
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = Files.newInputStream(path)) {
        	return mapper.valueToTree(reader.load(in));
        }
    }
}
