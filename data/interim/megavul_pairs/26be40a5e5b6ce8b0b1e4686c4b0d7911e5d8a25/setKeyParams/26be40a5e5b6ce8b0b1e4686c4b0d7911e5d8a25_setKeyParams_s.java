class setKeyParams {
@Test
  public void setKeyParams() throws Exception {
    assertPlotParam("key", "out");
    assertPlotParam("key", "left");
    assertPlotParam("key", "top");
    assertPlotParam("key", "center");
    assertPlotParam("key", "right");
    assertPlotParam("key", "horiz");
    assertPlotParam("key", "box");
    assertPlotParam("key", "bottom");
    assertInvalidPlotParam("yrange", "out%20right%20top%0aset%20yrange%20[33:system(%20");
  }
}
