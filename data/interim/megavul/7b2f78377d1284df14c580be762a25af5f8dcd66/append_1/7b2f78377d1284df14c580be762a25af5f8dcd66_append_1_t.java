class append_1 {
@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		csq = csq == null ? "null" : csq;
		for (int i = start; i < end; i++) {
			char c = csq.charAt(i);
			switch (c) {
				case '"' -> { // 34
					a.append(csq, start, i);
					start = i + 1;
					a.append(QUOT);
				}
				case '&' -> { // 38
					a.append(csq, start, i);
					start = i + 1;
					a.append(AMP);

				}
				case '\'' -> { // 39
					a.append(csq, start, i);
					start = i + 1;
					a.append(APOS);
				}
				case '<' -> { // 60
					a.append(csq, start, i);
					start = i + 1;
					a.append(LT);
				}
				case '=' -> { // 61
					a.append(csq, start, i);
					start = i + 1;
					a.append(EQUAL);
				}
				case '>' -> { // 62
					a.append(csq, start, i);
					start = i + 1;
					a.append(GT);
				}
				case '`' -> { // 96
					a.append(csq, start, i);
					start = i + 1;
					a.append(BACK_TICK);
				}
			}
		}
		a.append(csq, start, end);

	}
}
