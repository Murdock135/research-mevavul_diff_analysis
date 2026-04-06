class doFinal {
public int doFinal(byte[] out, int outOff)
                throws DataLengthException, IllegalStateException
            {
                try
                {
                    return ccm.doFinal(out, 0);
                }
                catch (InvalidCipherTextException e)
                {
                    throw new IllegalStateException("exception on doFinal(): " + e.toString());
                }
            }
}
