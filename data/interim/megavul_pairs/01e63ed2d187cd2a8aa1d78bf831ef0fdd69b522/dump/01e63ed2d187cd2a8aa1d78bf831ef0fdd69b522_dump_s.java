class dump {
private void dump(Object data, ExportConfig c, ProgressReporter reporter, ExportFileManager printWriter, CsvFormat exporter) {
        if (data instanceof SubGraph)
            exporter.dump((SubGraph)data,printWriter,reporter,c);
        if (data instanceof Result)
            exporter.dump((Result)data,printWriter,reporter,c);
    }
}
