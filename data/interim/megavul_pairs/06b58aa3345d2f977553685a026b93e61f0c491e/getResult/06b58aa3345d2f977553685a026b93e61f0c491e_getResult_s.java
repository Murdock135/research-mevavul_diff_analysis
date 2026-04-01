class getResult {
public NMapRun getResult() {
		OnePassParser parser = new OnePassParser() ;
		NMapRun nmapRun = parser.parse(results.getOutput(), OnePassParser.STRING_INPUT ) ;
		return nmapRun ;
	}
}
