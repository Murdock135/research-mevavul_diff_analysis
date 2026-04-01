class executeReturnFunction_1 {
public TValue executeReturnFunction(TContext context, TMemory memory, LineLocation location, List<TValue> values,
			Map<String, TValue> named) throws EaterException, EaterExceptionLocated {
		// ::comment when __CORE__
		if (OptionFlags.ALLOW_INCLUDE == false)
			// ::done
			return TValue.fromBoolean(false);
		// ::comment when __CORE__

		final String path = values.get(0).toString();
		return TValue.fromBoolean(fileExists(path));
		// ::done
	}
}
