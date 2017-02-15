import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public class ChemBot {
	// args = ["Stoichiometry", "H2", "mass", "12.5", "H2O", "volume", "Hydrogen
	// + Oxygen -> Water"]
	public static void main(String[] args) {
		/*
		if (args[0].equals("Stoichiometry")) {
			System.out.println(Arrays.toString(args));
			Stoichiometry s = new Stoichiometry(new Equation(args[6]));
			s.evaluate(new  Chemical(args[1]), args[2], new Number(args[3]), new Chemical(args[4]), args[5]);
		}
		*/
		//Chemical c = new Chemical("(NH4)2SO4");
		/*
		Equation eq = new Equation("KMnO4 + HCl -> KCl + MnCl2 + H2O + Cl2");
		eq.balance();
		System.out.println("R: "+Arrays.toString(eq.reactantAmounts)+", P: "+Arrays.toString(eq.productAmounts));
		*/
		System.out.println(Chemical.isFormula("Ba(BrO3)2·2H2O"));
		
	}

	public static String getImgurContent(String clientID, String img) {
		try {
			URL url;
			url = new URL("https://api.imgur.com/3/image");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			img = URLEncoder.encode(img, "UTF-8");
			System.out.println(img);
			String data = URLEncoder.encode("image", "UTF-8") + "=" + img;
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Client-ID " + clientID);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.connect();
			StringBuilder stb = new StringBuilder();
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			System.out.println(data);

			System.out.println(conn);
			// Get the response
			if (conn.getResponseCode() > 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				while (rd.ready()) {
					System.out.println(rd.readLine());
				}
				return "ERROR2";
			} else {
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					stb.append(line).append("\n");
				}
				wr.close();
				rd.close();

				return stb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
}
