class append_1 {
@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		csq = csq == null ? "null" : csq;
		for (int i = start; i < end; i++) {
			char c = csq.charAt(i);
			switch (c) {
				case '&' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(AMP);

				}
				case '<' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(LT);

				}
				case '>' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(GT);
				}
				case '"' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(QUOT);
				}

			}
		}
		a.append(csq, start, end);

	}
}
