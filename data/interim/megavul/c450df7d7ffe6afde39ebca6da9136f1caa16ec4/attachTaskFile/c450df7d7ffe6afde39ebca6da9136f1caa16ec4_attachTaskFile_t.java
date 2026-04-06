class attachTaskFile {
@PUT
	@Path("task/{nodeId}/file")
	@Operation(summary = "This attaches a Task file onto a given task element", description = "This attaches a Task file onto a given task element")
	@ApiResponse(responseCode = "200", description = "The task node metadatas", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = CourseNodeVO.class)),
			@Content(mediaType = "application/xml", schema = @Schema(implementation = CourseNodeVO.class)) })
	@ApiResponse(responseCode = "401", description = "The roles of the authenticated user are not sufficient")
	@ApiResponse(responseCode = "404", description = "The course or parentNode not found")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response attachTaskFile(@PathParam("courseId") Long courseId, @PathParam("nodeId") String nodeId,
			@Context HttpServletRequest request) {
			ICourse course = CoursesWebService.loadCourse(courseId);
		CourseEditorTreeNode parentNode = getParentNode(course, nodeId);
		if(course == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		if(parentNode == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		} else if(!(parentNode.getCourseNode() instanceof TACourseNode)) {
			return Response.serverError().status(Status.NOT_ACCEPTABLE).build();
		}
		if (!isAuthorEditor(course, request)) {
			return Response.serverError().status(Status.UNAUTHORIZED).build();
		}

		InputStream in = null;
		MultipartReader reader = null;
		try {
			reader = new MultipartReader(request);
			String filename = reader.getValue("filename", "task");
			String taskFolderPath = TACourseNode.getTaskFolderPathRelToFolderRoot(course, parentNode.getCourseNode());
			VFSContainer taskFolder = VFSManager.olatRootContainer(taskFolderPath, null);
			VFSLeaf singleFile = (VFSLeaf)taskFolder.resolve(filename);
			if (singleFile == null) {
				singleFile = taskFolder.createChildLeaf(filename);
			}
			File file = reader.getFile();
			if(file != null) {
				in = new FileInputStream(file);
				OutputStream out = singleFile.getOutputStream(false);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(out);
			} else {
				return Response.status(Status.NOT_ACCEPTABLE).build();
			}
		} catch (Exception e) {
			log.error("", e);
			return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			MultipartReader.closeQuietly(reader);
			IOUtils.closeQuietly(in);
		}
		
		return Response.ok().build();
	}
}
