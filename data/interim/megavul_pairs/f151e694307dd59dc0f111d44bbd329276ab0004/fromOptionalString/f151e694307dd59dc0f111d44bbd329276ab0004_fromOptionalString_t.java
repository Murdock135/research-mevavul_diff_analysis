class fromOptionalString {
public static SortDirection fromOptionalString(Optional<String> direction) {
        if ("DESC".equalsIgnoreCase(direction.orElse(null))) {
            return DESC;
        }
        return ASC;
    }
}
