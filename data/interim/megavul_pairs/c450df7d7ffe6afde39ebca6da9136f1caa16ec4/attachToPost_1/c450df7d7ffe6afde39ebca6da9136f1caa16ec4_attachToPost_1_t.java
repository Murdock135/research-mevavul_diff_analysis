class attachToPost_1 {
private Response attachToPost(Long messageKey, String filename, InputStream file,  HttpServletRequest request) {
		//load message
		Message mess = fom.loadMessage(messageKey);
		if(mess == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		if(!forum.equalsByPersistableKey(mess.getForum())) {
			return Response.serverError().status(Status.CONFLICT).build();
		}
		return attachToPost(mess, filename, file, request);
	}
}
