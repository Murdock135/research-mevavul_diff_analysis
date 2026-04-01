class getFilePath {
protected String getFilePath(String path) {
        String contextPath = this.get.get("contextPath");
        // 根目录
        if (StrUtils.isEmpty(contextPath)) {
            return path;
        }

        if (path.startsWith(contextPath)) {
            path = path.replaceFirst(contextPath, "");
        }
        return path;
    }
}
