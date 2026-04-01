class engineUpdateAAD {
protected void engineUpdateAAD(byte[] input, int inputOffset, int inputLen) {
            if (aad == null) {
                aad = Arrays.copyOfRange(input, inputOffset, inputLen);
            } else {
                int newSize = aad.length + inputLen;
                byte[] newaad = new byte[newSize];
                System.arraycopy(aad, 0, newaad, 0, aad.length);
                System.arraycopy(input, inputOffset, newaad, aad.length, inputLen);
            }
        }
}
