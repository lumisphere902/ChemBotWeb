public class Stoichiometry {
	private Equation equation;

	public Stoichiometry() {
	}

	public Stoichiometry(Equation equation) {
		this.equation = equation;
	}

	public String evaluate(Chemical given, String startType, Number value, Chemical target, String endType) {
		String work = "https://latex.codecogs.com/gif.latex?";
		work += wordsToLatex(
				"How to find the " + endType + " of " + target.name + " given the " + startType + " of " + given.name)
				+ ":\\newline%20\\newline%20";
		work += wordsToLatex("First, take the equation ") + equation.toLatexString() + "\\newline%20";
		equation.balance();
		work += wordsToLatex("Next, balance it into ") + equation.toLatexString() + "\\newline%20";
		work += wordsToLatex("Finally, find the " + endType + " you're looking for like this:")
				+ "\\newline%20\\newline%20";
		work += measurementToLatex(""+value.getValue(), given, getUnit(startType)) + "\\cdot";
		double startVal = value.value;
		if (startType.equals("mass")) {
			startVal /= given.molarMass;
			work += "\\frac{" + measurementToLatex("1", given, "mol") + "}{" + measurementToLatex(""+given.molarMass, given, "g")
					+ "}\\cdot";
		} else if (startType.equals("volume")) {
			startVal /= 22.4;
			work += "\\frac{" + measurementToLatex("1", given, "mol") + "}{" + measurementToLatex("22.4", given, "L") + "}\\cdot";
		}

		work += "\\frac{" + measurementToLatex(Integer.toString(equation.amountOf(target)), target, "mol") + "}{"
				+ measurementToLatex(""+equation.amountOf(given), given, "mol") + "}\\cdot";

		double endVal = 1;
		if (endType.equals("mass")) {
			endVal *= target.molarMass;
			work += "\\frac{" + measurementToLatex(Double.toString(target.molarMass), target, "g") + "}{"
					+ measurementToLatex("1", target, "mol") + "}";
		} else if (endType.equals("volume")) {
			endVal *= 22.4;
			work += "\\frac{" + measurementToLatex("22.4", target, "L") + "}{" + measurementToLatex("1", target, "mol") + "}";
		}
		double ans = startVal * equation.amountOf(target) / equation.amountOf(given) * endVal;
		work += "=" + measurementToLatex(Number.numToString(ans, value.sigFigs), target, getUnit(endType));
		String backSlashed = "";
		for (int i = 0; i < work.length(); i++) {
			if (work.charAt(i) == '\\') {
				backSlashed += '\\';
			}
			backSlashed += work.charAt(i);
		}
		System.out.println(ChemBot.getImgurContent("5075047008712e4", work));
		return work;
	}

	public String getUnit(String type) {
		if (type.equals("mass")) {
			return "g";
		} else if (type.equals("volume")) {
			return "L";
		} else if (type.equals("particles")) {
			return "particles";
		} else {
			return "mol";
		}
	}

	public String wordsToLatex(String words) {
		String str = "";
		for (int i = 0; i < words.length(); i++) {
			char x = words.charAt(i);
			if (x == ' ') {
				str += "\\:";
			} else {
				str += x;
			}
		}
		return str;
	}
	
	public String measurementToLatex(String amount, Chemical chemical, String unit) {
		return amount+"\\:"+unit+"\\:"+chemical.toLatexString();
	}
}