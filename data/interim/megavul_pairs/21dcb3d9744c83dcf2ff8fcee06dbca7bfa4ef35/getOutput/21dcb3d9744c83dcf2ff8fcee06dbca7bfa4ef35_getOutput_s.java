class getOutput {
private byte[] getOutput()
        throws BadPaddingException
    {
        try
        {
            byte[]  bytes = bOut.toByteArray();

            return cipher.processBlock(bytes, 0, bytes.length);
        }
        catch (final InvalidCipherTextException e)
        {
            throw new BadPaddingException("unable to decrypt block")
            {
                public synchronized Throwable getCause()
                {
                    return e;
                }
            };
        }
        finally
        {
            bOut.reset();
        }
    }
}
