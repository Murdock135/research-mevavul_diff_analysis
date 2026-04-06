class yamlStreamToJson {
private JsonNode yamlStreamToJson(InputStream yamlStream) {
        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(reader.load(yamlStream));
    }
}
