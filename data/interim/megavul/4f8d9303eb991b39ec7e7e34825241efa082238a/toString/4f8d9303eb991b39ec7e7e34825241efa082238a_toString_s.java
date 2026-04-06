class toString {
@Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("ClickHouseNode [uri=").append(baseUri)
                .append(config.getDatabase());
        if (!cluster.isEmpty()) {
            builder.append(", cluster=").append(cluster).append("(s").append(shardNum).append(",w").append(shardWeight)
                    .append(",r").append(replicaNum).append(')');
        }

        StringBuilder optsBuilder = new StringBuilder();
        for (Entry<String, String> option : options.entrySet()) {
            String key = option.getKey();
            if (!ClickHouseClientOption.DATABASE.getKey().equals(key)
                    && !ClickHouseClientOption.SSL.getKey().equals(key)) {
                optsBuilder.append(key).append('=').append(option.getValue()).append(",");
            }
        }
        if (optsBuilder.length() > 0) {
            optsBuilder.setLength(optsBuilder.length() - 1);
            builder.append(", options={").append(optsBuilder).append('}');
        }
        if (!tags.isEmpty()) {
            builder.append(", tags=").append(tags);
        }
        return builder.append("]@").append(hashCode()).toString();
    }
}
