class execute {
@Override
            public void execute(Runnable command) {
                internal.runOnContext(new Handler<Void>() {
                    @Override
                    public void handle(Void unused) {
                        command.run();
                    }
                });
            }
}
