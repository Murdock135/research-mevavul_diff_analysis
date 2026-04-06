class getImageFiles {
public List<File> getImageFiles(String input) {
        List<File> files = new ArrayList<>();
        String regex = "(\\!\\[.*?\\]\\((.*?)\\))";
        Pattern pattern = Pattern.compile(regex);
        if (StringUtils.isBlank(input)) {
            return new ArrayList<>();
        }
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            try {
                String path = matcher.group(2);
                if (!path.contains("/resource/md/get/url") && !path.contains("/resource/md/get/path")) {
                    if (path.contains("/resource/md/get/")) { // 兼容旧数据
                        String name = path.substring(path.indexOf("/resource/md/get/") + 17);
                        files.add(new File(FileUtils.MD_IMAGE_DIR + "/" + name));
                    } else if (path.contains("/resource/md/get")) { // 新数据走这里
                        String name = path.substring(path.indexOf("/resource/md/get") + 26);
                        files.add(new File(FileUtils.MD_IMAGE_DIR + "/" + URLDecoder.decode(name, StandardCharsets.UTF_8.name())));
                    }
                }
            } catch (Exception e) {
                LogUtil.error(e.getMessage(), e);
            }
        }
        return files;
    }
}
