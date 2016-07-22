
package c4c;

import java.util.Hashtable;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

/**
* Javaͨ��Ldap����AD����ɾ�ò�ѯ
* @author guob
*/

public class LdapbyUser {
DirContext dc = null;
String root = "lenovo,dc=com"; // LDAP�ĸ�ڵ��DC

/**
* 
* @param dn������"CN=RyanHanson,dc=example,dc=com"
* @param employeeID��Ad��һ��Ա��������
*/
public LdapbyUser(String dn,String employeeID) {
init();
searchInformation("dc=lenovo,dc=com", "", "(&(objectClass=user)(name=*))");//�������и�ڵ�
close();
}

/**
* 
* Ldap����
* 
* @return LdapContext
*/
public void init() {
Hashtable env = new Hashtable();
String LDAP_URL = "ldap://10.96.1.51:389"; //ַ
String adminName = "yufc1@lenovo.com"; // 
String adminPassword = "jjkk-0099"; // ����
env.put(Context.INITIAL_CONTEXT_FACTORY,
"com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.PROVIDER_URL, LDAP_URL);
env.put(Context.SECURITY_AUTHENTICATION, "simple");
env.put(Context.SECURITY_PRINCIPAL, adminName);
env.put(Context.SECURITY_CREDENTIALS, adminPassword);
try {
dc = new InitialDirContext(env);// ��ʼ��������
System.out.println("认证成功");// ������Ըĳ��쳣�׳���
} catch (javax.naming.AuthenticationException e) {
System.out.println("失败");
} catch (Exception e) {
System.out.println("失败" + e);
}
}

/**
* ���
*/
public void add(String newUserName) {
try {
BasicAttributes attrs = new BasicAttributes();
BasicAttribute objclassSet = new BasicAttribute("objectClass");
objclassSet.add("sAMAccountName");
objclassSet.add("employeeID");
attrs.put(objclassSet);
attrs.put("ou", newUserName);
dc.createSubcontext("ou=" + newUserName + "," + root, attrs);
} catch (Exception e) {
e.printStackTrace();
System.out.println("Exception in add():" + e);
}
}

/**
* ɾ��
* 
* @param dn
*/
public void delete(String dn) {
try {
dc.destroySubcontext(dn);
} catch (Exception e) {
e.printStackTrace();
System.out.println("Exception in delete():" + e);
}
}

/**
* ������ڵ�
* 
* @param oldDN
* @param newDN
* @return
*/
public boolean renameEntry(String oldDN, String newDN) {
try {
dc.rename(oldDN, newDN);
return true;
} catch (NamingException ne) {
System.err.println("Error: " + ne.getMessage());
return false;
}
}

/**
* �޸�
* 
* @return
*/
public boolean modifyInformation(String dn,String employeeID) {
try {
System.out.println("updating...\n");
ModificationItem[] mods = new ModificationItem[1];
/* �޸����� */
// Attribute attr0 = new BasicAttribute("employeeID", "W20110972");
// mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr0);

/* ɾ������ */
// Attribute attr0 = new BasicAttribute("description",
// "����");
// mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
// attr0);

/* ������� */
Attribute attr0 = new BasicAttribute("employeeID",employeeID);
mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);
/* �޸����� */
dc.modifyAttributes(dn+",dc=example,dc=com", mods);
return true;
} catch (NamingException e) {
e.printStackTrace();
System.err.println("Error: " + e.getMessage());
return false;
}
}

/**
* �ر�Ldap����
*/
public void close() {
if (dc != null) {
try {
dc.close();
} catch (NamingException e) {
System.out.println("NamingException in close():" + e);
}
}
}

/**
* @param base ����ڵ�(��������"dc=example,dc=com")
* @param scope ��������Χ,��Ϊ"base"(���ڵ�),"one"(����),""(����)
* @param filter ��ָ���ӽڵ�(��ʽΪ"(objectclass=*)",*��ָȫ������Ҳ����ָ��ĳһ�ض����͵����ڵ�)
*/
public void searchInformation(String base, String scope, String filter) {
SearchControls sc = new SearchControls();
if (scope.equals("base")) {
sc.setSearchScope(SearchControls.OBJECT_SCOPE);
} else if (scope.equals("one")) {
sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
} else {
sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
}
NamingEnumeration ne = null;
try {
ne = dc.search(base, filter, sc);
// Use the NamingEnumeration object to cycle through
// the result set.
while (ne.hasMore()) {
System.out.println("=============================");
SearchResult sr = (SearchResult) ne.next();
String name = sr.getName();
if (base != null && !base.equals("")) {
System.out.println("entry: " + name + "," + base);
} else {
System.out.println("entry: " + name);
}

Attributes at = sr.getAttributes();
NamingEnumeration ane = at.getAll();
while (ane.hasMore()) {
Attribute attr = (Attribute) ane.next();
String attrType = attr.getID();
NamingEnumeration values = attr.getAll();
Vector vals = new Vector();
// Another NamingEnumeration object, this time
// to iterate through attribute values.
while (values.hasMore()) {
Object oneVal = values.nextElement();
if (oneVal instanceof String) {
System.out.println(attrType + ": " + (String) oneVal);
} else {
System.out.println(attrType + ": " + new String((byte[]) oneVal));
}
}
}
}
} catch (Exception nex) {
System.err.println("Error: " + nex.getMessage());
nex.printStackTrace();
}
}
/**
* ��ѯ
* 
* @throws NamingException
*/
public void Ldapbyuserinfo(String userName) {
// Create the search controls
SearchControls searchCtls = new SearchControls();
// Specify the search scope
searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
// specify the LDAP search filter
String searchFilter = "sAMAccountName=" + userName;
// Specify the Base for the search ������ڵ�
String searchBase = "DC=example,DC=COM";
int totalResults = 0;
String returnedAtts[] = { "url", "whenChanged", "employeeID", "name",
"userPrincipalName", "physicalDeliveryOfficeName",
"departmentNumber", "telephoneNumber", "homePhone", "mobile",
"department", "sAMAccountName", "whenChanged", "mail" }; // ���Ʒ�������

searchCtls.setReturningAttributes(returnedAtts); // ���÷������Լ�

// searchCtls.setReturningAttributes(null); // ���������ԣ����������е����Լ�

try {
NamingEnumeration answer = dc.search(searchBase, searchFilter,
searchCtls);
if (answer == null || answer.equals(null)) {
System.out.println("answer is null");
} else {
System.out.println("answer not null");
}
while (answer.hasMoreElements()) {
SearchResult sr = (SearchResult) answer.next();
System.out
.println("************************************************");
System.out.println("getname=" + sr.getName());
Attributes Attrs = sr.getAttributes();
if (Attrs != null) {
try {

for (NamingEnumeration ne = Attrs.getAll(); ne
.hasMore();) {
Attribute Attr = (Attribute) ne.next();
System.out.println("AttributeID="
+ Attr.getID().toString());
// ��ȡ����ֵ
for (NamingEnumeration e = Attr.getAll(); e
.hasMore(); totalResults++) {
String user = e.next().toString(); // ����ѭ�������ȡ��userPrincipalName�û�����
System.out.println(user);
}
// System.out.println(" ---------------");
// // ��ȡ����ֵ
// Enumeration values = Attr.getAll();
// if (values != null) { // ���
// while (values.hasMoreElements()) {
// System.out.println(" 2AttributeValues="
// + values.nextElement());
// }
// }
// System.out.println(" ---------------");
}
} catch (NamingException e) {
System.err.println("Throw Exception : " + e);
}
}
}
System.out.println("Number: " + totalResults);
} catch (Exception e) {
e.printStackTrace();
System.err.println("Throw Exception : " + e);
}
}

/**
* ���������ڲ���
* @param args
*/
public static void main(String[] args) {
new LdapbyUser("CN=RyanHanson","bbs.it-home.org");
}
}
