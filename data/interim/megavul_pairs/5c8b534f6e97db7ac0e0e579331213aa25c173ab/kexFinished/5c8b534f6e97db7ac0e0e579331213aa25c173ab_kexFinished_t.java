class kexFinished {
public void kexFinished() {
		firstKexFinished = true;

		synchronized (connectionSemaphore)
		{
			flagKexOngoing = false;
			connectionSemaphore.notifyAll();
		}
	}
}
