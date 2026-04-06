class getVarArgs {
private static Object[] getVarArgs(InvocationOnMock invocationOnMockRender, int i)
    {
        Object[] parameters;
        if (invocationOnMockRender.getArguments().length > i) {
            Object argument = invocationOnMockRender.getArgument(i);
            if (argument instanceof String) {
                parameters = new Object[] { argument };
            } else {
                parameters = (Object[]) argument;
            }
        } else {
            parameters = new Object[] {};
        }
        return parameters;
    }
}
