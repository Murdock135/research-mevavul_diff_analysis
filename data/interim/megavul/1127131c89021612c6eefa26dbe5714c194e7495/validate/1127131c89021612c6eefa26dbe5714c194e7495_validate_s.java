class validate {
private BigInteger validate(BigInteger y, DHParameters dhParams)
    {
        if (dhParams.getQ() != null)
        {
            if (BigInteger.ONE.equals(y.modPow(dhParams.getQ(), dhParams.getP())))
            {
                return y;
            }

            throw new IllegalArgumentException("Y value does not appear to be in correct group");
        }
        else
        {
            return y;         // we can't validate without Q.
        }
    }
}
