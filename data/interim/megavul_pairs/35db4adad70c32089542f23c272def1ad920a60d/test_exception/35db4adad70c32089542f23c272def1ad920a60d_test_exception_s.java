class test_exception {
public void test_exception() throws Exception {
		RuntimeException ex = new RuntimeException("e1");
		String text = JSON.toJSONString(ex);
		System.out.println(text);
		
		RuntimeException ex2 = (RuntimeException) JSON.parse(text);
	}
}
