class add {
@Override
    public void add(int index, BaseObject element)
    {
        // Check if the index is valid
        rangeCheckForAdd(index);

        // Move right values
        if (index < this.size) {
            for (int i = this.size - 1; i >= index; --i) {
                put(i + 1, get(i));
            }
        }

        // Insert new value
        put(index, element);
    }
}
