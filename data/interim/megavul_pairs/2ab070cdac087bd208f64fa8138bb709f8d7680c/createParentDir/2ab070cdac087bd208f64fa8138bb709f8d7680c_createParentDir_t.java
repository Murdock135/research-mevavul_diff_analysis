class createParentDir {
public static void createParentDir(File f, String eMsg) throws IOException {
        File parent = f.getParentFile();
        // warning, linux and windows behave differently. Below snippet will pass on win(security hole), fail on linux
        // warning  mkdir is canonicaling, but exists/isDirectory is not. So  where mkdirs return true, and really creates dir, isDirectory can still return false
        // can be seen on this example
        // mkdirs /a/b/../c
        // where b do not exists will lead creation of /a/c
        // but exists on /a/b/../c is false on linux  even afterwards
        // without hexing of .. paths,
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Cant create directory " + (eMsg == null ? parent : eMsg));
        }
    }
}
