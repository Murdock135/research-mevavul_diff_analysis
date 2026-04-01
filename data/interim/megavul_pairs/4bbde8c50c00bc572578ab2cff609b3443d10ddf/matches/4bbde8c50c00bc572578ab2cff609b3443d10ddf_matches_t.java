class matches {
@Override
        public boolean matches(InetAddress socketAddress) {
            return
                socketAddress.isAnyLocalAddress()      // 0.0.0.0, ::0
                || socketAddress.isLoopbackAddress()   // 127.0.0.0/8, ::1
                || socketAddress.isLinkLocalAddress()  // 169.254.0.0/16, fe80::/10
                || socketAddress.isSiteLocalAddress()  // 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16, fec0::/10
                || socketAddress.isMulticastAddress()  // 224.0.0.0/4, ff00::/8
                || isUniqueLocalAddress(socketAddress) // fd00::/8
                || additionalAddresses.contains(socketAddress);
        }
}
