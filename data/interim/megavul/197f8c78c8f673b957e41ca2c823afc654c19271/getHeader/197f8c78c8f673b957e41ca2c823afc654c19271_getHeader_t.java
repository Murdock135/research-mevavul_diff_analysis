class getHeader {
@Override
	    public String getHeader(String name) {
	        String value = super.getHeader(name);
	        if (value == null)
	            return null;
	        return cleanXSS(value);
	    }
}
