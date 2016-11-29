
public class BankJettyDriver 
{
	public static void main(String[] args) throws Exception
	{
		int portNumber = 9999;
		String restClassPackage = "app.rest2";
		String applicationContext = "applicationContext.xml";

		new JerseyStarter().start(portNumber, restClassPackage, applicationContext);
	}
}
