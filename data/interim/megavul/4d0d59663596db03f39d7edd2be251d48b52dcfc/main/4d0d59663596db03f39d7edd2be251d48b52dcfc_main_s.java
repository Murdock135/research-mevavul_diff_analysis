class main {
public static void main(String[] args)throws Exception {
        Process p = Runtime.getRuntime().exec("ls");
        System.out.println(JNAUtil.getProcessID(p));
    }
}
