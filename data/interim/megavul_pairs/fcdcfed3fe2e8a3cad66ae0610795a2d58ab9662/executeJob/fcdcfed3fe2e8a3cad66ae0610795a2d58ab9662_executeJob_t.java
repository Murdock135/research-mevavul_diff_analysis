class executeJob {
@Override
    protected void executeJob(JobExecutionContext jobContext) throws JobExecutionException
    {
        try {
            JobDataMap data = jobContext.getJobDetail().getJobDataMap();

            // Get the job XObject to be executed
            BaseObject object = (BaseObject) data.get("xjob");

            // Force context document
            XWikiDocument jobDocument = object.getOwnerDocument();
            if (jobDocument == null) {
                jobDocument = getXWikiContext().getWiki().getDocument(object.getDocumentReference(), getXWikiContext());
            }

            getXWikiContext().setDoc(jobDocument);
            XWikiDocument secureDocument;
            // Make sure that the secure document has the correct content author.
            if (!Objects.equals(jobDocument.getAuthors().getContentAuthor(),
                jobDocument.getAuthors().getEffectiveMetadataAuthor()))
            {
                secureDocument = jobDocument.clone();
                secureDocument.getAuthors().setContentAuthor(jobDocument.getAuthors().getEffectiveMetadataAuthor());
            } else {
                secureDocument = jobDocument;
            }
            getXWikiContext().put("sdoc", secureDocument);

            ContextualAuthorizationManager contextualAuthorizationManager =
                Utils.getComponent(ContextualAuthorizationManager.class);
            contextualAuthorizationManager.checkAccess(Right.PROGRAM);

            // Make the Job execution data available to the Groovy script
            Binding binding = new Binding(data.getWrappedMap());

            // Set the right instance of XWikiContext
            binding.setProperty("context", getXWikiContext());
            binding.setProperty("xcontext", getXWikiContext());
            data.put("xwiki", new com.xpn.xwiki.api.XWiki(getXWikiContext().getWiki(), getXWikiContext()));

            // Execute the Groovy script
            GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
            shell.evaluate(object.getLargeStringValue("script"));
        } catch (CompilationFailedException e) {
            throw new JobExecutionException(
                "Failed to execute script for job [" + jobContext.getJobDetail().getKey() + "]", e, true);
        } catch (XWikiException e) {
            throw new JobExecutionException("Failed to load the document containing the job ["
                + jobContext.getJobDetail().getKey() + "]", e, true);
        } catch (AccessDeniedException e) {
            throw new JobExecutionException("Executing the job [" + jobContext.getJobDetail().getKey() + "] failed "
                + "due to insufficient rights.", e, true);
        }
    }
}
