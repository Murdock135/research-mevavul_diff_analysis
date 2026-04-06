class toString {
@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String key : mFields.keySet()) {
            // Don't display password in toString().
            String value = PASSWORD_KEY.equals(key) ? "<removed>" : mFields.get(key);
            sb.append(key).append(" ").append(value).append("\n");
        }
        return sb.toString();
    }
}
