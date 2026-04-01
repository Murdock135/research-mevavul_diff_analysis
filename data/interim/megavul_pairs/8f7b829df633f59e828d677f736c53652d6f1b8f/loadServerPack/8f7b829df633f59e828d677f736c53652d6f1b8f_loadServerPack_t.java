class loadServerPack {
@SuppressWarnings("ResultOfMethodCallIgnored")
    @Inject(at = @At("TAIL"), method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;")
    public void loadServerPack(File file, ResourcePackSource packSource, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        try (ZipResourcePack zipResourcePack = new ZipResourcePack("lmao", file, false)) {
            //noinspection DataFlowIssue
            ZipFile zipFile = ((ZipResourcePackInvoker) zipResourcePack).getTheZipFile();
            try {
                Path path = MinecraftClient.getInstance().runDirectory.toPath().resolve("serverrp_exposer");
                Files.createDirectories(path);

                int i = 1;
                while (Files.exists(path.resolve("server_resource_pack_" + i))) {
                    i++;
                }
                path = path.resolve("server_resource_pack_" + i);

                ServerRPExposer.LOGGER.info("[ServerRPExposer] Copying server resource pack to: " + path);

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File entryDestination = path.resolve(entry.getName()).toFile();
                    if (!entryDestination.getCanonicalPath().startsWith(path.toRealPath() + "/")) continue;
                    if (entry.isDirectory()) {
                        entryDestination.mkdirs();
                    } else {
                        entryDestination.getParentFile().mkdirs();
                        InputStream in = zipFile.getInputStream(entry);
                        OutputStream out = new FileOutputStream(entryDestination);
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
            } catch (IOException e) {
                ServerRPExposer.LOGGER.error("[ServerRPExposer] Failed to extract server resource pack!");
            }
        } catch (IOException e) {
            ServerRPExposer.LOGGER.error("[ServerRPExposer] Failed to extract server resource pack!");
        }
    }
}
