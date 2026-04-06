class engineSetSeed {
@Override
        protected void engineSetSeed(byte[] bytes)
        {
            random.setSeed(bytes);
        }
}
