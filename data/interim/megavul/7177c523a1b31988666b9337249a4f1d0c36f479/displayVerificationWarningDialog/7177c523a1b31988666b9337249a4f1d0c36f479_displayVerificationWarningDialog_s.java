class displayVerificationWarningDialog {
private void displayVerificationWarningDialog(final Contact contact, final Invite invite) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.verify_omemo_keys);
		View view = getLayoutInflater().inflate(R.layout.dialog_verify_fingerprints, null);
		final CheckBox isTrustedSource = view.findViewById(R.id.trusted_source);
		TextView warning = view.findViewById(R.id.warning);
		warning.setText(JidDialog.style(this, R.string.verifying_omemo_keys_trusted_source, contact.getJid().asBareJid().toEscapedString(), contact.getDisplayName()));
		builder.setView(view);
		builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
			if (isTrustedSource.isChecked() && invite.hasFingerprints()) {
				xmppConnectionService.verifyFingerprints(contact, invite.getFingerprints());
			}
			switchToConversation(contact, invite.getBody());
		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> StartConversationActivity.this.finish());
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(dialog1 -> StartConversationActivity.this.finish());
		dialog.show();
	}
}
