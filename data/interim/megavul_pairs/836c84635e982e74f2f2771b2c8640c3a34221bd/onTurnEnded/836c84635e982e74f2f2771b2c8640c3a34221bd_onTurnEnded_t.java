class onTurnEnded {
@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("java.lang.SecurityException:")) {
			securityExceptionOccurred = true;	
		}	
	}
}
