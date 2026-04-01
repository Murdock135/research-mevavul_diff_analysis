class toDecimal {
public static String toDecimal(long seconds, int nanoseconds)
    {
        StringBuilder sb = new StringBuilder(20)
            .append(seconds)
            .append('.');
        // 14-Mar-2016, tatu: Although we do not yet (with 2.7) trim trailing zeroes,
        //   for general case,
        if (nanoseconds == 0L) {
            // !!! TODO: 14-Mar-2016, tatu: as per [datatype-jsr310], should trim
            //     trailing zeroes
            if (seconds == 0L) {
                return "0.0";
            }

//            sb.append('0');
            sb.append("000000000");
        } else {
            StringBuilder nanoSB = new StringBuilder(9);
            nanoSB.append(nanoseconds);
            // May need to both prepend leading nanos (if value less than 0.1)
            final int nanosLen = nanoSB.length();
            int prepZeroes = 9 - nanosLen;
            while (prepZeroes > 0) {
                --prepZeroes;
                sb.append('0');
            }

            // !!! TODO: 14-Mar-2016, tatu: as per [datatype-jsr310], should trim
            //     trailing zeroes
            /*
            // AND possibly trim trailing ones
            int i = nanosLen;
            while ((i > 1) && nanoSB.charAt(i-1) == '0') {
                --i;
            }
            if (i < nanosLen) {
                nanoSB.setLength(i);
            }
            */
            sb.append(nanoSB);
        }
        return sb.toString();
    }
}
