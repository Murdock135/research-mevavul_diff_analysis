class _decodeNonStringName {
protected final void _decodeNonStringName(int ch) throws IOException
    {
        final int type = ((ch >> 5) & 0x7);
        String name;
        if (type == CBORConstants.MAJOR_TYPE_INT_POS) {
            name = _numberToName(ch, false);
        } else if (type == CBORConstants.MAJOR_TYPE_INT_NEG) {
            name = _numberToName(ch, true);
        } else if (type == CBORConstants.MAJOR_TYPE_BYTES) {
            // 08-Sep-2014, tatu: As per [Issue#5], there are codecs
            //   (f.ex. Perl module "CBOR::XS") that use Binary data...
            final int blen = _decodeExplicitLength(ch & 0x1F);
            byte[] b = _finishBytes(blen);
            // TODO: Optimize, if this becomes commonly used & bottleneck; we have
            //  more optimized UTF-8 codecs available.
            name = new String(b, UTF8);
        } else {
            if ((ch & 0xFF) == CBORConstants.INT_BREAK) {
                _reportUnexpectedBreak();
            }
            throw _constructError("Unsupported major type ("+type+") for CBOR Objects, not (yet?) supported, only Strings");
        }
        _parsingContext.setCurrentName(name);
    }
}
