<%@ Page Language="C#" %>
<%@ Import Namespace="System.IO" %>
<%
	
    string path = @"C:\Bala\"; // to specify absolute location or
    path = AppDomain.CurrentDomain.BaseDirectory; // to specify application's base directory
 
  // to get folder names only
    foreach (string s in Directory.GetDirectories(path, "*.*", SearchOption.AllDirectories))
    {
        Response.Write("/" + s.Remove(0, path.Length).Replace("\\", "/") + "/\r\n");
    }
 
    // to get list of all files
    foreach (string s in Directory.GetFiles(path, "*.*", SearchOption.AllDirectories))
    {
        Response.Write("/" + s.Remove(0, path.Length).Replace("\\", "/")  + "\r\n");
    }
%>