class setupClass {
@BeforeAll
    public void setupClass(TestUtils testUtils)
    {
        testUtils.createUserAndLogin(USERNAME, PASSWORD);
    }
}
