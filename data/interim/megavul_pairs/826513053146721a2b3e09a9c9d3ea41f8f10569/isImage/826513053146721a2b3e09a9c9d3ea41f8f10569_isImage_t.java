class isImage {
private boolean isImage(MultipartFile file) {
        BufferedImage image = null;
        try (InputStream input = file.getInputStream()) {
            image = ImageIO.read(input);
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return false;
        }
        Pattern pattern = Pattern.compile("\\.(png|jpg|jpeg|gif)$");
        Matcher matcher = pattern.matcher(file.getOriginalFilename().toLowerCase());
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0 || !matcher.find()) {
            return false;
        }

        return true;
    }
}
