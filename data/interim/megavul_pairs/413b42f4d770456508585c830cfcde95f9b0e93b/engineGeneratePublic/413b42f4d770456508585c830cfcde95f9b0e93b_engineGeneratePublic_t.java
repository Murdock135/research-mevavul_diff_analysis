class engineGeneratePublic {
protected PublicKey engineGeneratePublic(
        KeySpec keySpec)
        throws InvalidKeySpecException
    {
        if (keySpec instanceof DHPublicKeySpec)
        {
            try
            {
                return new BCDHPublicKey((DHPublicKeySpec)keySpec);
            }
            catch (IllegalArgumentException e)
            {
                throw new InvalidKeySpecException(e.getMessage(), e);
            }
        }

        return super.engineGeneratePublic(keySpec);
    }
}
