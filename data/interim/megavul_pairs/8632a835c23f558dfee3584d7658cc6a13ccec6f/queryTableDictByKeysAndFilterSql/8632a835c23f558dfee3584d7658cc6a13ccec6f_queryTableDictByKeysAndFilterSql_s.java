class queryTableDictByKeysAndFilterSql {
List<DictModel> queryTableDictByKeysAndFilterSql(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql,  @Param("codeValues") List<String> codeValues);
}
