class init {
public void init(final String controllerUrl, final KieServerConfig config) {
        this.config = config;
        this.controllerUrl = controllerUrl;
        try {
            if (container == null) {
                container = ContainerProvider.getWebSocketContainer();
            }
            session = container.connectToServer(this, new ClientEndpointConfig() {
                
                
                @Override
                public Map<String, Object> getUserProperties() {
                    return Collections.emptyMap();
                }
                
                @Override
                public List<Class<? extends Encoder>> getEncoders() {
                    return Collections.emptyList();
                }
                
                @Override
                public List<Class<? extends Decoder>> getDecoders() {
                    return Collections.emptyList();
                }
                
                @Override
                public List<String> getPreferredSubprotocols() {
                    return Collections.emptyList();
                }
                
                @Override
                public List<Extension> getExtensions() {
                    return Collections.emptyList();
                }
                
                @Override
                public Configurator getConfigurator() {
                   
                    return new Configurator(){

                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {                            
                            super.beforeRequest(headers);
                            
                            String userName = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_USER, "kieserver");
                            String password = KeyStoreHelperUtil.loadPassword(config);
                            String token = config.getConfigItemValue(KieServerConstants.CFG_KIE_CONTROLLER_TOKEN);
                            
                            if (token != null && !token.isEmpty()) {
                                headers.put(AUTHORIZATION, Arrays.asList("Bearer " + token));
                            } else {
                                try {
                                    headers.put(AUTHORIZATION, Arrays.asList("Basic " + Base64.getEncoder().encodeToString((userName + ':' + password).getBytes("UTF-8"))));
                                } catch (UnsupportedEncodingException e) {
                                    logger.warn(e.getMessage());
                                }
                            }
                        }
                        
                    };
                }
            }, URI.create(controllerUrl));
            
            this.messageHandler = new KieServerMessageHandler(session);            
            this.closed.set(false);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
