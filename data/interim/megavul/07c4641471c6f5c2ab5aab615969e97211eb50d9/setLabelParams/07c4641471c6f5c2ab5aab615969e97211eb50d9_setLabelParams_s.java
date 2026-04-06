class setLabelParams {
@Test
  public void setLabelParams() throws Exception {
    assertPlotParam("ylabel", "This is good");
    assertPlotParam("ylabel", " and so Is this - _ yay");
    assertInvalidPlotParam("ylabel", "[33:system(%20");
    assertInvalidPlotParam("title", "[33:system(%20");
    assertInvalidPlotParam("y2label", "[33:system(%20");
  }
}
