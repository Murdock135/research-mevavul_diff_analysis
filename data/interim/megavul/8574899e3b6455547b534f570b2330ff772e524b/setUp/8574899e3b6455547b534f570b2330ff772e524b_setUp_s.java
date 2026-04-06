class setUp {
@Before
    public void setUp() throws Exception {
        super.setUp();
        sqlTestList.add(setUp(new SQLDataSetDefTest()));
        sqlTestList.add(setUp(new SQLDataSetTrimTest()));
        sqlTestList.add(setUp(new SQLTableDataSetLookupTest()));
        sqlTestList.add(setUp(new SQLQueryDataSetLookupTest()));
    }
}
