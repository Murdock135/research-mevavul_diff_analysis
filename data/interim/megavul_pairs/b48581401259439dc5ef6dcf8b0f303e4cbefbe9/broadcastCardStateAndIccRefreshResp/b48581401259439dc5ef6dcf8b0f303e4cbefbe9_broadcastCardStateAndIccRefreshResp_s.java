class broadcastCardStateAndIccRefreshResp {
private void  broadcastCardStateAndIccRefreshResp(CardState cardState,
            IccRefreshResponse iccRefreshState) {
        Intent intent = new Intent(AppInterface.CAT_ICC_STATUS_CHANGE);
        boolean cardPresent = (cardState == CardState.CARDSTATE_PRESENT);

        if (iccRefreshState != null) {
            //This case is when MSG_ID_ICC_REFRESH is received.
            intent.putExtra(AppInterface.REFRESH_RESULT, iccRefreshState.refreshResult);
            CatLog.d(this, "Sending IccResult with Result: "
                    + iccRefreshState.refreshResult);
        }

        // This sends an intent with CARD_ABSENT (0 - false) /CARD_PRESENT (1 - true).
        intent.putExtra(AppInterface.CARD_STATUS, cardPresent);
        CatLog.d(this, "Sending Card Status: "
                + cardState + " " + "cardPresent: " + cardPresent);
        mContext.sendBroadcast(intent);
    }
}
