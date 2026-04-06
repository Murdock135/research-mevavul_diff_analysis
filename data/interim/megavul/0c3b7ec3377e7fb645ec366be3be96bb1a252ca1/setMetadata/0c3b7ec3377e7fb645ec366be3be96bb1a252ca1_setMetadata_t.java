class setMetadata {
@Override
        public void setMetadata(MediaMetadata metadata, long duration, String metadataDescription)
                throws RemoteException {
            synchronized (mLock) {
                mDuration = duration;
                mMetadataDescription = metadataDescription;
                mMetadata = sanitizeMediaMetadata(metadata);
            }
            mHandler.post(MessageHandler.MSG_UPDATE_METADATA);
        }
}
