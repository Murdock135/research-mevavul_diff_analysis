class addExtraKexAlgorithms {
private static void addExtraKexAlgorithms(CryptoWishList cwl) {
		String[] oldKexAlgorithms = cwl.kexAlgorithms;
		List<String> kexAlgorithms = new ArrayList<>(oldKexAlgorithms.length + 1);
		for (String algo : oldKexAlgorithms)
		{
			if (!algo.equals(EXT_INFO_C))
				kexAlgorithms.add(algo);
		}
		kexAlgorithms.add(EXT_INFO_C);
		cwl.kexAlgorithms = kexAlgorithms.toArray(new String[0]);
	}
}
