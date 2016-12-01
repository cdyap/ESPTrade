
public class TradeDriver 
{
	public static void main(String[] args) throws Exception
	{
		int portNumber = 9999;
		String restClassPackage = "app.rest2";
		String applicationContext = "applicationContext_jpa.xml";

		new JerseyStarter().start(portNumber, restClassPackage, applicationContext);
	}
}
