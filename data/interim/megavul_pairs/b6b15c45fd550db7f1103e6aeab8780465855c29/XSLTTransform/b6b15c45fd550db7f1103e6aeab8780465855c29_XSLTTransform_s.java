class XSLTTransform {
public XSLTranformationDoc XSLTTransform(String XMLPath, String XSLPath, long ttl) throws Exception {


			String outputXML = null;
			Source xmlSource = null;
			XSLTranformationDoc doc = null;
			Host host = hostWebAPI.getCurrentHost(request);

			/*Validate if in cache exists a valid version*/
			doc = XSLTransformationCache.getXSLTranformationDocByXMLPath(XMLPath,XSLPath);

			if(doc == null){
				/*Get the XSL source*/
				java.io.File binFile = null;
				Identifier xslId = APILocator.getIdentifierAPI().find(host, XSLPath);
				if(xslId!=null && InodeUtils.isSet(xslId.getId()) && xslId.getAssetType().equals("contentlet")){
					Contentlet cont = APILocator.getContentletAPI().findContentletByIdentifier(xslId.getId(), true, APILocator.getLanguageAPI().getDefaultLanguage().getId(), userAPI.getSystemUser(),false);
					if(cont!=null && InodeUtils.isSet(cont.getInode())){
						binFile = cont.getBinary(FileAssetAPI.BINARY_FIELD);
					}
				}else{
					File xslFile = fileAPI.getFileByURI(XSLPath, host, true, userAPI.getSystemUser(),false);
					binFile = fileAPI.getAssetIOFile (xslFile);
				} 
				
				
				/*Get the XML Source from file or from URL*/
				if(!XMLPath.startsWith("http")){
					Identifier xmlId = APILocator.getIdentifierAPI().find(host, XMLPath);
					if(xmlId!=null && InodeUtils.isSet(xmlId.getId()) && xmlId.getAssetType().equals("contentlet")){
						Contentlet cont = APILocator.getContentletAPI().findContentletByIdentifier(xmlId.getId(), true, APILocator.getLanguageAPI().getDefaultLanguage().getId(), userAPI.getSystemUser(),false);
						if(cont!=null && InodeUtils.isSet(cont.getInode())){
							xmlSource = new StreamSource(new InputStreamReader(new FileInputStream(cont.getBinary(FileAssetAPI.BINARY_FIELD)), "UTF8"));
						}
					}else{
						File xmlFile = fileAPI.getFileByURI(XMLPath, host, true,userAPI.getSystemUser(),false);
						xmlSource = new StreamSource(new InputStreamReader(new FileInputStream(fileAPI.getAssetIOFile(xmlFile)), "UTF8"));
					}

				}else{
					xmlSource = new StreamSource(XMLPath);
				}

				Source xsltSource = new StreamSource(new InputStreamReader(new FileInputStream(binFile), "UTF8"));

				// create an instance of TransformerFactory
				TransformerFactory transFact = TransformerFactory.newInstance();
				StreamResult result = new StreamResult(new ByteArrayOutputStream());
				Transformer trans = transFact.newTransformer(xsltSource);

				try{
					trans.transform(xmlSource, result);
				}catch(Exception e1){
					Logger.error(XsltTool.class, "Error in transformation. "+e1.getMessage());
					e1.printStackTrace();
				}

				outputXML = result.getOutputStream().toString();

				doc = new XSLTranformationDoc();
				doc.setIdentifier(xslId.getId());
				doc.setInode(xslId.getInode());
				doc.setXslPath(XSLPath);
				doc.setXmlPath(XMLPath);
				doc.setXmlTransformation(outputXML);
				doc.setTtl(new Date().getTime()+ttl);

				XSLTransformationCache.addXSLTranformationDoc(doc);

			}

			return doc;

	}
}
