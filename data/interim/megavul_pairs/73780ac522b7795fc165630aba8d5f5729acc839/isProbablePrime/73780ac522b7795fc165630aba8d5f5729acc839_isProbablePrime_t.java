class isProbablePrime {
protected boolean isProbablePrime(BigInteger x, int iterations)
    {
        /*
         * Primes class for FIPS 186-4 C.3 primality checking
         */
        return !Primes.hasAnySmallFactors(x) && Primes.isMRProbablePrime(x, param.getRandom(), iterations);
    }
}
