import java.util.ArrayList;

public class Email {
	private String displayName = null;
	private String emailString = null;
	private ArrayList<String> groupList = null;
	
	public Email(String displayName, String emailString,
			ArrayList<String> groupList) {
		this.displayName = displayName;
		this.emailString = emailString;
		this.groupList = groupList;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmailString() {
		return emailString;
	}

	public ArrayList<String> getGroupList() {
		return groupList;
	}

	private String getGroupXMLString() {
		if (groupList.size() > 0) {
			String groups = "";
			for (int i=0; i<groupList.size(); i++) {
				groups += "\t\t<group>" + groupList.get(i) + "</group>\n";
			}
			return "\t<grouplist>\n" + groups + "\n\t</grouplist>";
		} else {
			return "";
		}
	}
	
	public String toXMLString() {
		return "<email>\n"
			+ "\t<displayname>" + displayName + "</displayname>\n"
			+ "\t<emailstring>" + emailString + "</emailstring>\n"
			+ getGroupXMLString()
			+ "\n</email>"; 
	}
	
	public boolean equals(Object obj) {
		Email tmp = (Email) obj;
		return (this.emailString.equals(tmp.getEmailString()));
	}
}
