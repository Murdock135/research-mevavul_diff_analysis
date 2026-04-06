class createPendingIntent {
public PendingIntent createPendingIntent(
            RequestInfo requestInfo, ArrayList<ProviderData> providerDataList) {
        List<CredentialProviderInfo> allProviders =
                CredentialProviderInfoFactory.getCredentialProviderServices(
                        mContext,
                        mUserId,
                        CredentialManager.PROVIDER_FILTER_USER_PROVIDERS_ONLY,
                        mEnabledProviders,
                        // Don't need primary providers here.
                        new HashSet<String>());

        List<DisabledProviderData> disabledProviderDataList = allProviders.stream()
                .filter(provider -> !provider.isEnabled())
                .map(disabledProvider -> new DisabledProviderData(
                        disabledProvider.getComponentName().flattenToString())).toList();

        Intent intent = IntentFactory.createCredentialSelectorIntent(requestInfo, providerDataList,
                        new ArrayList<>(disabledProviderDataList), mResultReceiver)
                .setAction(UUID.randomUUID().toString());
        //TODO: Create unique pending intent using request code and cancel any pre-existing pending
        // intents
        return PendingIntent.getActivityAsUser(
                mContext, /*requestCode=*/0, intent,
                PendingIntent.FLAG_IMMUTABLE, /*options=*/null,
                UserHandle.of(mUserId));
    }
}
