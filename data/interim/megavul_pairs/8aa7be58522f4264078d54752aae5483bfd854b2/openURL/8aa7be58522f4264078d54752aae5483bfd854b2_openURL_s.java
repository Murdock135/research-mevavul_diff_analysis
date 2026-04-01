class openURL {
public void openURL(final String url)
    {
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
