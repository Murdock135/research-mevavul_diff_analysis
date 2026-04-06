class engineGenerateSeed {
@Override
        protected byte[] engineGenerateSeed(int numBytes)
        {
            return secureRandom.generateSeed(numBytes);
        }
}
