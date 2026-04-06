class deploy {
@Override
    public void deploy() {
        final DeploymentInfo deploymentInfo = originalDeployment.clone();

        if (deploymentInfo.getServletStackTraces() == ServletStackTraces.ALL) {
            UndertowServletLogger.REQUEST_LOGGER.servletStackTracesAll(deploymentInfo.getDeploymentName());
        }

        deploymentInfo.validate();
        final DeploymentImpl deployment = new DeploymentImpl(this, deploymentInfo, servletContainer);
        this.deployment = deployment;

        final ServletContextImpl servletContext = new ServletContextImpl(servletContainer, deployment);
        deployment.setServletContext(servletContext);
        handleExtensions(deploymentInfo, servletContext);

        final List<ThreadSetupHandler> setup = new ArrayList<>();
        setup.add(ServletRequestContextThreadSetupAction.INSTANCE);
        setup.add(new ContextClassLoaderSetupAction(deploymentInfo.getClassLoader()));
        setup.addAll(deploymentInfo.getThreadSetupActions());
        deployment.setThreadSetupActions(setup);

        deployment.getServletPaths().setWelcomePages(deploymentInfo.getWelcomePages());

        if (deploymentInfo.getDefaultEncoding() != null) {
            deployment.setDefaultCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
        }
        if(deploymentInfo.getDefaultRequestEncoding() != null) {
            deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultRequestEncoding()));
        } else if (deploymentInfo.getDefaultEncoding() != null) {
            deployment.setDefaultRequestCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
        }
        if(deploymentInfo.getDefaultResponseEncoding() != null) {
            deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultResponseEncoding()));
        } else if (deploymentInfo.getDefaultEncoding() != null) {
            deployment.setDefaultResponseCharset(Charset.forName(deploymentInfo.getDefaultEncoding()));
        }

        handleDeploymentSessionConfig(deploymentInfo, servletContext);

        deployment.setSessionManager(deploymentInfo.getSessionManagerFactory().createSessionManager(deployment));
        deployment.getSessionManager().setDefaultSessionTimeout(deploymentInfo.getDefaultSessionTimeout());


        try {
            deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Object>() {
                @Override
                public Void call(HttpServerExchange exchange, Object ignore) throws Exception {
                    final ApplicationListeners listeners = createListeners();
                    listeners.start();

                    deployment.setApplicationListeners(listeners);

                    //now create the servlets and filters that we know about. We can still get more later
                    createServletsAndFilters(deployment, deploymentInfo);

                    //first initialize the temp dir
                    initializeTempDir(servletContext, deploymentInfo);

                    //then run the SCI's
                    for (final ServletContainerInitializerInfo sci : deploymentInfo.getServletContainerInitializers()) {
                        final InstanceHandle<? extends ServletContainerInitializer> instance = sci.getInstanceFactory().createInstance();
                        try {
                            instance.getInstance().onStartup(sci.getHandlesTypes(), servletContext);
                        } finally {
                            instance.release();
                        }
                    }

                    deployment.getSessionManager().registerSessionListener(new SessionListenerBridge(deployment, listeners, servletContext));
                    for(SessionListener listener : deploymentInfo.getSessionListeners()) {
                        deployment.getSessionManager().registerSessionListener(listener);
                    }

                    initializeErrorPages(deployment, deploymentInfo);
                    initializeMimeMappings(deployment, deploymentInfo);
                    listeners.contextInitialized();
                    //run

                    HttpHandler wrappedHandlers = ServletDispatchingHandler.INSTANCE;
                    wrappedHandlers = wrapHandlers(wrappedHandlers, deploymentInfo.getInnerHandlerChainWrappers());
                    if(!deploymentInfo.isSecurityDisabled()) {
                        HttpHandler securityHandler = setupSecurityHandlers(wrappedHandlers);
                        wrappedHandlers = new PredicateHandler(DispatcherTypePredicate.REQUEST, securityHandler, wrappedHandlers);
                    }
                    HttpHandler outerHandlers = wrapHandlers(wrappedHandlers, deploymentInfo.getOuterHandlerChainWrappers());
                    wrappedHandlers = new PredicateHandler(DispatcherTypePredicate.REQUEST, outerHandlers, wrappedHandlers);
                    wrappedHandlers = handleDevelopmentModePersistentSessions(wrappedHandlers, deploymentInfo, deployment.getSessionManager(), servletContext);

                    MetricsCollector metrics = deploymentInfo.getMetricsCollector();
                    if(metrics != null) {
                        wrappedHandlers = new MetricsChainHandler(wrappedHandlers, metrics, deployment);
                    }
                    if( deploymentInfo.getCrawlerSessionManagerConfig() != null ) {
                        wrappedHandlers = new CrawlerSessionManagerHandler(deploymentInfo.getCrawlerSessionManagerConfig(), wrappedHandlers);
                    }

                    final ServletInitialHandler servletInitialHandler = SecurityActions.createServletInitialHandler(deployment.getServletPaths(), wrappedHandlers, deployment, servletContext);

                    HttpHandler initialHandler = wrapHandlers(servletInitialHandler, deployment.getDeploymentInfo().getInitialHandlerChainWrappers());
                    initialHandler = new HttpContinueReadHandler(initialHandler);
                    if(deploymentInfo.getUrlEncoding() != null) {
                        initialHandler = Handlers.urlDecodingHandler(deploymentInfo.getUrlEncoding(), initialHandler);
                    }
                    deployment.setInitialHandler(initialHandler);
                    deployment.setServletHandler(servletInitialHandler);
                    deployment.getServletPaths().invalidate(); //make sure we have a fresh set of servlet paths
                    servletContext.initDone();
                    return null;
                }
            }).call(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //any problems with the paths won't get detected until the data is initialize
        //so we force initialization here
        deployment.getServletPaths().initData();
        for(ServletContextListener listener : deploymentInfo.getDeploymentCompleteListeners()) {
            listener.contextInitialized(new ServletContextEvent(servletContext));
        }
        state = State.DEPLOYED;
    }
}
