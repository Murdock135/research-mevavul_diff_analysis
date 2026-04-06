class buildFileDatastoreFactory {
private static DataStoreFactory buildFileDatastoreFactory() throws IOException {
    var datastoreDirectory = new File(Constants.CREDENTIAL_DATASTORE_FOLDER);
    //noinspection ResultOfMethodCallIgnored
    datastoreDirectory.mkdir();

    return new FileDataStoreFactory(datastoreDirectory);
  }
}
