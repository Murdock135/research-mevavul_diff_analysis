class switchToConversation {
protected void switchToConversation(Contact contact) {
		Conversation conversation = xmppConnectionService.findOrCreateConversation(contact.getAccount(), contact.getJid(), false, true);
		switchToConversation(conversation);
	}
}
