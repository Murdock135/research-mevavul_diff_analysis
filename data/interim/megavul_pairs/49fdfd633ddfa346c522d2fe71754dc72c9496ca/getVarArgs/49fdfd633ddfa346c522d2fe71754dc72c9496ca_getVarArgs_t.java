class getVarArgs {
private static Object[] getVarArgs(InvocationOnMock invocationOnMockRender, int i)
    {
        Object[] parameters;
        Object[] arguments = invocationOnMockRender.getArguments();
        if (arguments.length > i) {
            parameters = Arrays.copyOfRange(arguments, i, arguments.length);
        } else {
            parameters = new Object[] {};
        }
        return parameters;
    }
}
