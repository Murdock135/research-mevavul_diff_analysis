class sendParameterizedEmail {
public static WebForm sendParameterizedEmail(Map<String,Object> parameters, Set<String> spamValidation, 
			Host host, User user) throws  DotRuntimeException
			{

		// check for possible spam
		if(spamValidation != null)
			if (FormSpamFilter.isSpamRequest(parameters, spamValidation)) {
				throw new DotRuntimeException("Spam detected");
			}

		//Variables initialization

		//Default parameters to be ignored when sending the email
		String ignoreString = ":formType:formName:to:from:subject:cc:bcc:html:dispatch:order:" +
		"prettyOrder:autoReplyTo:autoReplyFrom:autoReplyText:autoReplySubject:" +
		"ignore:emailTemplate:autoReplyTemplate:autoReplyHtml:chargeCreditCard:attachFiles:";
		if(UtilMethods.isSet(getMapValue("ignore", parameters))) {
			ignoreString += getMapValue("ignore", parameters).toString().replace(",", ":") + ":";
		}

		// Sort the forms' fields by the given order parameter
		String order = (String)getMapValue("order", parameters);
		Map<String, Object> orderedMap = new LinkedHashMap<String, Object>();

		// Parameter prettyOrder is used to map
		// the pretty names of the variables used in the order field
		// E.G: order = firstName, lastName
		//		prettyOrder = First Name, Last Name
		String prettyOrder = (String)getMapValue("prettyOrder", parameters);
		Map<String, String> prettyVariableNamesMap = new LinkedHashMap<String, String>();

		// Parameter attachFiles is used to specify the file kind of fields you want to attach
		// to the mail is sent by this method
		// E.G: attachFiles = file1, file2, ...
		String attachFiles = (String)getMapValue("attachFiles", parameters);

		//Building the parameters maps from the order and pretty order parameters
		if (order != null) {
			String[] orderArr = order.split("[;,]");
			String[] prettyOrderArr = prettyOrder!=null?prettyOrder.split("[;,]"):new String[0];

			for (int i = 0; i < orderArr.length; i++) {
				String orderParam = orderArr[i].trim();
				Object value = (getMapValue(orderParam, parameters) == null) ? 
						null : getMapValue(orderParam, parameters);

				if(value != null) {
					//if pretty name is passed using it as a key value in the ordered map
					if (prettyOrderArr.length > i) 
						prettyVariableNamesMap.put(orderArr[i].trim(), prettyOrderArr[i].trim());
					else
						prettyVariableNamesMap.put(orderArr[i].trim(), orderArr[i].trim());
					orderedMap.put(orderArr[i].trim(), value);
				}
			}

		}

		for (Entry<String, Object> param : parameters.entrySet()) {
			if(!orderedMap.containsKey(param.getKey())) {
				orderedMap.put(param.getKey(), param.getValue());
				prettyVariableNamesMap.put(param.getKey(), param.getKey());
			}
		}

		StringBuffer filesLinks = new StringBuffer();

		// Saving the form in the database and the submitted file to the dotCMS
		String formType = getMapValue("formType", parameters) != null?
				(String)getMapValue("formType", parameters):(String)getMapValue("formName", parameters);

				WebForm formBean = saveFormBean(parameters, host, formType, ignoreString, filesLinks);


				// Setting up the email
				// Email variables - decrypting crypted email addresses 

				String from = UtilMethods.replace((String)getMapValue("from", parameters), "spamx", "");
				String to = UtilMethods.replace((String)getMapValue("to", parameters), "spamx", "");
				String cc = UtilMethods.replace((String)getMapValue("cc", parameters), "spamx", "");
				String bcc = UtilMethods.replace((String)getMapValue("bcc", parameters), "spamx", "");
				String fromName = UtilMethods.replace((String)getMapValue("fromName", parameters), "spamx", "");
				try { from = PublicEncryptionFactory.decryptString(from); } catch (Exception e) { }
				try { to = PublicEncryptionFactory.decryptString(to); } catch (Exception e) { }
				try { cc = PublicEncryptionFactory.decryptString(cc); } catch (Exception e) { }
				try { bcc = PublicEncryptionFactory.decryptString(bcc); } catch (Exception e) { }
				try { fromName = PublicEncryptionFactory.decryptString(fromName); } catch (Exception e) { }

				String subject = (String)getMapValue("subject", parameters);
				subject = (subject == null) ? "Mail from " + host.getHostname() + "" : subject;

				String emailFolder = (String)getMapValue("emailFolder", parameters);

				boolean html = getMapValue("html", parameters) != null?Parameter.getBooleanFromString((String)getMapValue("html", parameters)):true;

				String templatePath = (String) getMapValue("emailTemplate", parameters);

				// Building email message no template
				Map<String, String> emailBodies = null;

				try {
					emailBodies = buildEmail(templatePath, host, orderedMap, prettyVariableNamesMap, filesLinks.toString(), ignoreString, user);
				} catch (Exception e) {
					Logger.error(EmailFactory.class, "sendForm: Couldn't build the email body text.", e);
					throw new DotRuntimeException("sendForm: Couldn't build the email body text.", e);
				}

				// Saving email backup in a file
				try {
					String filePath = FileUtil.getRealPath(Config.getStringProperty("EMAIL_BACKUPS"));
					new File(filePath).mkdir();

					File file = null;
					synchronized (emailTime) {
						emailTime = new Long(emailTime.longValue() + 1);
						if (UtilMethods.isSet(emailFolder)) {
							new File(filePath + File.separator + emailFolder).mkdir();
							filePath = filePath + File.separator + emailFolder;
						}
						file = new File(filePath + File.separator + emailTime.toString()
								+ ".html");
					}
					if (file != null) {
						java.io.OutputStream os = new java.io.FileOutputStream(file);
						BufferedOutputStream bos = new BufferedOutputStream(os);
						if(emailBodies.get("emailHTMLBody") != null)
							bos.write(emailBodies.get("emailHTMLBody").getBytes());
						else if(emailBodies.get("emailHTMLTableBody") != null) 
							bos.write(emailBodies.get("emailHTMLTableBody").getBytes());
						else
							bos.write(emailBodies.get("emailPlainTextBody").getBytes());
						bos.flush();
						bos.close();
						os.close();
					}
				} catch (Exception e) {
					Logger.warn(EmailFactory.class, "sendForm: Couldn't save the email backup in " + Config.getStringProperty("EMAIL_BACKUPS"));
				}

				// send the mail out;
				Mailer m = new Mailer();
				m.setToEmail(to);
				m.setFromEmail(from);
				m.setFromName(fromName);
				m.setCc(cc);
				m.setBcc(bcc);
				m.setSubject(subject);

				if (html) {
					if(UtilMethods.isSet(emailBodies.get("emailHTMLBody")))
						m.setHTMLBody(emailBodies.get("emailHTMLBody"));
					else
						m.setHTMLBody(emailBodies.get("emailHTMLTableBody"));
				}
				m.setTextBody(emailBodies.get("emailPlainTextBody"));

				//Attaching files requested to be attached to the email
				if(attachFiles != null) {
					attachFiles = "," + attachFiles.replaceAll("\\s", "") + ",";
					for(Entry<String, Object> entry : parameters.entrySet()) {
						if(entry.getValue() instanceof File && attachFiles.indexOf("," + entry.getKey() + ",") > -1) {
							File f = (File)entry.getValue();
							m.addAttachment(f, entry.getKey() + "." + UtilMethods.getFileExtension(f.getName()));
						}
					}
				}

				if (m.sendMessage()) {

					// there is an auto reply, send it on
					if ((UtilMethods.isSet((String)getMapValue("autoReplyTemplate", parameters)) ||
							UtilMethods.isSet((String)getMapValue("autoReplyText", parameters)))
							&& UtilMethods.isSet((String)getMapValue("autoReplySubject", parameters))
							&& UtilMethods.isSet((String)getMapValue("autoReplyFrom", parameters))) {

						templatePath = (String) getMapValue("autoReplyTemplate", parameters);

						if(UtilMethods.isSet(templatePath)) {
							try {
								emailBodies = buildEmail(templatePath, host, orderedMap, prettyVariableNamesMap, filesLinks.toString(), ignoreString, user);
							} catch (Exception e) {
								Logger.error(EmailFactory.class, "sendForm: Couldn't build the auto reply email body text. Sending plain text.", e);
							}
						}

						m = new Mailer();
						String autoReplyTo = (String)(getMapValue("autoReplyTo", parameters) == null?getMapValue("from", parameters):getMapValue("autoReplyTo", parameters));
						m.setToEmail(UtilMethods.replace(autoReplyTo, "spamx", ""));
						m.setFromEmail(UtilMethods.replace((String)getMapValue("autoReplyFrom", parameters), "spamx", ""));
						m.setSubject((String)getMapValue("autoReplySubject", parameters));

						String autoReplyText = (String)getMapValue("autoReplyText", parameters); 
						boolean autoReplyHtml = getMapValue("autoReplyHtml", parameters) != null?Parameter.getBooleanFromString((String)getMapValue("autoReplyHtml", parameters)):html;
						if (autoReplyText != null)
						{
							if(autoReplyHtml)
							{
								m.setHTMLBody((String)getMapValue("autoReplyText", parameters));
							} else {
								m.setTextBody((String)getMapValue("autoReplyText", parameters));
							}
						}
						else
						{
							if (autoReplyHtml) 
							{
								if(UtilMethods.isSet(emailBodies.get("emailHTMLBody")))
									m.setHTMLBody(emailBodies.get("emailHTMLBody"));
								else
									m.setHTMLBody(emailBodies.get("emailHTMLTableBody"));
							}
							m.setTextBody(emailBodies.get("emailPlainTextBody"));
						}
						m.sendMessage();
					}
				} else {
					if(formBean != null){
						try {
							HibernateUtil.delete(formBean);
						} catch (DotHibernateException e) {							
							Logger.error(EmailFactory.class, e.getMessage(), e);
						}
					}
					throw new DotRuntimeException("Unable to send the email");
				}

				return formBean;

			}
}
