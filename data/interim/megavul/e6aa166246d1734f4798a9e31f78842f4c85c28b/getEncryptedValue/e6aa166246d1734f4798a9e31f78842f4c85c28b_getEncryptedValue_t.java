class getEncryptedValue {
public String getEncryptedValue() {
        try {
            synchronized (this) {
                if (iv == null) { //if we were created from plain text or other reason without iv
                    iv = KEY.newIv();
                }
            }
            Cipher cipher = KEY.encrypt(iv);
            byte[] encrypted = cipher.doFinal(this.value.getBytes(UTF_8));
            byte[] payload = new byte[1 + 8 + iv.length + encrypted.length];
            int pos = 0;
            // For PAYLOAD_V1 we use this byte shifting model, V2 probably will need DataOutput
            payload[pos++] = PAYLOAD_V1;
            payload[pos++] = (byte)(iv.length >> 24);
            payload[pos++] = (byte)(iv.length >> 16);
            payload[pos++] = (byte)(iv.length >> 8);
            payload[pos++] = (byte)(iv.length);
            payload[pos++] = (byte)(encrypted.length >> 24);
            payload[pos++] = (byte)(encrypted.length >> 16);
            payload[pos++] = (byte)(encrypted.length >> 8);
            payload[pos++] = (byte)(encrypted.length);
            System.arraycopy(iv, 0, payload, pos, iv.length);
            pos+=iv.length;
            System.arraycopy(encrypted, 0, payload, pos, encrypted.length);
            return "{"+new String(Base64.encode(payload))+"}";
        } catch (GeneralSecurityException e) {
            throw new Error(e); // impossible
        }
    }
}
