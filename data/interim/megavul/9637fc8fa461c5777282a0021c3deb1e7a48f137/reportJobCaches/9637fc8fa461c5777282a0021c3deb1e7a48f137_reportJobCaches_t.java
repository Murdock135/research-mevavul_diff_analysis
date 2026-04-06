class reportJobCaches {
@Path("/report-job-caches")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@POST
	public void reportJobCaches(String cacheInstancesString) {
		Collection<CacheInstance> cacheInstances = new ArrayList<>();
		for (String field: Splitter.on(';').omitEmptyStrings().split(cacheInstancesString))
			cacheInstances.add(CacheInstance.fromString(field));
		jobManager.reportJobCaches(getJobToken(), cacheInstances);
	}
}
