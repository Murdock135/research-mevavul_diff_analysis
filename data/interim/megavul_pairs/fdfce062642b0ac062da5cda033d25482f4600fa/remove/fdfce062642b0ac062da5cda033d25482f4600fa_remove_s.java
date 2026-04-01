class remove {
@Override
    public BaseObject remove(int index)
    {
        rangeCheck(index);

        return this.map.remove(index);
    }
}
