class engineGeneratePublic {
protected PublicKey engineGeneratePublic(
        KeySpec keySpec)
        throws InvalidKeySpecException
    {
        if (keySpec instanceof DHPublicKeySpec)
        {
            return new BCDHPublicKey((DHPublicKeySpec)keySpec);
        }

        return super.engineGeneratePublic(keySpec);
    }
}
