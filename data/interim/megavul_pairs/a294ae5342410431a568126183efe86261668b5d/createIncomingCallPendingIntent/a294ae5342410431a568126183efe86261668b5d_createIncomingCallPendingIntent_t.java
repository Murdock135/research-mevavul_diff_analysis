class createIncomingCallPendingIntent {
static PendingIntent createIncomingCallPendingIntent(
            Context context, String sipProfileName) {
        Intent intent = new Intent(context, SipBroadcastReceiver.class);
        intent.setAction(SipManager.ACTION_SIP_INCOMING_CALL);
        intent.putExtra(EXTRA_PHONE_ACCOUNT, SipUtil.createAccountHandle(context, sipProfileName));
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
