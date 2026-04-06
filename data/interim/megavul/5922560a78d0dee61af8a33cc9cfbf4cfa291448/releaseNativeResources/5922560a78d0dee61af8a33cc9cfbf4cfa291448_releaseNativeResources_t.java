class releaseNativeResources {
@Override
    protected synchronized void releaseNativeResources() throws Exception {

        super.releaseNativeResources();

        if (globalRef != null) {
            try {
                globalRef.close();
            } finally {
                globalRef = null;
            }
        }
    }
}
