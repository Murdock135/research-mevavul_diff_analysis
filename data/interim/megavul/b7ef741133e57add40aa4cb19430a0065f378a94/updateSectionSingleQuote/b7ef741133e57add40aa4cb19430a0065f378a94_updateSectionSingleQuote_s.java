class updateSectionSingleQuote {
@Ignore("fails: unterminated quoted identifier")
  @Test
  public void updateSectionSingleQuote(TestContext context) {
    UpdateSection updateSection = new UpdateSection();
    updateSection.addField("key").setValue("'");
    createFoo(context)
      .update(FOO, updateSection, (Criterion) null, false, context.asyncAssertSuccess());
  }
}
