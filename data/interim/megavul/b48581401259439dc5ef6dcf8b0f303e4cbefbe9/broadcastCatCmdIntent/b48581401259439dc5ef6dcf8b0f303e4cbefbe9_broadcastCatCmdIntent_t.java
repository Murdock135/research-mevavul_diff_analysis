class broadcastCatCmdIntent {
private void broadcastCatCmdIntent(CatCmdMessage cmdMsg) {
        Intent intent = new Intent(AppInterface.CAT_CMD_ACTION);
        intent.putExtra("STK CMD", cmdMsg);
        intent.putExtra("SLOT_ID", mSlotId);
        CatLog.d(this, "Sending CmdMsg: " + cmdMsg+ " on slotid:" + mSlotId);
        mContext.sendBroadcast(intent, AppInterface.STK_PERMISSION);
    }
}
