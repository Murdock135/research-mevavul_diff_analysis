class executeJob {
@Override
    protected void executeJob(JobExecutionContext jobContext) throws JobExecutionException
    {
        try {
            JobDataMap data = jobContext.getJobDetail().getJobDataMap();

            // Get the job XObject to be executed
            BaseObject object = (BaseObject) data.get("xjob");

            // Force context document
            XWikiDocument jobDocument = getXWikiContext().getWiki().getDocument(object.getName(), getXWikiContext());
            getXWikiContext().setDoc(jobDocument);
            getXWikiContext().put("sdoc", jobDocument);

            if (getXWikiContext().getWiki().getRightService().hasProgrammingRights(getXWikiContext())) {

                // Make the Job execution data available to the Groovy script
                Binding binding = new Binding(data.getWrappedMap());

                // Set the right instance of XWikiContext
                binding.setProperty("context", getXWikiContext());
                binding.setProperty("xcontext", getXWikiContext());
                data.put("xwiki", new com.xpn.xwiki.api.XWiki(getXWikiContext().getWiki(), getXWikiContext()));

                // Execute the Groovy script
                GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
                shell.evaluate(object.getLargeStringValue("script"));
            } else {
                throw new JobExecutionException("The user [" + getXWikiContext().getUser() + "] didn't have "
                    + "programming rights when the job [" + jobContext.getJobDetail().getKey() + "] was scheduled.");
            }
        } catch (CompilationFailedException e) {
            throw new JobExecutionException(
                "Failed to execute script for job [" + jobContext.getJobDetail().getKey() + "]", e, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
