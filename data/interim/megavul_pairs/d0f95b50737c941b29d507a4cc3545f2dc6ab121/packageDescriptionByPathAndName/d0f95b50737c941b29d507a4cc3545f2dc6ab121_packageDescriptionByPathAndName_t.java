class packageDescriptionByPathAndName {
private String packageDescriptionByPathAndName(String path, String name) {
        String result = "";

        if (StringUtils.isNotEmpty(path)) {
            if (!path.startsWith("http")) {
                if (path.startsWith("{") && path.endsWith("}")) {
                    String srcContent = path.substring(1, path.length() - 1);
                    if (StringUtils.isEmpty(name)) {
                        name = srcContent;
                    }

                    if (Arrays.stream(imgArray).anyMatch(imgType -> StringUtils.equals(imgType, srcContent.substring(srcContent.indexOf('.') + 1)))) {
                        if (zentaoClient instanceof ZentaoGetClient) {
                            path = zentaoClient.getBaseUrl() + "/index.php?m=file&f=read&fileID=" + srcContent;
                        } else {
                            // 禅道开源版
                            path = zentaoClient.getBaseUrl() + "/file-read-" + srcContent;
                        }
                    } else {
                        return result;
                    }
                } else {
                    name = name.replaceAll("&amp;", "&");
                    path = path.replaceAll("&amp;", "&");
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (String item : path.split("&")) {
                    // 去掉多余的参数
                    if (!StringUtils.containsAny(item, "platform", "workspaceId")) {
                        stringBuilder.append(item);
                        stringBuilder.append("&");
                    }
                }
                path = getProxyPath(stringBuilder.toString());
            }
            // 图片与描述信息之间需换行，否则无法预览图片
            result = "\n\n![" + name + "](" + path + ")";
        }

        return result;
    }
}
