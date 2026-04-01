class reportJobCaches {
@Path("/report-job-caches")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@POST
	public void reportJobCaches(byte[] cacheInstanceBytes) {
		@SuppressWarnings("unchecked")
		Collection<CacheInstance> cacheInstances = (Collection<CacheInstance>) SerializationUtils
				.deserialize(cacheInstanceBytes);
		jobManager.reportJobCaches(getJobToken(), cacheInstances);
	}
}
