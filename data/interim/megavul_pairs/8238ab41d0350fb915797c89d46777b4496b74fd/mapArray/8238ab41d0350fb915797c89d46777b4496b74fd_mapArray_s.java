class mapArray {
protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException
        {
            Object value = deserialize(p, ctxt);
            if (p.nextToken()  == JsonToken.END_ARRAY) {
                ArrayList<Object> l = new ArrayList<Object>(2);
                l.add(value);
                return l;
            }
            Object value2 = deserialize(p, ctxt);
            if (p.nextToken()  == JsonToken.END_ARRAY) {
                ArrayList<Object> l = new ArrayList<Object>(2);
                l.add(value);
                l.add(value2);
                return l;
            }
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0;
            values[ptr++] = value;
            values[ptr++] = value2;
            int totalSize = ptr;
            do {
                value = deserialize(p, ctxt);
                ++totalSize;
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                values[ptr++] = value;
            } while (p.nextToken() != JsonToken.END_ARRAY);
            // let's create full array then
            ArrayList<Object> result = new ArrayList<Object>(totalSize);
            buffer.completeAndClearBuffer(values, ptr, result);
            ctxt.returnObjectBuffer(buffer);
            return result;
        }
}
