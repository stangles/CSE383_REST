import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.util.ArrayList;

public class EmailREST extends HttpServlet {
	private static ArrayList<Email> emails = new ArrayList<Email>();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURI();
		if (path.equals("/email/list")) {
			listRequest(resp.getOutputStream());
		} else if (path.contains("/email/user/")) {
			userRequest(resp.getOutputStream(), path.substring(12)); // idx 12 to path.length() is the username
		} else {
			sendXML(resp.getOutputStream(), "<error>Invalid URI path!</error>");
		}
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getRequestURI();
		if (path.contains("/email/user/")) {
			String userEmail = path.substring(12);
			for (int i=0; i<emails.size(); i++) {
				if ((emails.get(i).getEmailString()).equals(userEmail)) {
					emails.remove(i);
				}
			}
		}
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream in = req.getInputStream();
		OutputStream out = resp.getOutputStream();

		Element root = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			root = doc.getRootElement();
		} catch (JDOMException e) {
			sendXML(out, "<error>"+e.getMessage()+"<error>");
			return;
		}

		parsePutXML(out, root);
	}

	public void listRequest(OutputStream out) throws IOException {
		String xmlBody = "<emailList>\n";
		for(int i = 0; i < emails.size(); i++) {
			xmlBody += emails.get(i).toXMLString() + "\n";
		}
		xmlBody += "</emailList>";
		sendXML(out, xmlBody);
	}

	public void userRequest(OutputStream out, String email) throws IOException {
		for(int i = 0; i < emails.size(); i++) {
			if(emails.get(i).getEmailString().equals(email)) {
				String emailXML = emails.get(i).toXMLString();
				sendXML(out, emailXML);
				return;	
			}
		}
		sendXML(out, "<error>\nNo Email matching the given email account\n</error>");
	}

	private void parsePutXML(OutputStream out, Element root) throws IOException {
		if (root.getName().equals("emailList")) {
			parseEmailListXML(root);
		} else if (root.getName().equals("email")) {
			parseEmailXML(root);
		} else {
			sendXML(out, "<error>\nXml does not match protocol, invalid root element.\n</error>");
		}
	}

	private void parseEmailListXML(Element emailList) {
		java.util.List<Element> eList = emailList.getChildren();
		for(int i = 0; i < eList.size(); i++) {
			parseEmailXML(eList.get(i));
		}
	}

	private void parseEmailXML(Element email) {
		java.util.List<Element> emailParts = email.getChildren();
		if (emailParts.size() == 2 && emailParts.get(0).getName().equals("displayName") && emailParts.get(1).getName().equals("emailString")) {
			Email tmp = new Email(emailParts.get(0).getTextTrim(), emailParts.get(1).getTextTrim(), new ArrayList<String>());
			if (!(emails.contains(tmp))) {
				emails.add(tmp);
			}
		} else if(emailParts.size() == 3 && emailParts.get(0).getName().equals("displayName")
				&& emailParts.get(1).getName().equals("emailString") && emailParts.get(2).getName().equals("groupList")) {
			String displayName = emailParts.get(0).getTextTrim();
			String emailString = emailParts.get(1).getTextTrim();
			java.util.List<Element> groups = emailParts.get(2).getChildren();
			ArrayList<String> groupsString = new ArrayList<String>();
			for(int i = 0; i < groups.size(); i++) {
				groupsString.add(groups.get(i).getTextTrim());
			}
			Email tmp = new Email(displayName, emailString, groupsString);
			if (!(emails.contains(tmp))) {
				emails.add(tmp);
			}
		}
	}

	private void sendXML(OutputStream out, String xmlBody) throws IOException {
		out.write(xmlBody.getBytes("UTF-8"));
		out.close();
	}
}
