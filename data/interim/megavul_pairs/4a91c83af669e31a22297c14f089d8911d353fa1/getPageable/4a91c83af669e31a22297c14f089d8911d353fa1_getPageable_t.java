class getPageable {
public PageableRequest getPageable() {
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setRows(getParaToInt("rows",10));
        pageableRequest.setSort(getPara("sidx"));
        pageableRequest.setOrder(getPara("sord"));
        pageableRequest.setPage(getParaToInt("page",1));
        return pageableRequest;
    }
}
