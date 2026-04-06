class processReturnFiles {
private void processReturnFiles(VFSLeaf target, List<BulkAssessmentRow> rows) {
		Map<String, BulkAssessmentRow> assessedIdToRow = new HashMap<>();
		for(BulkAssessmentRow row:rows) {
			assessedIdToRow.put(row.getAssessedId(), row);
		}

		if(target.exists()) {
			File parentTarget = ((LocalImpl)target).getBasefile().getParentFile();

			ZipEntry entry;
			try(InputStream is = target.getInputStream();
					ZipInputStream zis = new ZipInputStream(is)) {
				byte[] b = new byte[FileUtils.BSIZE];
				while ((entry = zis.getNextEntry()) != null) {//TODO zip
					if(!entry.isDirectory()) {
						while (zis.read(b) > 0) {
							//continue
						}

						Path op = new File(parentTarget, entry.getName()).toPath();
						if(!Files.isHidden(op) && !op.toFile().isDirectory()) {
							Path parentDir = op.getParent();
							String assessedId = parentDir.getFileName().toString();
							String filename = op.getFileName().toString();

							BulkAssessmentRow row;
							if(assessedIdToRow.containsKey(assessedId)) {
								row = assessedIdToRow.get(assessedId);
							} else {
								row = new BulkAssessmentRow();
								row.setAssessedId(assessedId);
								assessedIdToRow.put(assessedId, row);
								rows.add(row);
							}

							if(row.getReturnFiles() == null) {
								row.setReturnFiles(new ArrayList<String>(2));
							}
							row.getReturnFiles().add(filename);
						}
					}
				}
			} catch(Exception e) {
				logError("", e);
			}
		}
	}
}
