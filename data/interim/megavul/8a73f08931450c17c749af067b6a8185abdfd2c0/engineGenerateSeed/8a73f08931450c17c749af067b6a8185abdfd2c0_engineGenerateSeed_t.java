class engineGenerateSeed {
protected byte[] engineGenerateSeed(int numBytes)
        {
            return secureRandom.generateSeed(numBytes);
        }
}
