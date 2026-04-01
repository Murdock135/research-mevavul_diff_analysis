class setOrderBy {
public <E> Page<E> setOrderBy(String orderBy) {
        if (SqlSafeUtil.check(orderBy)) {
            throw new PageException("order by [" + orderBy + "] 存在 SQL 注入风险, 如想避免 SQL 注入校验，可以调用 Page.setUnsafeOrderBy");
        }
        this.orderBy = orderBy;
        return (Page<E>) this;
    }
}
