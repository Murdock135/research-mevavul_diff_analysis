class matches {
@Override
        public boolean matches(InetAddress socketAddress) {
            return socketAddress.isAnyLocalAddress()
                || socketAddress.isLoopbackAddress()
                || socketAddress.isLinkLocalAddress()
                || socketAddress.isSiteLocalAddress();
        }
}
