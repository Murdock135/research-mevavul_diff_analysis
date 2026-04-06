class uploadFiles {
@Override
	public RefUpdated uploadFiles(Collection<FileUpload> uploads, String directory, String commitMessage) {
		Map<String, BlobContent> newBlobs = new HashMap<>();
		
		String parentPath = getDirectory();
		
		if (directory != null) { 
			if (parentPath != null)
				parentPath += "/" + directory;
			else
				parentPath = directory;
		}
		
		User user = Preconditions.checkNotNull(SecurityUtils.getUser());
		BlobIdent blobIdent = getBlobIdent();
		
		for (FileUpload upload: uploads) {
			String blobPath = FilenameUtils.sanitizeFilename(upload.getClientFileName());
			if (parentPath != null)
				blobPath = parentPath + "/" + blobPath;
			
			if (getProject().isReviewRequiredForModification(user, blobIdent.revision, blobPath)) 
				throw new BlobEditException("Review required for this change. Please submit pull request instead");
			else if (getProject().isBuildRequiredForModification(user, blobIdent.revision, blobPath)) 
				throw new BlobEditException("Build required for this change. Please submit pull request instead");
			
			BlobContent blobContent = new BlobContent.Immutable(upload.getBytes(), FileMode.REGULAR_FILE);
			newBlobs.put(blobPath, blobContent);
		}

		BlobEdits blobEdits = new BlobEdits(Sets.newHashSet(), newBlobs);
		String refName = blobIdent.revision!=null?GitUtils.branch2ref(blobIdent.revision):"refs/heads/master";

		ObjectId prevCommitId;
		if (blobIdent.revision != null)
			prevCommitId = getProject().getRevCommit(blobIdent.revision, true).copy();
		else
			prevCommitId = ObjectId.zeroId();

		while (true) {
			try {
				ObjectId newCommitId = blobEdits.commit(getProject().getRepository(), refName, prevCommitId, 
						prevCommitId, user.asPerson(), commitMessage);
				return new RefUpdated(getProject(), refName, prevCommitId, newCommitId);
			} catch (ObjectAlreadyExistsException|NotTreeException e) {
				throw new BlobEditException(e.getMessage());
			} catch (ObsoleteCommitException e) {
				prevCommitId = e.getOldCommitId();
			}
		}
	}
}
