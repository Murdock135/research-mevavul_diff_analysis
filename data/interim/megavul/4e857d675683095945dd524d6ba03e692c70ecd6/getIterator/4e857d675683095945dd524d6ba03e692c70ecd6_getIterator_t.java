class getIterator {
@Override
    public CompletableFuture<BatchIterator<Row>> getIterator(TransactionContext txnCtx,
                                                             CollectPhase collectPhase,
                                                             CollectTask collectTask,
                                                             boolean supportMoveToStart) {
        FileUriCollectPhase fileUriCollectPhase = (FileUriCollectPhase) collectPhase;
        InputFactory.Context<LineCollectorExpression<?>> ctx =
            inputFactory.ctxForRefs(txnCtx, FileLineReferenceResolver::getImplementation);
        ctx.add(collectPhase.toCollect());

        Role user = requireNonNull(roles.findUser(txnCtx.sessionSettings().userName()), "User who invoked a statement must exist");
        List<URI> fileUris = targetUriToStringList(txnCtx, nodeCtx, fileUriCollectPhase.targetUri()).stream()
            .map(s -> {
                var uri = FileReadingIterator.toURI(s);
                if (uri.getScheme().equals("file") && user.isSuperUser() == false) {
                    throw new UnauthorizedException("Only a superuser can read from the local file system");
                }
                return uri;
            })
            .toList();
        FileReadingIterator fileReadingIterator = new FileReadingIterator(
            fileUris,
            fileUriCollectPhase.compression(),
            fileInputFactoryMap,
            fileUriCollectPhase.sharedStorage(),
            fileUriCollectPhase.nodeIds().size(),
            getReaderNumber(fileUriCollectPhase.nodeIds(), clusterService.state().nodes().getLocalNodeId()),
            fileUriCollectPhase.withClauseOptions(),
            threadPool.scheduler()
        );
        CopyFromParserProperties parserProperties = fileUriCollectPhase.parserProperties();
        LineProcessor lineProcessor = new LineProcessor(
            parserProperties.skipNumLines() > 0
                ? new SkippingBatchIterator<>(fileReadingIterator, (int) parserProperties.skipNumLines())
                : fileReadingIterator,
            ctx.topLevelInputs(),
            ctx.expressions(),
            fileUriCollectPhase.inputFormat(),
            parserProperties,
            fileUriCollectPhase.targetColumns()
        );
        return CompletableFuture.completedFuture(lineProcessor);
    }
}
