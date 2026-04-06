class append {
@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		switch (c) {
			case '&' -> {
				a.append(AMP);
			}
			case '<' -> {
				a.append(LT);
			}
			case '>' -> {
				a.append(GT);
			}
			case '"' -> {
				a.append(QUOT);
			}
			default -> {
				a.append(c);
			}
		}
	}
}
