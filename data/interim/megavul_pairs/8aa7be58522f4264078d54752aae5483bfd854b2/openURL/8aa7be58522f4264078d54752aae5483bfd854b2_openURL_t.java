class openURL {
public void openURL(final String url)
    {
        if (url == null || !url.startsWith("http"))
        {
            logger.warn("Not a valid URL to open:" + url);
            return;
        }
        Thread launchBrowserThread
            = new Thread(getClass().getName())
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                launchBrowser(url);
                            }
                            catch (Exception e)
                            {
                                logger.error("Failed to launch browser", e);
                            }
                        }
                    };

        launchBrowserThread.start();
    }
}
