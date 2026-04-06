class setKexDone {
private void setKexDone() {
        kexOngoing.set(false);
        kexInitSent.clear();
        done.set();
    }
}
