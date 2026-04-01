class init {
public void init(
        boolean           forEncryption,
        CipherParameters  params)
    {
        if (params instanceof KeyParameter)
        {
            WorkingKey = generateWorkingKey(((KeyParameter)params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            if (forEncryption)
            {
                s = Arrays.clone(S);
            }
            else
            {
                s = Arrays.clone(Si);
            }
            return;
        }

        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }
}
