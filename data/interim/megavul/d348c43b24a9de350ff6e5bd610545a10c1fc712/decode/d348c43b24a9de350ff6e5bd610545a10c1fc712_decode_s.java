class decode {
public void decode(ResResource res, Directory inDir, Directory outDir, Map<String, String> resFileMapping)
            throws AndrolibException {

        ResFileValue fileValue = (ResFileValue) res.getValue();
        String inFilePath = fileValue.toString();
        String inFileName = fileValue.getStrippedPath();
        String outResName = res.getFilePath();
        String typeName = res.getResSpec().getType().getName();

        String ext = null;
        String outFileName;
        int extPos = inFileName.lastIndexOf(".");
        if (extPos == -1) {
            outFileName = outResName;
        } else {
            ext = inFileName.substring(extPos).toLowerCase();
            outFileName = outResName + ext;
        }

        String outFilePath = "res/" + outFileName;
        if (!inFilePath.equals(outFilePath)) {
            resFileMapping.put(inFilePath, outFilePath);
        }

        LOGGER.fine("Decoding file " + inFilePath + " to " + outFilePath);

        try {
            if (typeName.equals("raw")) {
                decode(inDir, inFilePath, outDir, outFileName, "raw");
                return;
            }
            if (typeName.equals("font") && !".xml".equals(ext)) {
                decode(inDir, inFilePath, outDir, outFileName, "raw");
                return;
            }
            if (typeName.equals("drawable") || typeName.equals("mipmap")) {
                if (inFileName.toLowerCase().endsWith(".9" + ext)) {
                    outFileName = outResName + ".9" + ext;

                    // check for htc .r.9.png
                    if (inFileName.toLowerCase().endsWith(".r.9" + ext)) {
                        outFileName = outResName + ".r.9" + ext;
                    }

                    // check for raw 9patch images
                    for (String extension : RAW_9PATCH_IMAGE_EXTENSIONS) {
                        if (inFileName.toLowerCase().endsWith("." + extension)) {
                            copyRaw(inDir, outDir, inFilePath, outFileName);
                            return;
                        }
                    }

                    // check for xml 9 patches which are just xml files
                    if (inFileName.toLowerCase().endsWith(".xml")) {
                        decode(inDir, inFilePath, outDir, outFileName, "xml");
                        return;
                    }

                    try {
                        decode(inDir, inFilePath, outDir, outFileName, "9patch");
                        return;
                    } catch (CantFind9PatchChunkException ex) {
                        LOGGER.log(Level.WARNING, String.format(
                            "Cant find 9patch chunk in file: \"%s\". Renaming it to *.png.", inFileName
                        ), ex);
                        outDir.removeFile(outFileName);
                        outFileName = outResName + ext;
                    }
                }

                // check for raw image
                for (String extension : RAW_IMAGE_EXTENSIONS) {
                    if (inFileName.toLowerCase().endsWith("." + extension)) {
                        copyRaw(inDir, outDir, inFilePath, outFileName);
                        return;
                    }
                }

                if (!".xml".equals(ext)) {
                    decode(inDir, inFilePath, outDir, outFileName, "raw");
                    return;
                }
            }

            decode(inDir, inFilePath, outDir, outFileName, "xml");
        } catch (RawXmlEncounteredException ex) {
            // If we got an error to decode XML, lets assume the file is in raw format.
            // This is a large assumption, that might increase runtime, but will save us for situations where
            // XSD files are AXML`d on aapt1, but left in plaintext in aapt2.
            decode(inDir, inFilePath, outDir, outFileName, "raw");
        } catch (AndrolibException ex) {
            LOGGER.log(Level.SEVERE, String.format(
                "Could not decode file, replacing by FALSE value: %s",
            inFileName), ex);
            res.replace(new ResBoolValue(false, 0, null));
        }
    }
}
