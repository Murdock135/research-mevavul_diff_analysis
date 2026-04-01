class export {
@GetMapping(value = "export/{modelId}/{type}")
    @ApiOperation("导出模型")
    @Authorize(action = "export")
    @SneakyThrows
    public void export(@PathVariable("modelId") @ApiParam("模型ID") String modelId,
                       @PathVariable("type") @ApiParam(value = "类型", allowableValues = "bpmn,json", example = "json")
                               ModelType type,
                       @ApiParam(hidden = true) HttpServletResponse response) {
        Model modelData = repositoryService.getModel(modelId);
        if (modelData == null) {
            throw new NotFoundException("模型不存在");
        }
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        byte[] modelEditorSource = repositoryService.getModelEditorSource(modelData.getId());

        JsonNode editorNode = new ObjectMapper().readTree(modelEditorSource);
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);

        // 处理异常
        if (bpmnModel.getMainProcess() == null) {
            throw new UnsupportedOperationException("无法导出模型文件:" + type);
        }

        String filename = "";
        byte[] exportBytes = null;

        String mainProcessId = bpmnModel.getMainProcess().getId();

        if (type == ModelType.bpmn) {
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            exportBytes = xmlConverter.convertToXML(bpmnModel);
            filename = mainProcessId + ".bpmn20.xml";
        } else if (type == ModelType.json) {
            exportBytes = modelEditorSource;
            filename = mainProcessId + ".json";

        } else {
            throw new UnsupportedOperationException("不支持的格式:" + type);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));

        /*创建输入流*/
        try (ByteArrayInputStream in = new ByteArrayInputStream(exportBytes)) {
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        }
    }
}
