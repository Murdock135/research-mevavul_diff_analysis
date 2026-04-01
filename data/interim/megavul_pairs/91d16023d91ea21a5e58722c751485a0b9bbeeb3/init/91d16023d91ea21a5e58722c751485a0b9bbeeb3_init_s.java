class init {
@PostConstruct
    public void init() {
        methodsCache.initClassMethod("com.alibaba.nacos.naming.controllers");
        methodsCache.initClassMethod("com.alibaba.nacos.console.controller");
        methodsCache.initClassMethod("com.alibaba.nacos.config.server.controller");
    }
}
