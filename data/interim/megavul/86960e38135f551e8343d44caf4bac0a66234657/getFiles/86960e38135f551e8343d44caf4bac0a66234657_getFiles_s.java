class getFiles {
@GetMapping("/listFiles")
    public String getFiles() throws JsonProcessingException {
        List<Map<String, String>> list = new ArrayList<>();
        File file = new File(fileDir + demoPath);
        if (file.exists()) {
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(file1 -> {
                Map<String, String> fileName = new HashMap<>();
                fileName.put("fileName", demoDir + "/" + file1.getName());
                list.add(fileName);
            });
        }
        return new ObjectMapper().writeValueAsString(list);
    }
}
