class attachToPost {
private Response attachToPost(Message mess, String filename, InputStream file,  HttpServletRequest request) {
		Identity identity = getIdentity(request);
		if(identity == null) {
			return Response.serverError().status(Status.UNAUTHORIZED).build();
		} else if (!identity.equalsByPersistableKey(mess.getCreator())) {
			if(mess.getModifier() == null || !identity.equalsByPersistableKey(mess.getModifier())) {
				return Response.serverError().status(Status.UNAUTHORIZED).build();
			}
		}

		VFSContainer container = fom.getMessageContainer(mess.getForum().getKey(), mess.getKey());
		VFSItem item = container.resolve(filename);
		VFSLeaf attachment = null;
		if(item == null) {
			attachment = container.createChildLeaf(filename);
		} else {
			filename = VFSManager.rename(container, filename);
			if(filename == null) {
				return Response.serverError().status(Status.NOT_ACCEPTABLE).build();
			}
			attachment = container.createChildLeaf(filename);
		}
		

		try(OutputStream out = attachment.getOutputStream(false)) {
			IOUtils.copy(file, out);
		} catch (IOException e) {
			return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			FileUtils.closeSafely(file);
		}
		return Response.ok().build();
	}
}
