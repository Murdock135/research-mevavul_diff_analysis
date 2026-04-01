class isAbsolute {
public static boolean isAbsolute(String url)
	{
		if (url.startsWith("//"))  // //www.domain.com/start
		{
			return true;
		}
	
		try 
		{
			URI uri = new URI(url);
			return uri.isAbsolute();
		}
		catch (URISyntaxException e) 
		{
			return true; // Block malformed URLs also
		}
	}
}
