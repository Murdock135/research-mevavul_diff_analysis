class processExtras {
private void processExtras(Bundle extras) {
        final String downloadUuid = extras.getString(ConversationsActivity.EXTRA_DOWNLOAD_UUID);
        final String text = extras.getString(Intent.EXTRA_TEXT);
        final String nick = extras.getString(ConversationsActivity.EXTRA_NICK);
        final boolean asQuote = extras.getBoolean(ConversationsActivity.EXTRA_AS_QUOTE);
        final boolean pm = extras.getBoolean(ConversationsActivity.EXTRA_IS_PRIVATE_MESSAGE, false);
        final List<Uri> uris = extractUris(extras);
        if (uris != null && uris.size() > 0) {
            final List<Uri> cleanedUris = cleanUris(new ArrayList<>(uris));
            mediaPreviewAdapter.addMediaPreviews(Attachment.of(getActivity(), cleanedUris));
            toggleInputMethod();
            return;
        }
        if (nick != null) {
            if (pm) {
                Jid jid = conversation.getJid();
                try {
                    Jid next = Jid.of(jid.getLocal(), jid.getDomain(), nick);
                    privateMessageWith(next);
                } catch (final IllegalArgumentException ignored) {
                    //do nothing
                }
            } else {
                final MucOptions mucOptions = conversation.getMucOptions();
                if (mucOptions.participating() || conversation.getNextCounterpart() != null) {
                    highlightInConference(nick);
                }
            }
        } else {
            if (text != null && asQuote) {
                quoteText(text);
            } else {
                appendText(text);
            }
        }
        final Message message = downloadUuid == null ? null : conversation.findMessageWithFileAndUuid(downloadUuid);
        if (message != null) {
            startDownloadable(message);
        }
    }
}
