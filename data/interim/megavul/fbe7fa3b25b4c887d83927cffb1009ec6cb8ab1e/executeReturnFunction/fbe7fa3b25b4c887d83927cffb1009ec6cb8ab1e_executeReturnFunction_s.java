class executeReturnFunction {
public TValue executeReturnFunction(TContext context, TMemory memory, LineLocation location, List<TValue> values,
			Map<String, TValue> named) throws EaterException, EaterExceptionLocated {
		// ::comment when __CORE__
		if (OptionFlags.ALLOW_INCLUDE == false)
			// ::done
			return TValue.fromString("");
		// ::comment when __CORE__

		final String name = values.get(0).toString();
		final String value = getenv(name);
		if (value == null)
			return TValue.fromString("");

		return TValue.fromString(value);
		// ::done
	}
}
