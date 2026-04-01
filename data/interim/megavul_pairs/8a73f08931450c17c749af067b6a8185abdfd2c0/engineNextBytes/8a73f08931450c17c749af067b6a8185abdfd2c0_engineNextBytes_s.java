class engineNextBytes {
@Override
        protected void engineNextBytes(byte[] bytes)
        {
            random.nextBytes(bytes);
        }
}
