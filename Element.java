import java.util.ArrayList;

public class Element implements Comparable<Element>, ChemicalCompoundable{
	public static double[] elementMass = new double[] { 1.01, 4.00, 6.94, 9.01, 10.81, 12.01, 14.01, 16, 19, 20.18,
			22.99, 24.31, 26.98, 28.09, 30.97, 32.07, 35.45, 39.95, 39.1, 40.08, 44.96, 47.87, 50.94, 52, 54.94, 55.85,
			58.93, 58.69, 63.55, 65.39, 69.72, 72.61, 74.92, 78.96, 79.9, 83.8, 85.47, 87.62, 88.91, 91.22, 92.91,
			95.94, 98, 101.07, 102.91, 106.42, 107.87, 112.41, 114.82, 118.71, 121.76, 127.6, 126.9, 131.29, 132.91,
			137.33, 138.91, 140.12, 140.91, 144.24, 145, 150.36, 151.96, 157.25, 158.93, 162.5, 178.49, 167.26, 169.93,
			173.04, 174.97, 178.49, 180.95, 183.84, 186.21, 190.23, 192.22, 195.08, 196.97, 200.57, 204.38, 207.2,
			208.98, 209, 210, 222, 223, 226, 227, 232.04, 231.04, 238.03 };
	public static String[] chemTable = new String[] { "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg",
			"Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn",
			"Ga", "Ge", "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd",
			"Ln", "Sn", "Sb", "Te", "I", "Xe", "Cs", "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy",
			"Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Ti", "Pb", "Bi", "Po",
			"At", "Rn", "Fr", "Ra", "Ac", "Th", "Pa", "U" };
	public int id;
	public int amount;
	public String phase;
	public Element(int i, int a) {
		id = i;
		amount = a;
	}
	public Element(Element e) {
		id = e.id;
		amount = e.amount;
	}

	public String toString() {
		return chemTable[id]+(amount>1?amount:"");
	}
	public String toLatexString(){
		String str = chemTable[id];
		if(amount>1){
			str+="_{"+amount+"}";
		}
		return str;
	}
	@Override
	public boolean equals(Object e) {
		return id == ((Element) e).id;
	}
	@Override
	public int compareTo(Element e){
		return id-e.id;
	}

	@Override
	public double getMolarMass() {
		return elementMass[id];
	}

	@Override
	public ArrayList<Element> getElements() {
		ArrayList<Element> arr = new ArrayList<Element>(1);
		arr.add(this);
		return arr;
	}
	@Override
	public int copiesOfElement(Element e) {
		return equals(e)?0:amount;
	}
}
