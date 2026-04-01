class convert {
protected Object convert(Object parent, Class type, Converter converter) {
        types.push(type);
        try {
            return converter.unmarshal(reader, this);
        } catch (final ConversionException conversionException) {
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } catch (AbstractSecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } finally {
            types.popSilently();
        }
    }
}
