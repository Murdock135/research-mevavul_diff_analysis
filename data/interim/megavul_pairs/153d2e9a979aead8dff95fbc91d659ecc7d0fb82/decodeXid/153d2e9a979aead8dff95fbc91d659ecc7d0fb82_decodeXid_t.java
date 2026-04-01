class decodeXid {
public static Xid decodeXid(final ActiveMQBuffer in) {
      int formatID = in.readInt();
      byte[] bq = safeReadBytes(in);
      byte[] gtxid = safeReadBytes(in);
      return new XidImpl(bq, formatID, gtxid);
   }
}
