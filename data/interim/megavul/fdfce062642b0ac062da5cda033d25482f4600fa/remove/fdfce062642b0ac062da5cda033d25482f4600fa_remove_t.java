class remove {
@Override
    public BaseObject remove(int index)
    {
        rangeCheck(index);

        BaseObject previous = this.map.remove(index);

        // Shifts right values to the left
        if (index < this.size - 1) {
            for (int i = index; i < this.size - 1; ++i) {
                put(i, get(i + 1));
            }
        }

        // The list is one element shorter
        --this.size;

        return previous;
    }
}
