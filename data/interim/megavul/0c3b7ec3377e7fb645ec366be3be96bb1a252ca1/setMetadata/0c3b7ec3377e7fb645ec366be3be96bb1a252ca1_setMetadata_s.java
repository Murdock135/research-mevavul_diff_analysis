class setMetadata {
@Override
        public void setMetadata(MediaMetadata metadata, long duration, String metadataDescription)
                throws RemoteException {
            synchronized (mLock) {
                MediaMetadata temp = metadata == null ? null : new MediaMetadata.Builder(metadata)
                        .build();
                // This is to guarantee that the underlying bundle is unparceled
                // before we set it to prevent concurrent reads from throwing an
                // exception
                if (temp != null) {
                    temp.size();
                }
                mMetadata = temp;
                mDuration = duration;
                mMetadataDescription = metadataDescription;
            }
            mHandler.post(MessageHandler.MSG_UPDATE_METADATA);
        }
}
