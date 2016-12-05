package app.rest2;

import java.io.Console;
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
import app.entities.LostItem;
import app.entities.Shoes;
import app.entities.SoldItem;
import app.repositories.AccountRepository;
import app.repositories.ClothesRepository;
import app.repositories.ItemRepository;
import app.repositories.LostItemRepository;
import app.repositories.ShoesRepository;
import app.repositories.SoldItemRepository;
import app.rest2.MovieRest.Reply;
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
										@QueryParam("price") Double price,
										@QueryParam("sellerID") Long id) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		Account seller = new Account();
		
		seller = accountRep.findOne(id);
		
		Item newItem = new Item();
		
		newItem.setName(name);
		newItem.setPrice(price);
		newItem.setSold(false);
		newItem.setSeller(seller);
		itemRep.save(newItem);
		
		map.put("message", "Transaction successful! Your item has been posted.");
		return map;
	}
	
	@POST
	@Path("/sellClothes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply sellClothes(@QueryParam("name") String name,
										@QueryParam("brand") String brand,
										@QueryParam("color") String color,
										@QueryParam("size") String size,
										@QueryParam("price") Double price,
										@QueryParam("sellerID") Long id) throws IOException
	{
		//HashMap<String, String> map = new HashMap<String, String>();
		
		Clothes newClothes = new Clothes();
		Account seller = new Account();
		
		seller = accountRep.findOne(id);
		
		newClothes.setName(name);
		newClothes.setPrice(price);
		newClothes.setBrand(brand);
		newClothes.setColor(color);
		newClothes.setSize(size);
		newClothes.setSold(false);
		newClothes.setSeller(seller);
		clothesRep.save(newClothes);
		
		Reply r = new Reply();
		
		r.setMessage("Transaction successful! Your clothes have been posted");
		
		//map.put("message", "Transaction successful! Your clothes have been posted.");
		return r;
	}
	
	@POST
	@Path("/sellShoes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> sellShoes(@QueryParam("name") String name,
										@QueryParam("brand") String brand,
										@QueryParam("color") String color,
										@QueryParam("size") Integer size,
										@QueryParam("price") Double price,
										@QueryParam("sellerID") Long id) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Shoes newShoes = new Shoes();
		
		Account seller = new Account();
		
		seller = accountRep.findOne(id);
		
		
		newShoes.setName(name);
		newShoes.setPrice(price);
		newShoes.setBrand(brand);
		newShoes.setColor(color);
		newShoes.setSize(size);
		newShoes.setSold(false);
		newShoes.setSeller(seller);
		shoesRep.save(newShoes);
		
		map.put("message", "Transaction successful! Your shoes have been posted.");
		return map;
	}
	
	@POST
	@Path("/newLostItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> newLostItem(@QueryParam("name") String name,
										@QueryParam("locationFound") String locationFound) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		LostItem newLostItem = new LostItem();
		
		//save new lost item
		newLostItem.setName(name);
		newLostItem.setLocationFound(locationFound);
		newLostItem.setFound(false);
		
		lostItemRep.save(newLostItem);
		
		map.put("message", "Transaction successful! Your lost item has been posted.");
		return map;
	}
	
	@POST
	@Path("/foundLostItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> foundLostItem(@QueryParam("id") Long id) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		LostItem lostItem = new LostItem();
		
		//find lost item
		lostItem = lostItemRep.findOne(id);
		
		if (lostItem == null ) {
			map.put("message", "Item not found.");
		} else {
			lostItem.setFound(true);
			lostItemRep.save(lostItem);
			map.put("message", "Item found!");
		}
		
		return map;
	}
	
	
	
	
	//buy
	@POST
	@Path("/buyClothes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply buyClothes(@QueryParam("itemID") Long id,
										@QueryParam("buyerID") Long buyerID) throws IOException
	{

		Reply r = new Reply();		
		SoldItem soldItem = new SoldItem();
		Clothes item = new Clothes();
		
		// update item sold status
		item = clothesRep.findOne(id);
		item.setSold(true);		
		clothesRep.save(item);
		
		//save sold item so that buyers and sellers can be tracked
		soldItem.setItemID(id);
		soldItem.setBuyerID(buyerID);
		soldItem.setType("clothes");
		soldItemRep.save(soldItem);
		
		

		r.setMessage("Transaction successful! You have bought " + item.getName() + ".");

		return r;
	}
	
	@POST
	@Path("/buyShoes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply buyShoes(@QueryParam("itemID") Long id,
										@QueryParam("buyerID") Long buyerID) throws IOException
	{
		Reply r = new Reply();
		
		SoldItem soldItem = new SoldItem();
		Shoes item = new Shoes();
		
		// update item sold status
		item = shoesRep.findOne(id);
		item.setSold(true);		
		shoesRep.save(item);
		
		//save sold item so that buyers and sellers can be tracked
		soldItem.setItemID(id);
		soldItem.setBuyerID(buyerID);
		soldItem.setType("shoes");
		soldItemRep.save(soldItem);
		
		r.setMessage("Transaction successful! You have bought " + item.getName() + ".");
		return r;
	}
	
	@POST
	@Path("/buyItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply buyItem(@QueryParam("itemID") Long id,
										@QueryParam("buyerID") Long buyerID) throws IOException
	{
		Reply r = new Reply();
		
		SoldItem soldItem = new SoldItem();
		Item item = new Item();
		
		// update item sold status
		item = itemRep.findOne(id);
		item.setSold(true);		
		itemRep.save(item);
		
		//save sold item so that buyers and sellers can be tracked
		soldItem.setItemID(id);
		soldItem.setBuyerID(buyerID);
		soldItem.setType("item");
		soldItemRep.save(soldItem);
		
		r.setMessage("Transaction successful! You have bought " + item.getName() + ".");
		return r;
	}
	
	
	
	@POST
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, String> signup(@QueryParam("idNumber") Long idNumber,
										@QueryParam("name") String name,
										@QueryParam("password") String password,
										@QueryParam("sex") String sex,
										@QueryParam("cellphone") Double cellphone) throws IOException
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
			newAccount.setCellphone(cellphone);
			accountRep.save(newAccount);
			map.put("message", "Signup successful!");
			
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
	public Reply login(@QueryParam("idNumber") Long idNumber,
					   @QueryParam("password") String password) throws IOException
	{
		//HashMap<String, Object> map = new HashMap<String, Object>();
		
		Reply reply = new Reply();
		Account findAccount = new Account();
		
		findAccount = accountRep.findByIdNumber(idNumber);
		
		//find existing account
		if (findAccount.equals(null))
		{
			reply.setMessage("Account not found!");
		}
		else {
			if (findAccount.getPassword().equals(password)) {
				reply.setMessage("Login successful!");
				System.out.println(findAccount.getIdNumber());
				reply.setAccountName(findAccount.getName());
				
			}
			else {
				reply.setMessage("Wrong password!");
			}
		}
		
		
		return reply;
	}
	
	
	
	
	
	//List items
	@GET
	@Path("/listClothes")
	@Produces(MediaType.APPLICATION_JSON)
	public Reply listClothes() throws IOException
	{
		Reply r = new Reply();
		
		List<Clothes> list = clothesRep.findBySold(false);
		r.setMessage(list);
		
		return r;
	}
	
	@GET
	@Path("/listShoes")
	@Produces(MediaType.APPLICATION_JSON)
	public Reply listShoes() throws IOException
	{
		Reply r = new Reply();
		List<Shoes> list = shoesRep.findBySold(false);
		r.setMessage(list);
		return r;
	}
	
	@GET
	@Path("/listItems")
	@Produces(MediaType.APPLICATION_JSON)
	public Reply listItems() throws IOException
	{
		Reply r = new Reply();
		List<Item> list = itemRep.findBySold(false);
		r.setMessage(list);
		return r;
	}
	
	
	
	
	
	
	// Filter clothes
	@GET
	@Path("/findClothesbyBrand")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findClothesbyBrand(@QueryParam("brand") String brand) throws IOException
	{
		Reply map = new Reply();
		
		map.setMessage(clothesRep.findByBrand(brand));
			
		return map;
	}
	
	@GET
	@Path("/findClothesbyPrice")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findClothesbyPrice(@QueryParam("price") Double price) throws IOException
	{
		Reply r = new Reply();
		
		r.setMessage(clothesRep.findByPrice(price));
			
		return r;
	}
	
	@GET
	@Path("/findClothesbySize")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findClothesbySize(@QueryParam("size") String size) throws IOException
	{
		Reply r = new Reply();
		
		r.setMessage(clothesRep.findBySize(size));
			
		return r;
	}
	
	@GET
	@Path("/findClothesbyColor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findClothesbyColor(@QueryParam("color") String color) throws IOException
	{
		Reply r = new Reply();
		
		r.setMessage(clothesRep.findByColor(color));
			
		return r;
	}
	
	
	
	
	
	
	// Filter shoes
	@GET
	@Path("/findShoesbyBrand")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findShoesbyBrand(@QueryParam("brand") String brand) throws IOException
	{
		Reply r = new Reply();

		r.setMessage(shoesRep.findByBrand(brand));

		return r;
	}

	@GET
	@Path("/findShoesbyPrice")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findShoesbyPrice(@QueryParam("price") Double price) throws IOException
	{
		Reply r = new Reply();

		r.setMessage(shoesRep.findByPrice(price));

		return r;
	}

	@GET
	@Path("/findShoesbySize")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findShoesbySize(@QueryParam("size") Integer size) throws IOException
	{
		Reply r = new Reply();

		r.setMessage(shoesRep.findBySize(size));

		return r;
	}

	@GET
	@Path("/findShoesbyColor")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reply findShoesbyColor(@QueryParam("color") String color) throws IOException
	{
		Reply r = new Reply();

		r.setMessage(shoesRep.findByColor(color));

		return r;
	}



	
	
	//Filter items
	@GET
	@Path("/findItembyName")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> findItembyName(@QueryParam("name") String name) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("list", itemRep.findByName(name));

		return map;
	}

	@GET
	@Path("/findItembyPrice")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> findItembyPrice(@QueryParam("price") Double price) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("list", itemRep.findByPrice(price));

		return map;
	}
	
	
	
	
	//delete methods
	@POST
	@Path("/deleteAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> deleteAccount(@QueryParam("id") Long id) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		accountRep.delete(id);
		map.put("message", "Account " + id.toString() + " deleted.");

		return map;
	}
	
	@POST
	@Path("/deleteItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> deleteItem(@QueryParam("id") Long id) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		String itemName = itemRep.findOne(id).getName();
		itemRep.delete(id);
		map.put("message", "Item " + itemName + " deleted.");

		return map;
	}
	
	@POST
	@Path("/deleteShoes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> deleteShoes(@QueryParam("id") Long id) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		String itemName = shoesRep.findOne(id).getName();
		shoesRep.delete(id);
		map.put("message", "Item " + itemName + " deleted.");

		return map;
	}
	
	@POST
	@Path("/deleteClothes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Object> deleteClothes(@QueryParam("id") Long id) throws IOException
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		String itemName = clothesRep.findOne(id).getName();
		clothesRep.delete(id);
		map.put("message", "Item " + itemName + " deleted.");

		return map;
	}
	
	public class Reply {
		Object message;
		Object accountName;
		public Object getMessage() {
			return message;
		}
		public void setMessage(Object message) {
			this.message = message;
		}
		public Object getAccountName() {
			return accountName;
		}
		public void setAccountName(Object accountName) {
			this.accountName = accountName;
		}

	}
	
}
