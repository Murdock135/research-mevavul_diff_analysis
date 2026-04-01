class playSound {
private void playSound(int soundId) {
        if (soundId == 0) return;
        final ContentResolver cr = mContext.getContentResolver();
        if (Settings.System.getInt(cr, Settings.System.LOCKSCREEN_SOUNDS_ENABLED, 1) == 1) {

            mLockSounds.stop(mLockSoundStreamId);
            // Init mAudioManager
            if (mAudioManager == null) {
                mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                if (mAudioManager == null) return;
                mUiSoundsStreamType = mAudioManager.getUiSoundsStreamType();
            }

            mUiBgExecutor.execute(() -> {
                // If the stream is muted, don't play the sound
                if (mAudioManager.isStreamMute(mUiSoundsStreamType)) return;

                int id = mLockSounds.play(soundId,
                        mLockSoundVolume, mLockSoundVolume, 1/*priority*/, 0/*loop*/, 1.0f/*rate*/);
                synchronized (this) {
                    mLockSoundStreamId = id;
                }
            });

        }
    }
}
