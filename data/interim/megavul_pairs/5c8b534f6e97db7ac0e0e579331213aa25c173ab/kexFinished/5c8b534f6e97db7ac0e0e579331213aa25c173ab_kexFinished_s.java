class kexFinished {
public void kexFinished() {
		synchronized (connectionSemaphore)
		{
			flagKexOngoing = false;
			connectionSemaphore.notifyAll();
		}
	}
}
