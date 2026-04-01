class getHeader {
@Override
	    public String getHeader(String name) {
	        //logger.info("Ineader .. parameter .......");
	        String value = super.getHeader(name);
	        if (value == null)
	            return null;
	        //logger.info("Ineader RequestWrapper ........... value ....");
	        return cleanXSS(value);
	    }
}
