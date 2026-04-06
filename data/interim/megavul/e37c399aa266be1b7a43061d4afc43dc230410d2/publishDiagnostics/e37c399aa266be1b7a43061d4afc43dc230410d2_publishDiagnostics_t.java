class publishDiagnostics {
public CompletableFuture<Path> publishDiagnostics(DOMDocument xmlDocument,
			Consumer<PublishDiagnosticsParams> publishDiagnostics, Consumer<TextDocument> triggerValidation,
			XMLValidationSettings validationSettings, CancelChecker monitor) {
		String uri = xmlDocument.getDocumentURI();
		TextDocument document = xmlDocument.getTextDocument();
		try {
			List<Diagnostic> diagnostics = this.doDiagnostics(xmlDocument, monitor, validationSettings);
			monitor.checkCanceled();
			publishDiagnostics.accept(new PublishDiagnosticsParams(uri, diagnostics));
			return null;
		} catch (CacheResourceDownloadingException e) {
			CompletableFuture<Path> future = e.getFuture();
			if (future == null) {
				// This case comes from when URL uses ../../ and resources is not included in
				// the cache path
				// To prevent from "Path Traversal leading to Remote Command Execution (RCE)"
				publishOneDiagnosticInRoot(xmlDocument, e.getMessage(), DiagnosticSeverity.Error, publishDiagnostics);
			} else {
				// An XML Schema or DTD is being downloaded by the cache manager, but it takes
				// too long.
				// In this case:
				// - 1) we add an information message to the document element to explain that
				// validation
				// cannot be performed because the XML Schema/DTD is downloading.
				publishOneDiagnosticInRoot(xmlDocument, e.getMessage(), DiagnosticSeverity.Information,
						publishDiagnostics);
				// - 2) we restart the validation only once the XML Schema/DTD is downloaded.
				future //
						.exceptionally(downloadException -> {
							// Error while downloading the XML Schema/DTD
							publishOneDiagnosticInRoot(xmlDocument, downloadException.getCause().getMessage(),
									DiagnosticSeverity.Error, publishDiagnostics);
							return null;
						}) //
						.thenAccept((path) -> {
							if (path != null) {
								triggerValidation.accept(document);
							}
						});
			}
			return future;
		}
	}
}
