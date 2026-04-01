class releaseNativeResources {
@Override
    protected synchronized void releaseNativeResources() throws Exception {
        synchronized (globalRef) {
            if (globalRef != null) {
                try {
                    globalRef.close();
                } finally {
                    globalRef = null;
                }
            }
        }
    }
}
