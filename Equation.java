import java.util.*;

public class Equation {
	public ArrayList<ChemicalCompoundable> reactants;
	public ArrayList<ChemicalCompoundable> products;
	public int[] reactantAmounts;
	public int[] productAmounts;
	boolean balanced;

	public Equation(ArrayList<ChemicalCompoundable> r, ArrayList<ChemicalCompoundable> p) {
		reactants = r;
		products = p;
		reactantAmounts = new int[reactants.size()];
		Arrays.fill(reactantAmounts, 1);
		productAmounts = new int[products.size()];
		Arrays.fill(productAmounts, 1);
		balanced = false;
	}

	public Equation(String str) {
		StringTokenizer st = new StringTokenizer(str);
		boolean isProduct = false;
		reactants = new ArrayList<ChemicalCompoundable>();
		products = new ArrayList<ChemicalCompoundable>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals("->")) {
				isProduct = true;
			} else if (!token.equals("+")) {
				if (isProduct) {
					products.add(new Chemical(token));
				} else {
					reactants.add(new Chemical(token));
				}
			}
		}
		reactantAmounts = new int[reactants.size()];
		Arrays.fill(reactantAmounts, 1);
		productAmounts = new int[products.size()];
		Arrays.fill(productAmounts, 1);
		balanced = false;
	}

	public void balance() {
		balanced = true;
		LinkedList<State> stack = new LinkedList();
		stack.add(new State(reactantAmounts, productAmounts));
		HashSet<String> done = new HashSet<>();
		while (true) {
			State curState = stack.remove();
			ArrayList<Element> offSets = curState.getStateOffSets(reactants, products);
			if (offSets.size() == 0) {
				reactantAmounts = curState.reactAmounts;
				productAmounts = curState.prodAmounts;
				break;
			}
			boolean[] incrementR = new boolean[reactants.size()];
			boolean[] incrementP = new boolean[products.size()];
			for (int i = 0; i < offSets.size(); i++) {
				Element unBalancedElem = offSets.get(i);
				if (unBalancedElem.amount > 0) {
					// add product
					for (int j = 0; j < products.size(); j++) {
						if (incrementP[j])
							continue;
						if (products.get(j).copiesOfElement(unBalancedElem) > 0) {
							incrementP[j] = true;
						}
					}
				} else {
					// add react
					for (int j = 0; j < reactants.size(); j++) {
						if (incrementR[j])
							continue;
						if (reactants.get(j).copiesOfElement(unBalancedElem) > 0) {
							incrementR[j] = true;
						}
					}
				}
			}
			for (int i = 0; i < incrementR.length; i++) {
				if (incrementR[i]) {
					int[] newAmounts = new int[incrementR.length];
					System.arraycopy(curState.reactAmounts, 0, newAmounts, 0, incrementR.length);
					newAmounts[i]++;
					State newState = new State(newAmounts, curState.prodAmounts);
					String str = newState.toString();
					if (!done.contains(str)) {
						stack.add(newState);
						done.add(str);
					}
				}
			}
			for (int i = 0; i < incrementP.length; i++) {
				int[] newAmounts = new int[incrementP.length];
				System.arraycopy(curState.prodAmounts, 0, newAmounts, 0, incrementP.length);
				newAmounts[i]++;
				State newState = new State(curState.reactAmounts, newAmounts);
				String str = newState.toString();
				if (!done.contains(str)) {
					stack.add(newState);
					done.add(str);
				}
			}
		}
	}

	private class State {
		public int[] reactAmounts;
		public int[] prodAmounts;

		public State(int[] r, int[] p) {
			reactAmounts = r;
			prodAmounts = p;
		}

		public ArrayList<Element> getStateOffSets(ArrayList<ChemicalCompoundable> r,
				ArrayList<ChemicalCompoundable> p) {
			ArrayList<Element> arr = new ArrayList<Element>();
			for (int i = 0; i < r.size(); i++) {
				ArrayList<Element> curList = r.get(i).getElements();
				for (int j = 0; j < curList.size(); j++) {
					Element cur = curList.get(j);
					int index = arr.indexOf(cur);
					if (index == -1) {
						arr.add(new Element(cur.id, reactAmounts[i] * cur.amount));
					} else {
						arr.get(index).amount += reactAmounts[i] * cur.amount;
					}
				}
			}
			for (int i = 0; i < p.size(); i++) {
				ArrayList<Element> curList = p.get(i).getElements();
				for (int j = 0; j < curList.size(); j++) {
					Element cur = curList.get(j);
					int index = arr.indexOf(cur);
					arr.get(index).amount -= prodAmounts[i] * cur.amount;
				}
			}
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).amount == 0) {
					arr.remove(i);
					i--;
				}
			}
			return arr;
		}
		@Override
		public String toString(){
			return Arrays.toString(reactAmounts)+Arrays.toString(prodAmounts);
		}
	}

	public int indexOf(ArrayList arr, Element e) {
		for (int i = 0; i < arr.size(); i++)
			if (e.id == ((Element) arr.get(i)).id)
				return i;
		return -1;
	}

	public String toLatexString() {
		String str = "";
		System.out.println("reactants: " + reactants + ", products:" + products);
		for (int i = 0; i < reactants.size(); i++) {
			if (reactantAmounts[i] != 1) {
				str += reactantAmounts[i];
			}
			str += reactants.get(i).toLatexString();
			if (i < reactants.size() - 1) {
				str += "%20+%20";
			} else {
				str += "%20\\rightarrow%20";
			}
		}
		for (int i = 0; i < products.size(); i++) {
			if (productAmounts[i] != 1) {
				str += productAmounts[i];
			}
			str += products.get(i).toLatexString();
			if (i < products.size() - 1) {
				str += "%20+%20";
			}
		}
		return str;
	}

	public int amountOf(Chemical c) {
		for (int i = 0; i < reactants.size(); i++) {
			if (reactants.get(i).getElements().equals(c.getElements())) {
				return reactantAmounts[i];
			}
		}
		for (int i = 0; i < products.size(); i++) {
			if (products.get(i).getElements().equals(c.getElements())) {
				return productAmounts[i];
			}
		}
		return 0;
	}
}