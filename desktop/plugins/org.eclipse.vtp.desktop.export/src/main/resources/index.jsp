<%@ page language="java" contentType="text/plain; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.io.File,java.io.PrintWriter"%>
<%
String requestURI = request.getRequestURI();
String realPath = application.getRealPath("/");
System.out.println(realPath);
File root = new File(realPath);
System.out.println(root.exists());
PrintWriter pw = response.getWriter();
for(File child : root.listFiles())
{
	if(child.isDirectory())
	{
		printDir("", child, pw);
	}
}
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