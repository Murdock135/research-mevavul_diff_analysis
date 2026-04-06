class connectPendingPopup {
public PopupInfo connectPendingPopup(final AwContents parentAwContents) throws Exception {
        TestAwContentsClient popupContentsClient;
        AwTestContainerView popupContainerView;
        final AwContents popupContents;
        popupContentsClient = new TestAwContentsClient();
        popupContainerView = createAwTestContainerViewOnMainSync(popupContentsClient);
        popupContents = popupContainerView.getAwContents();
        enableJavaScriptOnUiThread(popupContents);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                parentAwContents.supplyContentsForPopup(popupContents);
            }
        });

        OnPageFinishedHelper onPageFinishedHelper = popupContentsClient.getOnPageFinishedHelper();
        int callCount = onPageFinishedHelper.getCallCount();
        onPageFinishedHelper.waitForCallback(callCount, 1, WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        return new PopupInfo(popupContentsClient, popupContainerView, popupContents);
    }
}
