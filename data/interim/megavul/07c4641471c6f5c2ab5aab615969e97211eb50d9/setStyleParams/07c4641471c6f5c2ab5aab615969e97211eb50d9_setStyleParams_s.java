class setStyleParams {
@Test
  public void setStyleParams() throws Exception {
    assertPlotParam("style", "linespoint");
    assertPlotParam("style", "points");
    assertPlotParam("style", "circles");
    assertPlotParam("style", "dots");
    assertInvalidPlotParam("style", "dots%20[33:system(%20");
  }
}
