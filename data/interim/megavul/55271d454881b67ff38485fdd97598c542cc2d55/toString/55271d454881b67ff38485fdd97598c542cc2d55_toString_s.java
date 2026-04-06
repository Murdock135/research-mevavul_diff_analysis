class toString {
@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String key : mFields.keySet()) {
            sb.append(key).append(" ").append(mFields.get(key)).append("\n");
        }
        return sb.toString();
    }
}
