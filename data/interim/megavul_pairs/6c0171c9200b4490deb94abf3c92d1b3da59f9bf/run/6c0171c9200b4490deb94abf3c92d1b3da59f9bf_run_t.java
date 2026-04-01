class run {
@Override
	public void run(TaskMonitor monitor) {
		long start_ts = System.currentTimeMillis();
		monitor.setMessage("Extracting all...");

		try (RefdFile refdFile = FileSystemService.getInstance().getRefdFile(srcFSRL, monitor)) {
			GFileSystem fs = refdFile.fsRef.getFilesystem();
			GFile file = refdFile.file;
			if (!file.isDirectory()) {
				Msg.warn(this, "Extract All source not a directory!  " + file.getFSRL());
				return;
			}

			if (verifyRootOutputDir(file.getName())) {
				startExtract(fs, file, monitor);
			}
		}
		catch (CancelledException ce) {
			Msg.warn(this, "Extract all task canceled");
		}
		catch (UnsupportedOperationException | IOException e) {
			Msg.showError(this, parentComponent, "Error extracting file", e.getMessage());
		}
		Msg.info(this,
			"Exported " + getTotalFilesExportedCount() + " files, " + getTotalDirsExportedCount() +
				" directories, " + getTotalBytesExportedCount() + " bytes");

		long elapsed = System.currentTimeMillis() - start_ts;

		//@formatter:off
		int option = OptionDialog.showOptionDialog(parentComponent, "Export Summary",
			"<html><div style='margin-bottom: 20pt; text-align: center; font-weight: bold'>Export files summary:</div>" +
					"<div style='margin-bottom: 20pt'>Source location:</div>" +
					"<div style='margin-bottom: 20pt; margin-left: 50pt'>" + HTMLUtilities.friendlyEncodeHTML(srcFSRL.toPrettyString()) + "</div>" +
					"<div style='margin-bottom: 20pt;'>Destination:</div>" +
					"<div style='margin-bottom: 20pt; margin-left: 50pt'>" + HTMLUtilities.friendlyEncodeHTML(rootOutputDirectory.getPath() )+ "</div>" +
					"<div style='margin-bottom: 20pt;'>Elapsed time: " + DateUtils.formatDuration(elapsed) + "</div>" +
					"<table style='margin-bottom: 20pt;' width='100%'>" +
					"<tr><td></td><td>Files</td><td>Directories</td><td>Bytes</td></tr>" +
					"<tr><td>Successful</td><td>" + getTotalFilesExportedCount() + "</td><td>" + getTotalDirsExportedCount() + 
						"</td><td>" + FileUtilities.formatLength(getTotalBytesExportedCount()) + "</td></tr>" +
					"<tr><td>Failed</td><td>" + errorredFiles.size() + "</td><td></td><td></td></tr>" +
					"</table>" +
					"</div></html>", "OK", "Show exported files");
		//@formatter:on
		if (option == OptionDialog.OPTION_TWO) {
			try {
				FileUtilities.openNative(rootOutputDirectory);
			}
			catch (IOException e) {
				Msg.showError(this, parentComponent, "Problem Starting Explorer",
					"Problem starting file explorer: " + e.getMessage());
			}

		}
	}
}
