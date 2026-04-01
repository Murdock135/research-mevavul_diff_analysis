class afterFeaturesReceived {
@Override
    protected void afterFeaturesReceived() throws NotConnectedException, InterruptedException {
        StartTls startTlsFeature = getFeature(StartTls.ELEMENT, StartTls.NAMESPACE);
        if (startTlsFeature != null) {
            if (startTlsFeature.required() && config.getSecurityMode() == SecurityMode.disabled) {
                notifyConnectionError(new SecurityRequiredByServerException());
                return;
            }

            if (config.getSecurityMode() != ConnectionConfiguration.SecurityMode.disabled) {
                sendNonza(new StartTls());
            }
        }

        if (getSASLAuthentication().authenticationSuccessful()) {
            // If we have received features after the SASL has been successfully completed, then we
            // have also *maybe* received, as it is an optional feature, the compression feature
            // from the server.
            maybeCompressFeaturesReceived.reportSuccess();
        }
    }
}
