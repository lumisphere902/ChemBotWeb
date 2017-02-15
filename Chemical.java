import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Chemical implements ChemicalCompoundable {

	public ChemicalCompoundable[] parts;
	public ArrayList<Element> rawElements;
	public String name;
	public int amount;
	public String phase;
	public double molarMass;

	public Chemical(String formula) {
		this(formula, 1);
	}

	public Chemical(String str, int amount) {
		boolean nameFound = false;
		if (false&&!isFormula(str)) {
			name = str;
			nameFound = true;
			System.out.println("detected it's a name");
			try {
				URL url;
				url = new URL("https://www.wolframcloud.com/objects/4e38e001-a051-4752-a78f-390ca9521804");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				String data = URLEncoder.encode("chemical", "UTF-8") + "=" + toWolfram(str);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.connect();
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				if (conn.getResponseCode() > 200) {
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
					while (rd.ready()) {
						System.out.println(rd.readLine());
					}
				} else {
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					str = rd.readLine();
					str = str.substring(1, str.length() - 1);
					wr.close();
					rd.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Formula name: " + str);
		this.amount = amount;
		ArrayList<String> split = splitString(str);
		int numObj = split.size();
		parts = new ChemicalCompoundable[numObj];
		for (int i = 0; i < numObj; i++) {
			String cur = split.get(i);
			String num = "";
			for (int j = cur.length() - 1;; j--) {
				if (Character.isDigit(cur.charAt(j))) {
					num = cur.charAt(j) + num;
				} else {
					break;
				}
			}
			int curAmount = num.isEmpty() ? 1 : Integer.parseInt(num);
			if (cur.charAt(0) == '(') {
				parts[i] = new Chemical(cur.substring(1, cur.indexOf(')')), curAmount);
			} else {
				String elem = "" + cur.charAt(0);
				if (cur.length() > 1 && Character.isLetter(cur.charAt(1))) {
					elem += cur.charAt(1);
				}
				parts[i] = new Element(getAtomicNumber(elem), curAmount);
			}
		}
		// calc molarmass and rawElements
		molarMass = 0;
		rawElements = new ArrayList<Element>();
		for (int i = 0; i < parts.length; i++) {
			molarMass += parts[i].getMolarMass();
			addOnElements(parts[i].getElements());
		}
		if (!nameFound) {
			name = this.toLatexString();
		}
	}

	public static boolean isFormula(String str) {
		String temp = "";
		for (int i = 0; i < str.length(); i++) {
			char cur = str.charAt(i);
			if (Character.isDigit(cur)||cur=='('||cur==')') {
				return true;
			}
			if (Character.isUpperCase(cur)) {
				if (temp.length() > 2 || (!temp.isEmpty() && getAtomicNumber(temp) == -1))
					return false;
				temp = "" + cur;
			} else {
				temp += cur;
			}
		}
		if (temp.length() > 2 || getAtomicNumber(temp) == -1)
			return false;
		return true;
	}

	public static int getAtomicNumber(String str) {
		for (int i = 0; i < 92; i++) {
			if (Element.chemTable[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	private String toWolfram(String str) {
		StringTokenizer st = new StringTokenizer(str);
		String ans = "";
		while (st.hasMoreTokens()) {
			String temp = st.nextToken();
			temp.toLowerCase();
			char[] arr = temp.toCharArray();
			arr[0] = Character.toUpperCase(arr[0]);
			ans += new String(arr);
		}
		return ans;
	}

	private void addOnElements(ArrayList<Element> arr) {
		for (int i = 0, ii = arr.size(); i < ii; i++) {
			Element toBeAdded = arr.get(i);
			int index = rawElements.indexOf(toBeAdded);
			if (index == -1) {
				rawElements.add(new Element(toBeAdded));
			} else {
				rawElements.get(index).amount += toBeAdded.amount;
			}
		}
	}

	private ArrayList<String> splitString(String str) {
		ArrayList<String> arr = new ArrayList<String>();
		String temp = "";
		boolean chem = false;
		for (int i = 0; i < str.length(); i++) {
			char cur = str.charAt(i);
			if (cur == ' ')
				continue;
			if (chem) {
				if (cur == ')') {
					chem = false;
				}
				temp += cur;
			} else {
				if (cur == '(') {
					chem = true;
				}
				if (!temp.isEmpty() && (cur == '(' || Character.isUpperCase(cur))) {
					arr.add(temp);
					temp = "" + cur;
				} else {
					temp += cur;
				}
			}
		}
		if (!temp.isEmpty())
			arr.add(temp);
		return arr;
	}

	public ArrayList<Element> getElements() {
		return rawElements;
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < parts.length; i++) {
			Object cur = parts[i];
			str += cur.toString();
		}
		return amount > 1 ? "(" + str + ")" + amount : str;
	}

	public String debugString() {
		System.out.println(parts.length);
		String str = "{";
		for (int i = 0; i < parts.length; i++) {
			Object cur = parts[i];
			if (cur instanceof Chemical) {
				str += ((Chemical) cur).debugString();
			} else {
				str += cur.toString();
			}
			str += ',';
		}
		str = str.substring(0, str.length() - 1);
		str += '}';
		return str + ":" + amount;
	}

	public String toLatexString() {
		String str = "";
		for (int i = 0; i < parts.length; i++) {
			str += parts[i].toLatexString();
		}

		return (amount > 1 ? "(" + str + ")_{" + amount + "}" : str);
	}

	@Override
	public double getMolarMass() {
		return molarMass;
	}

	@Override
	public int copiesOfElement(Element e) {
		int index = rawElements.indexOf(e);
		return index==-1?0:rawElements.get(index).amount;
	}
}