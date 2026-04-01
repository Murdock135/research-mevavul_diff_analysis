class list {
@RequestMapping("/")
    @ResponseBody
    public String list(Model model, // TODO model should no longer be injected
                       @RequestParam(required = false, defaultValue = "FILENAME") SortBy sortBy,
                       @RequestParam(required = false, defaultValue = "false") boolean desc,
                       @RequestParam(required = false) String base) throws IOException, TemplateException {
        Path currentFolder = loggingPath(base);
        securityCheck(currentFolder, null);


        List<FileEntry> files = getFileProvider(currentFolder).getFileEntries(currentFolder);
        List<FileEntry> sortedFiles = sortFiles(files, sortBy, desc);

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("desc", desc);
        model.addAttribute("files", sortedFiles);
        model.addAttribute("currentFolder", currentFolder.toAbsolutePath().toString());
        model.addAttribute("base", base != null ? URLEncoder.encode(base, "UTF-8") : "");
        model.addAttribute("parent", getParent(currentFolder));
        model.addAttribute("stylesheets", stylesheets);

        return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("logview.ftl"), model);
    }
}
