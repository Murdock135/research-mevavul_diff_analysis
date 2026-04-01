class calculateAgreement {
public BigInteger calculateAgreement(
        DHPublicKeyParameters   pub,
        BigInteger              message)
    {
        if (!pub.getParameters().equals(dhParams))
        {
            throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters.");
        }

        BigInteger p = dhParams.getP();

        return message.modPow(key.getX(), p).multiply(pub.getY().modPow(privateValue, p)).mod(p);
    }
}
