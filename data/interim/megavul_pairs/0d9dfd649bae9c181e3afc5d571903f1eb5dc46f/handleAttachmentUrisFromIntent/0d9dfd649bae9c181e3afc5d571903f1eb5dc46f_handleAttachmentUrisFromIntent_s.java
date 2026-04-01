class handleAttachmentUrisFromIntent {
private long handleAttachmentUrisFromIntent(List<Uri> uris) {
        ArrayList<Attachment> attachments = Lists.newArrayList();
        for (Uri uri : uris) {
            try {
                if (uri != null) {
                    if ("file".equals(uri.getScheme())) {
                        final File f = new File(uri.getPath());
                        // We should not be attaching any files from the data directory UNLESS
                        // the data directory is part of the calling process.
                        final String filePath = f.getCanonicalPath();
                        if (filePath.startsWith(DATA_DIRECTORY_ROOT)) {
                            final String callingPackage = getCallingPackage();
                            if (callingPackage == null) {
                                showErrorToast(getString(R.string.attachment_permission_denied));
                                continue;
                            }

                            // So it looks like the data directory are usually /data/data, but
                            // DATA_DIRECTORY_ROOT is only /data.. so let's check for both
                            final String pathWithoutRoot;
                            // We add 1 to the length for the additional / before the package name.
                            if (filePath.startsWith(ALTERNATE_DATA_DIRECTORY_ROOT)) {
                                pathWithoutRoot = filePath.substring(
                                        ALTERNATE_DATA_DIRECTORY_ROOT.length() + 1);
                            } else {
                                pathWithoutRoot = filePath.substring(
                                        DATA_DIRECTORY_ROOT.length() + 1);
                            }

                            // If we are trying to access a data package that's not part of the
                            // calling package, show error toast and ignore this attachment.
                            if (!pathWithoutRoot.startsWith(callingPackage)) {
                                showErrorToast(getString(R.string.attachment_permission_denied));
                                continue;
                            }
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
