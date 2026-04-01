class fromFile {
public static SFile fromFile(File internal) {
		if (internal == null) {
			return null;
		}
		return new SFile(internal);
	}
}
