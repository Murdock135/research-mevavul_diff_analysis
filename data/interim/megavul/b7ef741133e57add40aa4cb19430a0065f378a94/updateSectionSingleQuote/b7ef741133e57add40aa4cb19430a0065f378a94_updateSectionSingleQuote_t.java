class updateSectionSingleQuote {
@Test
  public void updateSectionSingleQuote(TestContext context) {
    UpdateSection updateSection = new UpdateSection();
    updateSection.addField("key").setValue("'");

    postgresClient = createFoo(context);
    postgresClient.save(FOO, xPojo, context.asyncAssertSuccess(save -> {
      postgresClient.update(FOO, updateSection, (Criterion) null, true, context.asyncAssertSuccess(update -> {
        context.assertEquals(1, update.getUpdated(), "number of records updated");
        postgresClient.selectSingle("SELECT jsonb->>'key' FROM " + FOO, context.asyncAssertSuccess(select -> {
          context.assertEquals("'", select.getString(0), "single quote");
        }));
      }));
    }));
  }
}
