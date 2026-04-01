class setLabelParams {
@Test
  public void setLabelParams() throws Exception {
    assertPlotParam("ylabel", "This is good");
    assertPlotParam("ylabel", " and so Is this - _ yay");
    assertInvalidPlotParam("ylabel", "system(%20no%0anewlines");
    assertInvalidPlotParam("title", "system(%20no%0anewlines");
    assertInvalidPlotParam("y2label", "system(%20no%0anewlines");
  }
}
