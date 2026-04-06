class main {
public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: xml2js path file");
		}
		else
		{
			try
			{
				Xml2Js fw = new Xml2Js();

				// Generates result
				StringBuffer result = new StringBuffer();
				result.append("(function() {\nvar f = {};\n");

				List<String> files = fw
						.walk(new File(new File(".").getCanonicalPath()
								+ File.separator + args[0]), null);
				Iterator<String> it = files.iterator();

				while (it.hasNext())
				{
					result.append(it.next());
				}

				result.append("\n");
				result.append("var l = mxStencilRegistry.loadStencil;\n\n");
				result.append(
						"mxStencilRegistry.loadStencil = function(filename, fn)\n{\n");
				result.append("  var t = f[filename.substring(STENCIL_PATH.length + 1)];\n");
				result.append("  var s = null;\n");
				result.append("  if (t != null) {\n");
				result.append("    s = pako.inflateRaw(Uint8Array.from(atob(t), function (c) {\n");
				result.append("      return c.charCodeAt(0);\n");
				result.append("    }), {to: 'string'});\n");
				result.append("  }\n");
				result.append("  if (fn != null && s != null) {\n");
				result.append(
						"    window.setTimeout(function(){fn(mxUtils.parseXml(s))}, 0);\n");
				result.append("  } else {\n");
				result.append(
						"    return (s != null) ? mxUtils.parseXml(s) : l.apply(this, arguments)\n");
				result.append("  }\n");
				result.append("};\n");
				result.append("})();\n");

				FileWriter writer = new FileWriter(
						new File(new File(".").getCanonicalPath()
								+ File.separator + args[1]));
				writer.write(result.toString());
				writer.flush();
				writer.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
