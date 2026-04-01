class init {
public void init(KeyGenerationParameters param)
    {
        this.param = (RSAKeyGenerationParameters)param;
        this.iterations = getNumberOfIterations(this.param.getStrength(), this.param.getCertainty());
    }
}
