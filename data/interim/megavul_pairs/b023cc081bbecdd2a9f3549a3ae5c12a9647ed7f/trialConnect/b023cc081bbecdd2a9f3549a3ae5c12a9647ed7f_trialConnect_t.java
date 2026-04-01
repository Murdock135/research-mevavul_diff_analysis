class trialConnect {
private void trialConnect() throws IOException, RetryDirectly, IllegalAccessException,
            FileDownloadSecurityException {
        FileDownloadConnection trialConnection = null;
        try {
            final ConnectionProfile trialConnectionProfile;
            if (isNeedForceDiscardRange) {
                trialConnectionProfile = ConnectionProfile.ConnectionProfileBuild
                        .buildTrialConnectionProfileNoRange();
            } else {
                trialConnectionProfile = ConnectionProfile.ConnectionProfileBuild
                        .buildTrialConnectionProfile();
            }
            final ConnectTask trialConnectTask = new ConnectTask.Builder()
                    .setDownloadId(model.getId())
                    .setUrl(model.getUrl())
                    .setEtag(model.getETag())
                    .setHeader(userRequestHeader)
                    .setConnectionProfile(trialConnectionProfile)
                    .build();
            trialConnection = trialConnectTask.connect();
            handleTrialConnectResult(trialConnectTask.getRequestHeader(),
                    trialConnectTask, trialConnection);

        } finally {
            if (trialConnection != null) trialConnection.ending();
        }
    }
}
