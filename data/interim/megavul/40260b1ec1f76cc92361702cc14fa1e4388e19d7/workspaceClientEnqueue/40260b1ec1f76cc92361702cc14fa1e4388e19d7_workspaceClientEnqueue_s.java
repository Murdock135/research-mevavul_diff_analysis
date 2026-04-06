class workspaceClientEnqueue {
@POST
    @Path("/WorkSpaceClientEnqueue.action")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response workspaceClientEnqueue(@FormParam(WorkSpaceAdapter.CLIENT_NAME) String clientName,
            @FormParam(WorkSpaceAdapter.WORK_BUNDLE_OBJ) String workBundleString) {
        logger.debug("TPWorker incoming execute! check prio={}", Thread.currentThread().getPriority());
        // TODO Doesn't look like anything is actually calling this, should we remove this?
        final boolean success;
        try {
            // Look up the place reference
            final String nsName = KeyManipulator.getServiceLocation(clientName);
            final IPickUpSpace place = (IPickUpSpace) Namespace.lookup(nsName);
            if (place == null) {
                throw new IllegalArgumentException("No client place found using name " + clientName);
            }

            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(workBundleString.getBytes("8859_1")));
            WorkBundle paths = (WorkBundle) ois.readObject();
            success = place.enque(paths);
        } catch (Exception e) {
            logger.warn("WorkSpaceClientEnqueWorker exception", e);
            return Response.serverError().entity("WorkSpaceClientEnqueWorker exception:\n" + e.getMessage()).build();
        }

        if (success) {
            // old success from WorkSpaceClientEnqueWorker
            // return WORKER_SUCCESS;
            return Response.ok().entity("Successful add to the PickUpPlaceClient queue").build();
        } else {
            // old failure from WorkSpaceClientEnqueWorker
            // return new WorkerStatus(WorkerStatus.FAILURE, "WorkSpaceClientEnqueWorker failed, queue full");
            return Response.serverError().entity("WorkSpaceClientEnqueWorker failed, queue full").build();
        }
    }
}
