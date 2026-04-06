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
    assertInvalidPlotParam("key", "out%20right%20top%0aset%20yrange%20[33:system(%20");
    assertInvalidPlotParam("key", "%3Bsystem%20%22cat%20/home/ubuntuvm/secret.txt%20%3E/tmp/secret.txt%22%20%22");
  }
}
