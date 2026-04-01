class isDockerInstalled {
public static boolean isDockerInstalled(Path dockerExecutable) {
    return Files.exists(dockerExecutable);
  }
}
