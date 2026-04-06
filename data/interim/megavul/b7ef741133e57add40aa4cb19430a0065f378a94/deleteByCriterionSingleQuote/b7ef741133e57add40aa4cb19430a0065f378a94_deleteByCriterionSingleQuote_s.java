class deleteByCriterionSingleQuote {
@Ignore("fails - SQL injection!")
  @Test
  public void deleteByCriterionSingleQuote(TestContext context) throws FieldException {
    deleteByCriterion(context, "'");  // SQL injection?
  }
}
