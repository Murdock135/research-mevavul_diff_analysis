class yamlPathToJson {
private JsonNode yamlPathToJson(Path path) throws IOException {
        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        Path p;
        
        try (InputStream in = Files.newInputStream(path)) {
        	return mapper.valueToTree(reader.load(in));
        }
    }
}
