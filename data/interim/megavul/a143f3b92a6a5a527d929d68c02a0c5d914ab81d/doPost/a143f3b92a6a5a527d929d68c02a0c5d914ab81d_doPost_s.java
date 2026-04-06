class doPost {
@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String formattedSvgEncoded = req.getParameter("fsvg");
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String transformto = req.getParameter("transformto");
        String jpdl = req.getParameter("jpdl");
        String gpd = req.getParameter("gpd");
        String bpmn2in = req.getParameter("bpmn2");
        String jsonin = req.getParameter("json");
        String preprocessingData = req.getParameter("pp");
        String respaction = req.getParameter("respaction");
        String pp = req.getParameter("pp");
        String processid = req.getParameter("processid");
        String sourceEnc = req.getParameter("enc");
        String convertServiceTasks = req.getParameter("convertservicetasks");
        String htmlSourceEnc = req.getParameter("htmlenc");

        String formattedSvg = ( formattedSvgEncoded == null ? "" : new String(Base64.decodeBase64(formattedSvgEncoded), "UTF-8") );

        String htmlSource = ( htmlSourceEnc == null ? "" : new String(Base64.decodeBase64(htmlSourceEnc), "UTF-8") );

        if(sourceEnc != null && sourceEnc.equals("true")) {
            bpmn2in = new String(Base64.decodeBase64(bpmn2in), "UTF-8");
        }

        if (profile == null) {
            profile = _profileService.findProfile(req, profileName);
        }

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        Repository repository = profile.getRepository();

        if (transformto != null && transformto.equals(TO_PDF)) {
            if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {

                try {
                    ByteArrayOutputStream pdfBout = new ByteArrayOutputStream();
                    Document pdfDoc = new Document(PageSize.A4);
                    PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, pdfBout);
                    pdfDoc.open();
                    pdfDoc.addCreationDate();

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");

                    float widthHint = getFloatParam(req, SVG_WIDTH_PARAM, DEFAULT_PDF_WIDTH);
                    float heightHint = getFloatParam(req, SVG_HEIGHT_PARAM, DEFAULT_PDF_HEIGHT);
                    String objStyle = "style=\"width:" + widthHint + "px;height:" + heightHint + "px;\"";
                    t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, widthHint);
                    t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, heightHint);


                    ByteArrayOutputStream imageBout = new ByteArrayOutputStream();
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(imageBout);
                    t.transcode(input, output);

                    Image processImage = Image.getInstance(imageBout.toByteArray());
                    scalePDFImage(pdfDoc, processImage);
                    pdfDoc.add(processImage);

                    pdfDoc.close();

                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("text/plain");

                    resp.getWriter().write("<object type=\"application/pdf\" " + objStyle + " data=\"data:application/pdf;base64," + Base64.encodeBase64String(pdfBout.toByteArray()) + "\"></object>");
                } catch(Exception e) {
                    resp.sendError(500, e.getMessage());
                }
            } else {
                storeInRepository(uuid, formattedSvg, transformto, processid, repository);

                try {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/pdf");
                    if (processid != null) {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + processid + ".pdf\"");
                    } else {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + uuid + ".pdf\"");
                    }

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    Document pdfDoc = new Document(PageSize.A4);
                    PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, resp.getOutputStream());
                    pdfDoc.open();
                    pdfDoc.addCreationDate();

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);

                    Image processImage = Image.getInstance(bout.toByteArray());
                    scalePDFImage(pdfDoc, processImage);
                    pdfDoc.add(processImage);

                    pdfDoc.close();
                } catch(Exception e) {
                    resp.sendError(500, e.getMessage());
                }
            }
        } else if (transformto != null && transformto.equals(TO_PNG)) {
            try {
                if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("text/plain");
                    if(req.getParameter(SVG_WIDTH_PARAM) != null && req.getParameter(SVG_HEIGHT_PARAM) != null) {
                        int widthHint = (int) getFloatParam(req, SVG_WIDTH_PARAM, DEFAULT_PDF_WIDTH);
                        int heightHint = (int) getFloatParam(req, SVG_HEIGHT_PARAM, DEFAULT_PDF_HEIGHT);
                        resp.getWriter().write("<img width=\"" + widthHint + "\" height=\"" + heightHint + "\" src=\"data:image/png;base64," + Base64.encodeBase64String(bout.toByteArray()) + "\">");
                    } else {
                        resp.getWriter().write("<img src=\"data:image/png;base64," + Base64.encodeBase64String(bout.toByteArray()) + "\">");
                    }
                } else {
                    storeInRepository(uuid, formattedSvg, transformto, processid, repository);
                    resp.setContentType("image/png");
                    if (processid != null) {
                        resp.setHeader("Content-Disposition", "attachment; filename=\"" + processid + ".png\"");
                    } else {
                        resp.setHeader("Content-Disposition", "attachment; filename=\"" + uuid + ".png\"");
                    }

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(
                            resp.getOutputStream());
                    t.transcode(input, output);
                }
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        } else if (transformto != null && transformto.equals(TO_SVG)) {
            storeInRepository(uuid, formattedSvg, transformto, processid, repository);
        } else if (transformto != null && transformto.equals(JPDL_TO_BPMN2)) {
            try {
                String bpmn2 = JbpmMigration.transform(jpdl);
                Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2);
                // add bpmndi info to Definitions with help of gpd
                addBpmnDiInfo(def, gpd);
                // hack for now
                revisitSequenceFlows(def, bpmn2);
                // another hack if id == name
                revisitNodeNames(def);

                // get the xml from Definitions
                ResourceSet rSet = new ResourceSetImpl();
                rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
                JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
                rSet.getResources().add(bpmn2resource);
                bpmn2resource.getContents().add(def);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bpmn2resource.save(outputStream, new HashMap<Object, Object>());
                String fullXmlModel =  outputStream.toString();
                // convert to json and write response
                String json = profile.createUnmarshaller().parseModel(fullXmlModel, profile, pp);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().print(json);
            } catch(Exception e) {
                _logger.error(e.getMessage());
                resp.setContentType("application/json");
                resp.getWriter().print("{}");
            }
        }  else if (transformto != null && transformto.equals(BPMN2_TO_JSON)) {
            try {
                if(convertServiceTasks != null && convertServiceTasks.equals("true")) {
                    bpmn2in = bpmn2in.replaceAll("drools:taskName=\".*?\"", "drools:taskName=\"ReadOnlyService\"");
                    bpmn2in = bpmn2in.replaceAll("tns:taskName=\".*?\"", "tns:taskName=\"ReadOnlyService\"");
                }

                Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2in);
                def.setTargetNamespace("http://www.omg.org/bpmn20");

                if(convertServiceTasks != null && convertServiceTasks.equals("true")) {
                    // fix the data input associations for converted tasks
                    List<RootElement> rootElements =  def.getRootElements();
                    for(RootElement root : rootElements) {
                        if(root instanceof Process) {
                            updateTaskDataInputs((Process) root, def);
                        }
                    }
                }

                // get the xml from Definitions
                ResourceSet rSet = new ResourceSetImpl();
                rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
                JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION, true);
                bpmn2resource.getDefaultLoadOptions().put( JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY, true );
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF, JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);
                bpmn2resource.setEncoding("UTF-8");
                rSet.getResources().add(bpmn2resource);
                bpmn2resource.getContents().add(def);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bpmn2resource.save(outputStream, new HashMap<Object, Object>());
                String revisedXmlModel =  outputStream.toString();
                String json = profile.createUnmarshaller().parseModel(revisedXmlModel, profile, pp);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().print(json);
            } catch(Exception e) {
                e.printStackTrace();
                _logger.error(e.getMessage());
                resp.setContentType("application/json");
                resp.getWriter().print("{}");
            }
        } else if (transformto != null && transformto.equals(JSON_TO_BPMN2)) {
            try {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();
                if(preprocessingData == null) {
                    preprocessingData = "";
                }
                String processXML = profile.createMarshaller().parseModel(jsonin, preprocessingData);
                resp.setContentType("application/xml");
                resp.getWriter().print(processXML);
            } catch(Exception e) {
                e.printStackTrace();
                _logger.error(e.getMessage());
                resp.setContentType("application/xml");
                resp.getWriter().print("");
            }

        } else if (transformto != null && transformto.equals(HTML_TO_PDF)) {
            try {
                resp.setContentType("application/pdf");
                if (processid != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + processid + ".pdf\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + uuid + ".pdf\"");
                }

                Document pdfDoc = new Document(PageSize.A4);
                PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, resp.getOutputStream());
                pdfDoc.open();
                pdfDoc.addCreator("jBPM Designer");
                pdfDoc.addSubject("Business Process Documentation");
                pdfDoc.addCreationDate();
                pdfDoc.addTitle("Process Documentation");

                HTMLWorker htmlWorker = new HTMLWorker(pdfDoc);
                htmlWorker.parse(new StringReader(htmlSource));
                pdfDoc.close();
            } catch(DocumentException e) {
                resp.sendError(500, e.getMessage());
            }
        }
    }
}
