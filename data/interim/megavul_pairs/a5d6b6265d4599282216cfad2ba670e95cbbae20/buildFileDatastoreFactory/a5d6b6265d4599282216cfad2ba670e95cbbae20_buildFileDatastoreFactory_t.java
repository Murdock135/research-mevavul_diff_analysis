class buildFileDatastoreFactory {
private static DataStoreFactory buildFileDatastoreFactory() throws IOException {
    var datastoreDirectory = new File(Constants.CREDENTIAL_DATASTORE_FOLDER);
    //noinspection ResultOfMethodCallIgnored
    datastoreDirectory.mkdir();
    datastoreDirectory.setWritable(true, true);
    datastoreDirectory.setReadable(true, true);

    return new FileDataStoreFactory(datastoreDirectory);
  }
}
