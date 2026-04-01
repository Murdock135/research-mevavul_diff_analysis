class getMdImage {
public ResponseEntity<FileSystemResource> getMdImage(String name) {
        if (name.contains("/"))
            MSException.throwException(Translator.get("invalid_parameter"));
        File file = new File(FileUtils.MD_IMAGE_DIR + "/" + name);
        HttpHeaders headers = new HttpHeaders();
        String fileName = "";
        try {
            fileName = URLEncoder.encode(file.getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }
}
