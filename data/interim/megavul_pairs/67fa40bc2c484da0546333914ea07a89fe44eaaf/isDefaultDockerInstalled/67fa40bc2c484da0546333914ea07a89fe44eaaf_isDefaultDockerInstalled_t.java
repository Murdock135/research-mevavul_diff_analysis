class isDefaultDockerInstalled {
public static boolean isDefaultDockerInstalled() {
    try {
      new ProcessBuilder(DEFAULT_DOCKER_CLIENT.toString()).start();
      return true;
    } catch (IOException ex) {
      return false;
    }
  }
}
