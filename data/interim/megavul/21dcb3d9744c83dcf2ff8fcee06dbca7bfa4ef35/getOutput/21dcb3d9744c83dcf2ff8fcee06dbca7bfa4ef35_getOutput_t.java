class getOutput {
private byte[] getOutput()
        throws BadPaddingException
    {
        try
        {
            byte[]  bytes = bOut.toByteArray();

            return cipher.processBlock(bytes, 0, bytes.length);
        }
        catch (InvalidCipherTextException e)
        {
            throw new BadBlockException("unable to decrypt block", e);
        }
        finally
        {
            bOut.reset();
        }
    }
}
