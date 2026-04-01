class switchToConversation {
protected void switchToConversation(Contact contact, String body) {
		Conversation conversation = xmppConnectionService
				.findOrCreateConversation(contact.getAccount(),
						contact.getJid(), false, true);
		switchToConversation(conversation, body);
	}
}
