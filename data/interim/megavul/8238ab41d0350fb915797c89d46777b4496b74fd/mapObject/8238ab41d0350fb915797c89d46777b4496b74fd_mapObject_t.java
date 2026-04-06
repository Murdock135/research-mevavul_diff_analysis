class mapObject {
protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            // will point to FIELD_NAME at this point, guaranteed
            // 19-Jul-2021, tatu: Was incorrectly using "getText()" before 2.13, fixed for 2.13.0
            String key1 = p.currentName();
            p.nextToken();
            Object value1 = deserialize(p, ctxt);

            String key = p.nextFieldName();
            if (key == null) { // single entry; but we want modifiable
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(2);
                result.put(key1, value1);
                return result;
            }
            // And then the general case; default map size is 16
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put(key1, value1);
            do {
                p.nextToken();
                final Object newValue = deserialize(p, ctxt);
                final Object oldValue = result.put(key, newValue);
                if (oldValue != null) {
                    return _mapObjectWithDups(p, ctxt, result, key, oldValue, newValue,
                            p.nextFieldName());
                }
            } while ((key = p.nextFieldName()) != null);
            return result;
        }
}
