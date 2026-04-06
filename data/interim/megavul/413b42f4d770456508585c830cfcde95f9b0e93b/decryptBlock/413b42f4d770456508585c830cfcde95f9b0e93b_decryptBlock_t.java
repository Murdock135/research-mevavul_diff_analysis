class decryptBlock {
private void decryptBlock(int[][] KW)
    {
        int t0 = this.C0 ^ KW[ROUNDS][0];
        int t1 = this.C1 ^ KW[ROUNDS][1];
        int t2 = this.C2 ^ KW[ROUNDS][2];

        int r = ROUNDS - 1, r0, r1, r2, r3 = this.C3 ^ KW[ROUNDS][3];
        while (r > 1)
        {
            r0 = Tinv0[t0&255] ^ shift(Tinv0[(r3>>8)&255], 24) ^ shift(Tinv0[(t2>>16)&255], 16) ^ shift(Tinv0[(t1>>24)&255], 8) ^ KW[r][0];
            r1 = Tinv0[t1&255] ^ shift(Tinv0[(t0>>8)&255], 24) ^ shift(Tinv0[(r3>>16)&255], 16) ^ shift(Tinv0[(t2>>24)&255], 8) ^ KW[r][1];
            r2 = Tinv0[t2&255] ^ shift(Tinv0[(t1>>8)&255], 24) ^ shift(Tinv0[(t0>>16)&255], 16) ^ shift(Tinv0[(r3>>24)&255], 8) ^ KW[r][2];
            r3 = Tinv0[r3&255] ^ shift(Tinv0[(t2>>8)&255], 24) ^ shift(Tinv0[(t1>>16)&255], 16) ^ shift(Tinv0[(t0>>24)&255], 8) ^ KW[r--][3];
            t0 = Tinv0[r0&255] ^ shift(Tinv0[(r3>>8)&255], 24) ^ shift(Tinv0[(r2>>16)&255], 16) ^ shift(Tinv0[(r1>>24)&255], 8) ^ KW[r][0];
            t1 = Tinv0[r1&255] ^ shift(Tinv0[(r0>>8)&255], 24) ^ shift(Tinv0[(r3>>16)&255], 16) ^ shift(Tinv0[(r2>>24)&255], 8) ^ KW[r][1];
            t2 = Tinv0[r2&255] ^ shift(Tinv0[(r1>>8)&255], 24) ^ shift(Tinv0[(r0>>16)&255], 16) ^ shift(Tinv0[(r3>>24)&255], 8) ^ KW[r][2];
            r3 = Tinv0[r3&255] ^ shift(Tinv0[(r2>>8)&255], 24) ^ shift(Tinv0[(r1>>16)&255], 16) ^ shift(Tinv0[(r0>>24)&255], 8) ^ KW[r--][3];
        }

        r0 = Tinv0[t0&255] ^ shift(Tinv0[(r3>>8)&255], 24) ^ shift(Tinv0[(t2>>16)&255], 16) ^ shift(Tinv0[(t1>>24)&255], 8) ^ KW[r][0];
        r1 = Tinv0[t1&255] ^ shift(Tinv0[(t0>>8)&255], 24) ^ shift(Tinv0[(r3>>16)&255], 16) ^ shift(Tinv0[(t2>>24)&255], 8) ^ KW[r][1];
        r2 = Tinv0[t2&255] ^ shift(Tinv0[(t1>>8)&255], 24) ^ shift(Tinv0[(t0>>16)&255], 16) ^ shift(Tinv0[(r3>>24)&255], 8) ^ KW[r][2];
        r3 = Tinv0[r3&255] ^ shift(Tinv0[(t2>>8)&255], 24) ^ shift(Tinv0[(t1>>16)&255], 16) ^ shift(Tinv0[(t0>>24)&255], 8) ^ KW[r][3];
        
        // the final round's table is a simple function of Si so we don't use a whole other four tables for it

        this.C0 = (Si[r0&255]&255) ^ ((s[(r3>>8)&255]&255)<<8) ^ ((s[(r2>>16)&255]&255)<<16) ^ (Si[(r1>>24)&255]<<24) ^ KW[0][0];
        this.C1 = (s[r1&255]&255) ^ ((s[(r0>>8)&255]&255)<<8) ^ ((Si[(r3>>16)&255]&255)<<16) ^ (s[(r2>>24)&255]<<24) ^ KW[0][1];
        this.C2 = (s[r2&255]&255) ^ ((Si[(r1>>8)&255]&255)<<8) ^ ((Si[(r0>>16)&255]&255)<<16) ^ (s[(r3>>24)&255]<<24) ^ KW[0][2];
        this.C3 = (Si[r3&255]&255) ^ ((s[(r2>>8)&255]&255)<<8) ^ ((s[(r1>>16)&255]&255)<<16) ^ (s[(r0>>24)&255]<<24) ^ KW[0][3];
    }
}
