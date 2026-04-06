class onWalletCardsRetrieved {
@Override
        public void onWalletCardsRetrieved(@NonNull GetWalletCardsResponse response) {
            Log.i(TAG, "Successfully retrieved wallet cards.");
            mIsWalletUpdating = false;
            List<WalletCard> cards = response.getWalletCards();
            if (cards.isEmpty()) {
                Log.d(TAG, "No wallet cards exist.");
                mCardViewDrawable = null;
                mSelectedCard = null;
                refreshState();
                return;
            }
            int selectedIndex = response.getSelectedIndex();
            if (selectedIndex >= cards.size()) {
                Log.w(TAG, "Error retrieving cards: Invalid selected card index.");
                mSelectedCard = null;
                mCardViewDrawable = null;
                return;
            }
            mSelectedCard = cards.get(selectedIndex);
            mCardViewDrawable = mSelectedCard.getCardImage().loadDrawable(mContext);
            refreshState();
        }
}
