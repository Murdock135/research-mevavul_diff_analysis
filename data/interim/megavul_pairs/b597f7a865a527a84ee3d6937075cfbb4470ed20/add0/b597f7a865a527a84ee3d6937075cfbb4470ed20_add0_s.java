class add0 {
private void add0(int h, int i, AsciiString name, String value) {
        // Update the hash table.
        entries[i] = new HeaderEntry(h, name, value, entries[i]);
        ++size;
    }
}
