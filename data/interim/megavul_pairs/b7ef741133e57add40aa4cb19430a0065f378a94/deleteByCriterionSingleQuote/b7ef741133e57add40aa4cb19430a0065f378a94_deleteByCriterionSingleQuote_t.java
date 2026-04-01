class deleteByCriterionSingleQuote {
@Test
  public void deleteByCriterionSingleQuote(TestContext context) throws FieldException {
    deleteByCriterion(context, "'");  // SQL injection?
  }
}
