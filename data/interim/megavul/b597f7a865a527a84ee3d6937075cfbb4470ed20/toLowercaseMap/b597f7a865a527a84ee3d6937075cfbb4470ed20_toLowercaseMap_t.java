class toLowercaseMap {
private static CharSequenceMap toLowercaseMap(Iterator<? extends CharSequence> valuesIter,
                                                  int arraySizeHint) {
        final CharSequenceMap result = new CharSequenceMap(arraySizeHint);

        while (valuesIter.hasNext()) {
            final AsciiString lowerCased = AsciiString.of(valuesIter.next()).toLowerCase();
            try {
                int index = lowerCased.forEachByte(FIND_COMMA);
                if (index != -1) {
                    int start = 0;
                    do {
                        result.add(lowerCased.subSequence(start, index, false).trim(), EMPTY_STRING);
                        start = index + 1;
                    } while (start < lowerCased.length() &&
                             (index = lowerCased.forEachByte(start,
                                                             lowerCased.length() - start, FIND_COMMA)) != -1);
                    result.add(lowerCased.subSequence(start, lowerCased.length(), false).trim(), EMPTY_STRING);
                } else {
                    result.add(lowerCased.trim(), EMPTY_STRING);
                }
            } catch (Exception e) {
                // This is not expect to happen because FIND_COMMA never throws but must be caught
                // because of the ByteProcessor interface.
                throw new IllegalStateException(e);
            }
        }
        return result;
    }
}
