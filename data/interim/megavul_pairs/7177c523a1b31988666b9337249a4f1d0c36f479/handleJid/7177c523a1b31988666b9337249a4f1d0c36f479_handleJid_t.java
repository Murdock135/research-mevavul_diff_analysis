class handleJid {
private boolean handleJid(Invite invite) {
		List<Contact> contacts = xmppConnectionService.findContacts(invite.getJid(), invite.account);
		if (invite.isAction(XmppUri.ACTION_JOIN)) {
			Conversation muc = xmppConnectionService.findFirstMuc(invite.getJid());
			if (muc != null) {
				switchToConversationDoNotAppend(muc, invite.getBody());
				return true;
			} else {
				showJoinConferenceDialog(invite.getJid().asBareJid().toString());
				return false;
			}
		} else if (contacts.size() == 0) {
			showCreateContactDialog(invite.getJid().toString(), invite);
			return false;
		} else if (contacts.size() == 1) {
			Contact contact = contacts.get(0);
			if (!invite.isSafeSource() && invite.hasFingerprints()) {
				displayVerificationWarningDialog(contact, invite);
			} else {
				if (invite.hasFingerprints()) {
					if (xmppConnectionService.verifyFingerprints(contact, invite.getFingerprints())) {
						Toast.makeText(this, R.string.verified_fingerprints, Toast.LENGTH_SHORT).show();
					}
				}
				if (invite.account != null) {
					xmppConnectionService.getShortcutService().report(contact);
				}
				switchToConversationDoNotAppend(contact, invite.getBody());
			}
			return true;
		} else {
			if (mMenuSearchView != null) {
				mMenuSearchView.expandActionView();
				mSearchEditText.setText("");
				mSearchEditText.append(invite.getJid().toString());
				filter(invite.getJid().toString());
			} else {
				mInitialSearchValue.push(invite.getJid().toString());
			}
			return true;
		}
	}
}
