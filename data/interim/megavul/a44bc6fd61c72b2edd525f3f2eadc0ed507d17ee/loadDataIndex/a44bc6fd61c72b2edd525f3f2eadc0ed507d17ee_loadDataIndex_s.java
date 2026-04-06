class loadDataIndex {
@GetMapping("/admin/rbstore/load-index")
    public JSONAware loadDataIndex(HttpServletRequest request) {
        String type = getParameterNotNull(request, "type");
        JSON index = RBStore.fetchRemoteJson(type + "/index.json");
        return index == null ? RespBody.error() : index;
    }
}
