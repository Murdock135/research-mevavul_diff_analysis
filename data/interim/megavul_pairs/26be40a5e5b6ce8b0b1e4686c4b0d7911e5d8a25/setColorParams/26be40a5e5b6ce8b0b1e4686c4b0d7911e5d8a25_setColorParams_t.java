class setColorParams {
@Test
  public void setColorParams() throws Exception {
    assertPlotParam("bgcolor", "x000000");
    assertPlotParam("bgcolor", "XDEADBE");
    assertPlotParam("bgcolor", "%58DEADBE");
    assertInvalidPlotParam("bgcolor", "XDEADBEF");
    assertInvalidPlotParam("bgcolor", "%5BDEADBE");
    assertInvalidPlotParam("bgcolor", "xBDE%0AAD");

    assertPlotParam("fgcolor", "x000000");
    assertPlotParam("fgcolor", "XDEADBE");
    assertPlotParam("fgcolor", "%58DEADBE");
    assertInvalidPlotParam("fgcolor", "XDEADBEF");
    assertInvalidPlotParam("fgcolor", "%5BDEADBE");
    assertInvalidPlotParam("fgcolor", "xBDE%0AAD");
  }
}
