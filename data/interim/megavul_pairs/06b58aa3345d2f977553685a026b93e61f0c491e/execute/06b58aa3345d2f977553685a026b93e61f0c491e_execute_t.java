class execute {
public void execute() throws NMapInitializationException, NMapExecutionException {
        nmapExecutor = new NMapExecutor(flags, nmapProperties);
        results = nmapExecutor.execute();
    }
}
