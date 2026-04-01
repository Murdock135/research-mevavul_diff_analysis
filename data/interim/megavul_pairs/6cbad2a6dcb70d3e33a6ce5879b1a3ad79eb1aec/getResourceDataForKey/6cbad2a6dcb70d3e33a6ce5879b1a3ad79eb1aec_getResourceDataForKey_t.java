class getResourceDataForKey {
public Object getResourceDataForKey(String key) {
		Object data = null;
		String dataString = null;
		Matcher matcher = DATA_SEPARATOR_PATTERN.matcher(key);
		if (matcher.find()) {
			if (log.isDebugEnabled()) {
				log.debug(Messages.getMessage(
						Messages.RESTORE_DATA_FROM_RESOURCE_URI_INFO, key,
						dataString));
			}
			int dataStart = matcher.end();
			dataString = key.substring(dataStart);
			byte[] objectArray = null;
			byte[] dataArray;
			try {
				dataArray = dataString.getBytes("ISO-8859-1");
				objectArray = decrypt(dataArray);
			} catch (UnsupportedEncodingException e1) {
				// default encoding always presented.
			}
			if ("B".equals(matcher.group(1))) {
				data = objectArray;
			} else {
				try {
					ObjectInputStream in = new LookAheadObjectInputStream(new ByteArrayInputStream(objectArray));
					data = in.readObject();
				} catch (StreamCorruptedException e) {
					log.error(Messages
							.getMessage(Messages.STREAM_CORRUPTED_ERROR), e);
				} catch (IOException e) {
					log.error(Messages
							.getMessage(Messages.DESERIALIZE_DATA_INPUT_ERROR),
							e);
				} catch (ClassNotFoundException e) {
					log
							.error(
									Messages
											.getMessage(Messages.DATA_CLASS_NOT_FOUND_ERROR),
									e);
				}
			}
		}

		return data;
	}
}
