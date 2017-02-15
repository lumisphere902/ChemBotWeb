import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

public class Number {
	public double value;
	public int sigFigs;
	public int uncertainty;
	public Number(String num) {
		this.value = Double.parseDouble(num);
		this.sigFigs = sigFig(num);
		this.uncertainty = findDig(num);
	}
	public Number(double value, int sigFigs, int uncertainty) {
		this.value = value;
		this.sigFigs = sigFigs;
		this.uncertainty = uncertainty;
	}
	public Number add(Number num) {
		double newValue = value + num.value;
		int newUncertainty = Math.max(uncertainty, num.uncertainty);
		int newSigFigs = sigFig(numToUncertainty(newValue, newUncertainty));
		return new Number(newValue, newSigFigs, newUncertainty);
	}
	public Number subtract(Number num) {
		double newValue = value - num.value;
		int newUncertainty = Math.max(uncertainty, num.uncertainty);
		int newSigFigs = sigFig(numToUncertainty(newValue, newUncertainty));
		return new Number(newValue, newSigFigs, newUncertainty);
	}
	public Number multiply(Number num) {
		double newValue = value * num.value;
		int newSigFigs = Math.min(sigFigs, num.sigFigs);
		String number = numToString(newValue, newSigFigs);
		return new Number(number);
	}
	public Number divide(Number num) {
		double newValue = value / num.value;
		int newSigFigs = Math.min(sigFigs, num.sigFigs);
		String number = numToString(newValue, newSigFigs);
		return new Number(number);
	}
	public String numToUncertainty(double value, int uncertainty) {
		if (uncertainty >= 0) {
			value /= Math.pow(10, uncertainty);
			value = Math.round(value);
			value *= Math.pow(10, uncertainty);
			return Integer.toString((int) value);
		}
		else {
			value /= Math.pow(10, uncertainty);
			value = Math.round(value);
			value *= Math.pow(10, uncertainty);
			String format = "#.";
			for (int i = 0; i > uncertainty; i--) {
				format += "#";
			}
			return new DecimalFormat(format).format(value);
		}
	}
	public static int sigFig(String str) {
		boolean decimalFound = false;
		boolean prevNumExists = false;
		int count = 0;
		int zeroInARow = 0;
		int i = 0;
		for (; str.charAt(i) == '0' && i < str.length(); i++)
			;
		for (; i < str.length(); i++) {
			char cur = str.charAt(i);
			if (cur == '.') {
				count += zeroInARow;
				zeroInARow = 0;
				decimalFound = true;
				prevNumExists = false;
			} else if (cur == '0') {
				zeroInARow++;
			} else {
				count++;
				if (!decimalFound || prevNumExists) {
					count += zeroInARow;
				}
				prevNumExists = true;
				zeroInARow = 0;
			}
		}
		if (decimalFound) {
			count += zeroInARow;
		}
		return count;
	}
	public static int findDig(String str) {
		str = additionRefine(str);
		boolean whole = str.charAt(str.length()-1)=='.';
		for(int i=str.length()-1-(whole?1:0);i>=0;i--){
			char cur = str.charAt(i);
			if(whole){
				if(cur!='0'){
					return str.length()-2-i;
				}
			}else{
				if(cur=='.'){
					return i-str.length()+1;
				}
			}
		}
		return Integer.MAX_VALUE;
	}

	public static String additionRefine(String str) {
		String ans = "";
		int i = 0;
		boolean foundDecimal = false;
		for (; i < str.length() && str.charAt(i) == '0'; i++)
			;
		for (; i < str.length(); i++) {
			char cur = str.charAt(i);
			if (cur == '.') {
				foundDecimal = true;
			}
			ans += cur;
			if (!foundDecimal) {
				ans += ".";
			}
		}
		return ans;
	}
	public static String numToString(double val, int sigs) {
		BigDecimal bd = new BigDecimal(val);
		bd = bd.round(new MathContext(sigs));
		return bd.toPlainString();
	}
	public double getValue(){
		return value;
	}
	public int getSigFigs(){
		return sigFigs;
	}
//	public static String numToString(double value, int sigFigs) {
//		String raw = "" + value;
//		boolean decimalFound = false;
//		int count = 0;
//		for (int i = raw.charAt(0) == '0' ? 1 : 0; i < raw.length(); i++) {
//			char cur = raw.charAt(i);
//			if (cur == '.') {
//				decimalFound = true;
//			} else {
//				count++;
//				if (count > sigFigs) {
//					boolean roundUp = false;
//					if (i < raw.length() - 1) {
//						char next = raw.charAt(i + 1);
//						if (next == '.') {
//							System.out.println("xd");
//							if (i < raw.length() - 2 && raw.charAt(i + 2) >= '5')
//								roundUp = true;
//						} else {
//							if (next >= '5')
//								roundUp = true;
//						}
//					}
//					String refined;
//					if (roundUp) {
//						System.out.println("xd");
//						refined = raw.substring(0, i - 1) + (char) (raw.charAt(i-1) + 1);
//					} else {
//						refined = raw.substring(0, i);
//					}
//					if (!decimalFound) {
//						for (int j = i; raw.charAt(j) != '.'; j++) {
//							refined += '0';
//						}
//					} else if (refined.charAt(refined.length() - 1) == '.') {
//						refined = refined.substring(0, refined.length() - 1);
//					}
//					return refined;
//				}
//			}
//		}
//		return raw;
//	}
	@Override
	public String toString() {
		return numToString(value, sigFigs);
	}
	public static void main(String[] args) {
		Number n1 = new Number("420");
		Number n2 = new Number("5.5");
		Number sub = n1.add(n2);
		System.out.println(sub.toString());
	}
}