class execute {
@Override
            public void execute(Runnable command) {
                current.runOnContext(new Handler<Void>() {
                    @Override
                    public void handle(Void unused) {
                        command.run();
                    }
                });
            }
}
