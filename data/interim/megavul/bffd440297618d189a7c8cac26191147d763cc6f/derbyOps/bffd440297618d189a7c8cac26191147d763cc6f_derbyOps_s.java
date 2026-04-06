class derbyOps {
@GetMapping(value = "/derby")
    public RestResult<Object> derbyOps(@RequestParam(value = "sql") String sql) {
        String selectSign = "select";
        String limitSign = "ROWS FETCH NEXT";
        String limit = " OFFSET 0 ROWS FETCH NEXT 1000 ROWS ONLY";
        try {
            if (PropertyUtil.isEmbeddedStorage()) {
                LocalDataSourceServiceImpl dataSourceService = (LocalDataSourceServiceImpl) DynamicDataSource
                        .getInstance().getDataSource();
                if (StringUtils.startsWithIgnoreCase(sql, selectSign)) {
                    if (!StringUtils.containsIgnoreCase(sql, limitSign)) {
                        sql += limit;
                    }
                    JdbcTemplate template = dataSourceService.getJdbcTemplate();
                    List<Map<String, Object>> result = template.queryForList(sql);
                    return RestResultUtils.success(result);
                }
                return RestResultUtils.failed("Only query statements are allowed to be executed");
            }
            return RestResultUtils.failed("The current storage mode is not Derby");
        } catch (Exception e) {
            return RestResultUtils.failed(e.getMessage());
        }
    }
}
