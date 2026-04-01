class addMediaPackage {
@POST
  @Produces(MediaType.TEXT_XML)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("addMediaPackage/{wdID}")
  @RestQuery(name = "addMediaPackage",
      description = "<p>Create and ingest media package from media tracks with additional Dublin Core metadata. It is "
        + "mandatory to set a title for the recording. This can be done with the 'title' form field or by supplying a DC "
        + "catalog with a title included.  The identifier of the newly created media package will be taken from the "
        + "<em>identifier</em> field or the episode DublinCore catalog (deprecated<sup>*</sup>). If no identifier is "
        + "set, a newa randumm UUIDv4 will be generated. This endpoint is not meant to be used by capture agents for "
        + "scheduled recordings. It's primary use is for manual ingests with command line tools like curl.</p> "
        + "<p>Multiple tracks can be ingested by using multiple form fields. It's important, however, to always set the "
        + "flavor of the next media file <em>before</em> sending the media file itself.</p>"
        + "<b>(*)</b> The special treatment of the identifier field is deprecated any may be removed in future versions "
        + "without further notice in favor of a random UUID generation to ensure uniqueness of identifiers. "
        + "<h3>Example curl command:</h3>"
        + "<p>Ingest one video file:</p>"
        + "<p><pre>\n"
        + "curl -f -i --digest -u opencast_system_account:CHANGE_ME -H 'X-Requested-Auth: Digest' \\\n"
        + "    http://localhost:8080/ingest/addMediaPackage/fast -F creator='John Doe' -F title='Test Recording' \\\n"
        + "    -F 'flavor=presentation/source' -F 'BODY=@test-recording.mp4' \n"
        + "</pre></p>"
        + "<p>Ingest two video files:</p>"
        + "<p><pre>\n"
        + "curl -f -i --digest -u opencast_system_account:CHANGE_ME -H 'X-Requested-Auth: Digest' \\\n"
        + "    http://localhost:8080/ingest/addMediaPackage/fast -F creator='John Doe' -F title='Test Recording' \\\n"
        + "    -F 'flavor=presentation/source' -F 'BODY=@test-recording-vga.mp4' \\\n"
        + "    -F 'flavor=presenter/source' -F 'BODY=@test-recording-camera.mp4' \n"
        + "</pre></p>",
      pathParameters = {
          @RestParameter(description = "Workflow definition id", isRequired = true, name = "wdID", type = RestParameter.Type.STRING) },
      restParameters = {
          @RestParameter(description = "The kind of media track. This has to be specified prior to each media track", isRequired = true, name = "flavor", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "abstract", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "accessRights", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "available", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "contributor", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "coverage", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "created", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "creator", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "date", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "description", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "extent", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "format", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "identifier", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "isPartOf", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "isReferencedBy", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "isReplacedBy", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "language", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "license", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "publisher", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "relation", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "replaces", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "rights", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "rightsHolder", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "source", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "spatial", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "subject", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "temporal", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "title", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode metadata value", isRequired = false, name = "type", type = RestParameter.Type.STRING),
          @RestParameter(description = "URL of episode DublinCore Catalog", isRequired = false, name = "episodeDCCatalogUri", type = RestParameter.Type.STRING),
          @RestParameter(description = "Episode DublinCore Catalog", isRequired = false, name = "episodeDCCatalog", type = RestParameter.Type.STRING),
          @RestParameter(description = "URL of series DublinCore Catalog", isRequired = false, name = "seriesDCCatalogUri", type = RestParameter.Type.STRING),
          @RestParameter(description = "Series DublinCore Catalog", isRequired = false, name = "seriesDCCatalog", type = RestParameter.Type.STRING),
          @RestParameter(description = "URL of a media track file", isRequired = false, name = "mediaUri", type = RestParameter.Type.STRING) },
      bodyParameter = @RestParameter(description = "The media track file", isRequired = true, name = "BODY", type = RestParameter.Type.FILE),
      reponses = {
          @RestResponse(description = "Ingest successfull. Returns workflow instance as XML", responseCode = HttpServletResponse.SC_OK),
          @RestResponse(description = "Ingest failed due to invalid requests.", responseCode = HttpServletResponse.SC_BAD_REQUEST),
          @RestResponse(description = "Ingest failed. Something went wrong internally. Please have a look at the log files",
              responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR) },
      returnDescription = "")
  public Response addMediaPackage(@Context HttpServletRequest request, @PathParam("wdID") String wdID) {
    logger.trace("add mediapackage as multipart-form-data with workflow definition id: {}", wdID);
    MediaPackageElementFlavor flavor = null;
    try {
      MediaPackage mp = ingestService.createMediaPackage();
      DublinCoreCatalog dcc = null;
      Map<String, String> workflowProperties = new HashMap<>();
      int seriesDCCatalogNumber = 0;
      int episodeDCCatalogNumber = 0;
      boolean hasMedia = false;
      if (ServletFileUpload.isMultipartContent(request)) {
        for (FileItemIterator iter = new ServletFileUpload().getItemIterator(request); iter.hasNext();) {
          FileItemStream item = iter.next();
          if (item.isFormField()) {
            String fieldName = item.getFieldName();
            String value = Streams.asString(item.openStream(), "UTF-8");
            logger.trace("form field {}: {}", fieldName, value);
            /* Ignore empty fields */
            if ("".equals(value)) {
              continue;
            }

            /* “Remember” the flavor for the next media. */
            if ("flavor".equals(fieldName)) {
              try {
                flavor = MediaPackageElementFlavor.parseFlavor(value);
              } catch (IllegalArgumentException e) {
                String error = String.format("Could not parse flavor '%s'", value);
                logger.debug(error, e);
                return Response.status(Status.BAD_REQUEST).entity(error).build();
              }
              /* Fields for DC catalog */
            } else if (dcterms.contains(fieldName)) {
              if ("identifier".equals(fieldName)) {
                /* Use the identifier for the mediapackage */
                mp.setIdentifier(new IdImpl(value));
              }
              EName en = new EName(DublinCore.TERMS_NS_URI, fieldName);
              if (dcc == null) {
                dcc = dublinCoreService.newInstance();
              }
              dcc.add(en, value);

              /* Episode metadata by URL */
            } else if ("episodeDCCatalogUri".equals(fieldName)) {
              try {
                URI dcurl = new URI(value);
                updateMediaPackageID(mp, dcurl);
                ingestService.addCatalog(dcurl, MediaPackageElements.EPISODE, mp);
                episodeDCCatalogNumber += 1;
              } catch (java.net.URISyntaxException e) {
                /* Parameter was not a valid URL: Return 400 Bad Request */
                logger.warn(e.getMessage(), e);
                return Response.serverError().status(Status.BAD_REQUEST).build();
              }

              /* Episode metadata DC catalog (XML) as string */
            } else if ("episodeDCCatalog".equals(fieldName)) {
              InputStream is = new ByteArrayInputStream(value.getBytes("UTF-8"));
              updateMediaPackageID(mp, is);
              is.reset();
              String fileName = "episode" + episodeDCCatalogNumber + ".xml";
              episodeDCCatalogNumber += 1;
              ingestService.addCatalog(is, fileName, MediaPackageElements.EPISODE, mp);

              /* Series by URL */
            } else if ("seriesDCCatalogUri".equals(fieldName)) {
              try {
                URI dcurl = new URI(value);
                ingestService.addCatalog(dcurl, MediaPackageElements.SERIES, mp);
              } catch (java.net.URISyntaxException e) {
                /* Parameter was not a valid URL: Return 400 Bad Request */
                logger.warn(e.getMessage(), e);
                return Response.serverError().status(Status.BAD_REQUEST).build();
              }

              /* Series DC catalog (XML) as string */
            } else if ("seriesDCCatalog".equals(fieldName)) {
              String fileName = "series" + seriesDCCatalogNumber + ".xml";
              seriesDCCatalogNumber += 1;
              InputStream is = new ByteArrayInputStream(value.getBytes("UTF-8"));
              ingestService.addCatalog(is, fileName, MediaPackageElements.SERIES, mp);

              /* Add media files by URL */
            } else if ("mediaUri".equals(fieldName)) {
              if (flavor == null) {
                /* A flavor has to be specified in the request prior the media file */
                return Response.serverError().status(Status.BAD_REQUEST).build();
              }
              URI mediaUrl;
              try {
                mediaUrl = new URI(value);
              } catch (java.net.URISyntaxException e) {
                /* Parameter was not a valid URL: Return 400 Bad Request */
                logger.warn(e.getMessage(), e);
                return Response.serverError().status(Status.BAD_REQUEST).build();
              }
              ingestService.addTrack(mediaUrl, flavor, mp);
              hasMedia = true;

            } else {
              /* Tread everything else as workflow properties */
              workflowProperties.put(fieldName, value);
            }

            /* Media files as request parameter */
          } else {
            if (flavor == null) {
              /* A flavor has to be specified in the request prior the video file */
              logger.debug("A flavor has to be specified in the request prior to the content BODY");
              return Response.serverError().status(Status.BAD_REQUEST).build();
            }
            ingestService.addTrack(item.openStream(), item.getName(), flavor, mp);
            hasMedia = true;
          }
        }

        /* Check if we got any media. Fail if not. */
        if (!hasMedia) {
          logger.warn("Rejected ingest without actual media.");
          return Response.serverError().status(Status.BAD_REQUEST).build();
        }

        /* Add episode mediapackage if metadata were send separately */
        if (dcc != null) {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          dcc.toXml(out, true);
          InputStream in = new ByteArrayInputStream(out.toByteArray());
          ingestService.addCatalog(in, "dublincore.xml", MediaPackageElements.EPISODE, mp);

          /* Check if we have metadata for the episode */
        } else if (episodeDCCatalogNumber == 0) {
          logger.warn("Rejected ingest without episode metadata. At least provide a title.");
          return Response.serverError().status(Status.BAD_REQUEST).build();
        }

        WorkflowInstance workflow = (wdID == null) ? ingestService.ingest(mp) : ingestService.ingest(mp, wdID,
                workflowProperties);
        return Response.ok(workflow).build();
      }
      return Response.serverError().status(Status.BAD_REQUEST).build();
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
