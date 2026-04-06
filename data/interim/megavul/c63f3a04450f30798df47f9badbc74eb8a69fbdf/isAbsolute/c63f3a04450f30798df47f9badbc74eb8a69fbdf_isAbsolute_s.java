class isAbsolute {
public static boolean isAbsolute(String url)
	{
		if (url.startsWith("//"))  // //www.domain.com/start
		{
			return true;
		}
	
		if (url.startsWith("/")) // /somePage.html
		{
			return false;
		}
	
		boolean result = false;
	
		try 
		{
			URI uri = new URI(url);
			result = uri.isAbsolute();
		}
		catch (URISyntaxException e) {} //Ignore
	
		return result;
	}
}
