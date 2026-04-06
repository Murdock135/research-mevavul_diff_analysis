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
        int finishCallCount = onPageFinishedHelper.getCallCount();
        TestAwContentsClient.OnReceivedTitleHelper onReceivedTitleHelper =
                popupContentsClient.getOnReceivedTitleHelper();
        int titleCallCount = onReceivedTitleHelper.getCallCount();

        onPageFinishedHelper.waitForCallback(finishCallCount, 1, WAIT_TIMEOUT_MS,
                TimeUnit.MILLISECONDS);
        onReceivedTitleHelper.waitForCallback(titleCallCount, 1, WAIT_TIMEOUT_MS,
                TimeUnit.MILLISECONDS);

        return new PopupInfo(popupContentsClient, popupContainerView, popupContents);
    }
}
