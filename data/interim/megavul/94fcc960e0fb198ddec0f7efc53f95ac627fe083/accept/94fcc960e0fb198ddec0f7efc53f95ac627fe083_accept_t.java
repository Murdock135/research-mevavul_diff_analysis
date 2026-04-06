class accept {
@Override
        public void accept(@NotNull DockerfileBuilder builder) {
            builder.from("alpine:3.19.0");
            builder.run("apk add --no-cache openssh");
            builder.expose(22);
            builder.copy("entrypoint.sh", "/entrypoint.sh");

            builder.add("authorized_keys", "/home/sshj/.ssh/authorized_keys");
            builder.copy("test-container/trusted_ca_keys", "/etc/ssh/trusted_ca_keys");

            for (String hostKey : hostKeys) {
                builder.copy(hostKey, "/etc/ssh/" + Paths.get(hostKey).getFileName());
                builder.copy(hostKey + ".pub", "/etc/ssh/" + Paths.get(hostKey).getFileName() + ".pub");
            }

            for (String certificate : certificates) {
                builder.copy(certificate, "/etc/ssh/" + Paths.get(certificate).getFileName());
            }


            builder.run("apk add --no-cache tini"
                    + " && echo \"root:smile\" | chpasswd"
                    + " && adduser -D -s /bin/ash sshj"
                    + " && passwd -u sshj"
                    + " && echo \"sshj:ultrapassword\" | chpasswd"
                    + " && chmod 600 /home/sshj/.ssh/authorized_keys"
                    + " && chmod 600 /etc/ssh/ssh_host_*_key"
                    + " && chmod 644 /etc/ssh/*.pub"
                    + " && chmod 755 /entrypoint.sh"
                    + " && chown -R sshj:sshj /home/sshj");
            builder.entryPoint("/sbin/tini", "/entrypoint.sh", "-o", "LogLevel=DEBUG2");

            builder.add("sshd_config", "/etc/ssh/sshd_config");
        }
}
