class handleAttachmentUrisFromIntent {
private long handleAttachmentUrisFromIntent(List<Uri> uris) {
        ArrayList<Attachment> attachments = Lists.newArrayList();
        for (Uri uri : uris) {
            try {
                if (uri != null) {
                    if ("file".equals(uri.getScheme())) {
                        // We must not allow files from /data, even from our process.
                        final File f = new File(uri.getPath());
                        final String filePath = f.getCanonicalPath();
                        if (filePath.startsWith(DATA_DIRECTORY_ROOT)) {
                          showErrorToast(getString(R.string.attachment_permission_denied));
                          Analytics.getInstance().sendEvent(ANALYTICS_CATEGORY_ERRORS,
                                  "send_intent_attachment", "data_dir", 0);
                          continue;
                        }
                    }
                    if (!handleSpecialAttachmentUri(uri)) {
                        final Attachment a = mAttachmentsView.generateLocalAttachment(uri);
                        attachments.add(a);

                        Analytics.getInstance().sendEvent("send_intent_attachment",
                                Utils.normalizeMimeType(a.getContentType()), null, a.size);
                    }
                }
            } catch (AttachmentFailureException e) {
                LogUtils.e(LOG_TAG, e, "Error adding attachment");
                showAttachmentTooBigToast(e.getErrorRes());
            } catch (IOException | SecurityException e) {
                LogUtils.e(LOG_TAG, e, "Error adding attachment");
                showErrorToast(getString(R.string.attachment_permission_denied));
            }
        }
        return addAttachments(attachments);
    }
}
