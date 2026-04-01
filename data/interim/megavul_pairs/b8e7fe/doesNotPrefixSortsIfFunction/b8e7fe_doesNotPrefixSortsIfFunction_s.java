class doesNotPrefixSortsIfFunction {
@Test
	public void doesNotPrefixSortsIfFunction() {

		Sort sort = new Sort("sum(foo)");
		assertThat(applySorting("select p from Person p", sort, "p"), endsWith("order by sum(foo) asc"));
	}
}
