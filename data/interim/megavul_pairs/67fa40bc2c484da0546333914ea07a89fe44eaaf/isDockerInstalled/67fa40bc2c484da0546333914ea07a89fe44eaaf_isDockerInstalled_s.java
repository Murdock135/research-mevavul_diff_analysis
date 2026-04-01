class isDockerInstalled {
public static boolean isDockerInstalled(Path dockerExecutable) {
    try {
      new ProcessBuilder(dockerExecutable.toString()).start();
      return true;

    } catch (IOException ex) {
      return false;
    }
  }
}
