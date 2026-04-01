class vtableOffset {
protected final long vtableOffset(Object receiver) {
		/*[IF]*/
		/* Must be 'referenceClass' rather than 'type().parameterType(0)' or
		 * 'defc' so that the itable index matches the defining interface at
		 * handle creation time, otherwise handles on interfaces methods defined
		 * in parent interfaces will crash
		 */
		/*[ENDIF]*/
		Class<?> interfaceClass = referenceClass;
		if (interfaceClass.isInstance(receiver)) {
			long interfaceJ9Class = getJ9ClassFromClass(interfaceClass);
			long receiverJ9Class = getJ9ClassFromClass(receiver.getClass());
			return convertITableIndexToVTableIndex(interfaceJ9Class, (int)vmSlot, receiverJ9Class) << VTABLE_ENTRY_SHIFT;
		} else {
			throw new IncompatibleClassChangeError();
		}
	}
}
