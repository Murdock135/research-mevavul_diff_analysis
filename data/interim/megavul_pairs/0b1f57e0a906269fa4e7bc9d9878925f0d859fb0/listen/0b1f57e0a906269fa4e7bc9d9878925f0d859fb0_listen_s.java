class listen {
public static void listen(final Consumer<String[]> consumer) throws Exception {
        final ServerSocket listener = new ServerSocket(PORT);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try (Socket socket = listener.accept();
                         ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                        // Receive args from another JD-GUI instance
                        String[] args = (String[])ois.readObject();
                        consumer.accept(args);
                    } catch (IOException|ClassNotFoundException e) {
                        assert ExceptionUtil.printStackTrace(e);
                    }
                }
            }
        };

        new Thread(runnable).start();
    }
}
