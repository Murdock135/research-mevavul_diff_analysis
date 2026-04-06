class setStyleParams {
@Test
  public void setStyleParams() throws Exception {
    assertPlotParam("style", "linespoint");
    assertPlotParam("style", "points");
    assertPlotParam("style", "circles");
    assertPlotParam("style", "dots");
    assertInvalidPlotParam("style", "dots%20%0a[33:system(%20");
    assertInvalidPlotParam("style", "%3Bsystem%20%22cat%20/home/ubuntuvm/secret.txt%20%3E/tmp/secret.txt%22%20%22\"");
  }
}
