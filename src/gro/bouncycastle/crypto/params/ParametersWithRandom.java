package gro.bouncycastle.crypto.params;

import gro.bouncycastle.crypto.CipherParameters;

import ewe.security.SecureRandom;

public class ParametersWithRandom
    implements CipherParameters
{
    private SecureRandom        random;
    private CipherParameters    parameters;

    public ParametersWithRandom(
        CipherParameters    parameters,
        SecureRandom        random)
    {
        this.random = random;
        this.parameters = parameters;
    }

    public ParametersWithRandom(
        CipherParameters    parameters)
    {
        this(parameters, new SecureRandom());
    }

    public SecureRandom getRandom()
    {
        return random;
    }

    public CipherParameters getParameters()
    {
        return parameters;
    }
}
