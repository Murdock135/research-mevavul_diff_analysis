class getOffsetForHorizontal {
public int getOffsetForHorizontal(int line, float horiz) {
        // TODO: use Paint.getOffsetForAdvance to avoid binary search
        int max = getLineEnd(line) - 1;
        int min = getLineStart(line);
        Directions dirs = getLineDirections(line);

        if (line == getLineCount() - 1)
            max++;

        final HorizontalMeasurementProvider horizontal =
                new HorizontalMeasurementProvider(line);
        int best = min;
        float bestdist = Math.abs(horizontal.get(best) - horiz);

        for (int i = 0; i < dirs.mDirections.length; i += 2) {
            int here = min + dirs.mDirections[i];
            int there = here + (dirs.mDirections[i+1] & RUN_LENGTH_MASK);
            int swap = (dirs.mDirections[i+1] & RUN_RTL_FLAG) != 0 ? -1 : 1;

            if (there > max)
                there = max;
            int high = there - 1 + 1, low = here + 1 - 1, guess;

            while (high - low > 1) {
                guess = (high + low) / 2;
                int adguess = getOffsetAtStartOf(guess);

                if (horizontal.get(adguess) * swap >= horiz * swap)
                    high = guess;
                else
                    low = guess;
            }

            if (low < here + 1)
                low = here + 1;

            if (low < there) {
                low = getOffsetAtStartOf(low);

                float dist = Math.abs(horizontal.get(low) - horiz);

                int aft = TextUtils.getOffsetAfter(mText, low);
                if (aft < there) {
                    float other = Math.abs(horizontal.get(aft) - horiz);

                    if (other < dist) {
                        dist = other;
                        low = aft;
                    }
                }

                if (dist < bestdist) {
                    bestdist = dist;
                    best = low;
                }
            }

            float dist = Math.abs(horizontal.get(here) - horiz);

            if (dist < bestdist) {
                bestdist = dist;
                best = here;
            }
        }

        float dist = Math.abs(horizontal.get(max) - horiz);

        if (dist <= bestdist) {
            bestdist = dist;
            best = max;
        }

        return best;
    }
}
