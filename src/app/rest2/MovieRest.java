package app.rest2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.entities.Account;
import app.entities.Clothes;
import app.entities.Item;
import app.entities.Shoes;
import app.entities.SoldItem;
import app.repositories.AccountRepository;
import app.repositories.ClothesRepository;
import app.repositories.ItemRepository;
import app.repositories.LostItemRepository;
import app.repositories.ShoesRepository;
import app.repositories.SoldItemRepository;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

@Component
@Path("/espTrade")
public class MovieRest {
	@Autowired
	private AccountRepository accountRep;
	
	@Autowired
	private ClothesRepository clothesRep;
	
	@Autowired
	private ItemRepository itemRep;
	
	@Autowired
	private LostItemRepository lostItemRep;
	
	@Autowired
	private ShoesRepository shoesRep;
	
	@Autowired
	private SoldItemRepository soldItemRep;

	
	@GET
	@Path("/text")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> text(@QueryParam("message")String params) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("message", params);
		
		try{
			Gson gson = new GsonBuilder().create();
			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			
			java.net.Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.admu.edu.ph", 3128));
			
			
			OkHttpClient client = new OkHttpClient.Builder()
					.proxy(proxy)
					.addInterceptor(interceptor)
					.build();
			
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("http://localhost:9999/") // a legit base url is needed regardless
					.client(client)
					.addConverterFactory(GsonConverterFactory.create(gson))
					.build();
			
			JDICTService service = retrofit.create(JDICTService.class);	
			
			
			String message_id = String.valueOf(UUID.randomUUID()).replaceAll("-","");
			
			String message_sent = params;
			
			Call<Reply> sendToChikka = service.sendChikka("SEND", "639177777428", "29290091", message_id, message_sent, "b2418ebf7f826869fc8626dcee056e7d0845ad2cb5b76e9de87f9d9038049262",  "860326f64656b1901624f3147b11c76ab43d508a52af3467775e1794b463e112");
			
			Response<Reply> response = sendToChikka.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	private interface JDICTService
	{		
		@FormUrlEncoded
		@POST("https://post.chikka.com/smsapi/request")
		Call<Reply> sendChikka(@Field("message_type") String message_type,
								@Field("mobile_number") String number,
								@Field("shortcode") String shortcode,
								@Field("message_id")String id,
								@Field("message") String message,
								@Field("client_id") String client_id,
								@Field("secret_key") String secret_key);
	}
	
	@POST
	@Path("/sellItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> sellItem(@QueryParam("name") String name,
										@QueryParam("price") Double price) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Item newItem = new Item();
		
		newItem.setName(name);
		newItem.setPrice(price);
		newItem.setSold(false);
		
		itemRep.save(newItem);
		
		map.put("message", "Transaction successful! Your item has been posted.");
		return map;
	}
	
	@POST
	@Path("/sellClothes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> sellClothes(@QueryParam("name") String name,
										@QueryParam("brand") String brand,
										@QueryParam("color") String color,
										@QueryParam("size") String size,
										@QueryParam("price") Double price) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Clothes newClothes = new Clothes();
		
		newClothes.setName(name);
		newClothes.setPrice(price);
		newClothes.setBrand(brand);
		newClothes.setColor(color);
		newClothes.setSize(size);
		newClothes.setSold(false);
		
		clothesRep.save(newClothes);
		
		map.put("message", "Transaction successful! Your clothes have been posted.");
		return map;
	}
	
	@POST
	@Path("/sellShoes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> sellShoes(@QueryParam("name") String name,
										@QueryParam("brand") String brand,
										@QueryParam("color") String color,
										@QueryParam("size") String size,
										@QueryParam("price") Double price) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Shoes newShoes = new Shoes();
		
		newShoes.setName(name);
		newShoes.setPrice(price);
		newShoes.setBrand(brand);
		newShoes.setColor(color);
		newShoes.setSize(size);
		newShoes.setSold(false);
		
		shoesRep.save(newShoes);
		
		map.put("message", "Transaction successful! Your shoes have been posted.");
		return map;
	}
	
	@POST
	@Path("/buy")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> buy(@QueryParam("itemID") Long id,
										@QueryParam("buyerID") Long buyerID,
										@QueryParam("sellerID") Long sellerID) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		SoldItem soldItem = new SoldItem();
		Item item = new Item();
		
		// update item sold status
		item = itemRep.findOne(id);
		item.setSold(true);		
		itemRep.save(item);
		
		//save sold item so that buyers and sellers can be tracked
		soldItem.setItemID(id);
		soldItem.setBuyerID(buyerID);
		soldItem.setSellerID(sellerID);
		soldItemRep.save(soldItem);
		
		map.put("message", "Transaction successful! Your shoes have been posted.");
		return map;
	}
	
	@POST
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> signup(@QueryParam("idNumber") Long idNumber,
										@QueryParam("name") String name,
										@QueryParam("password") String password,
										@QueryParam("sex") String sex) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Account newAccount = new Account();
		
		
		//find existing account
		if (accountRep.findOne(idNumber) == null)
		{
			newAccount.setIdNumber(idNumber);
			newAccount.setName(name);
			newAccount.setPassword(password);
			newAccount.setSex(sex);
			accountRep.save(newAccount);
			map.put("message", "Login successful!");
		}
		else {
			map.put("message", "Account exists!");
		}
		
		
		return map;
	}
	
	@GET
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> login(@QueryParam("idNumber") Long idNumber,
										@QueryParam("password") String password) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		Account findAccount = new Account();
		
		findAccount = accountRep.findOne(idNumber);
		
		//find existing account
		if (findAccount == null)
		{
			map.put("message", "Account not found!");
		}
		else {
			if (findAccount.getPassword() == password) {
				map.put("message", "Login successful!");
				map.put("login", true);
			}
			else {
				map.put("message", "Wrong password!");
				map.put("login", false);
			}
		}
		
		
		return map;
	}
	
	public class Reply {
		Object title;

		public Object getTitle() {
			return title;
		}
		
		Object gross;

		public Object getGross() {
			return gross;
		}

	}
}
