class allocateJobCaches {
@Path("/allocate-job-caches")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
    @POST
    public byte[] allocateJobCaches(String cacheAllocationRequestString) {
		CacheAllocationRequest cacheAllocationRequest = CacheAllocationRequest.fromString(cacheAllocationRequestString);
		return SerializationUtils.serialize((Serializable) jobManager.allocateJobCaches(
				getJobToken(), cacheAllocationRequest.getCurrentTime(), cacheAllocationRequest.getInstances()));
    }
}
