class setKexDone {
private void setKexDone() {
        kexOngoing.set(false);
        initialKex.set(false);
        if (strictKex.get()) {
            transport.getDecoder().resetSequenceNumber();
        }
        kexInitSent.clear();
        done.set();
    }
}
