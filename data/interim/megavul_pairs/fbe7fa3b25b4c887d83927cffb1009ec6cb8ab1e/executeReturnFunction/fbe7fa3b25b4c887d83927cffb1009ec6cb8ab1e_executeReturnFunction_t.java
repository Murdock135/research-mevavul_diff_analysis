class executeReturnFunction {
public TValue executeReturnFunction(TContext context, TMemory memory, LineLocation location, List<TValue> values,
			Map<String, TValue> named) throws EaterException, EaterExceptionLocated {
		// ::comment when __CORE__
		final String value = getenv(values.get(0).toString());
		if (value == null)
			return TValue.fromString("");

		return TValue.fromString(value);
		// ::done

		// ::uncomment when __CORE__
		// return TValue.fromString("");
		// ::done
	}
}
