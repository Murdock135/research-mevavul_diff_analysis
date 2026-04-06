class setFormatParams {
@Test
  public void setFormatParams() throws Exception {
    assertPlotParam("yformat", "%25.2f");
    assertPlotParam("y2format", "%25.2f");
    assertPlotParam("xformat", "%25.2f");
    assertPlotParam("yformat", "%253.0em");
    assertPlotParam("yformat", "%253.0em%25%25");
    assertPlotParam("yformat", "%25.2f seconds");
    assertPlotParam("yformat", "%25.0f ms");
    assertInvalidPlotParam("yformat", "%252.system(%20");
    assertInvalidPlotParam("yformat", "%252.%0asystem(%20");
  }
}
