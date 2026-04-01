class createTempDir {
@Beta
  @Deprecated
  @J2ObjCIncompatible
  public static File createTempDir() {
    return TempFileCreator.INSTANCE.createTempDir();
  }
}
