<%@ page language="java" contentType="text/plain; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.io.File,java.io.PrintWriter"%>
<%
String realPath = application.getRealPath("/");
File root = new File(realPath);
PrintWriter pw = response.getWriter();
printDir("", root, pw);
pw.close();
response.flushBuffer();
%>
<%! 
public void printDir(String prefix, File dir, PrintWriter out)
{
	out.println(prefix + "/" + dir.getName() + "/");
	for(File child : dir.listFiles())
	{
		if(child.isDirectory())
		{
			printDir(prefix + "/" + dir.getName(), child, out);
		}
		else
		{
			out.println(prefix + "/" + dir.getName() + "/" + child.getName());
		}
	}
}
%>
