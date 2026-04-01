class initialize {
public void initialize(
        int strength,
        SecureRandom random)
    {
        if (strength < 512 || strength > 4096 || ((strength < 1024) && strength % 64 != 0) || (strength >= 1024 && strength % 1024 != 0))
        {
            throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
        }

        this.strength = strength;
        this.random = random;
    }
}
