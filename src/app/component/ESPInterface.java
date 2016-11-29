package app.component;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.repositories.AccountRepository;
import app.repositories.ClothesRepository;
import app.repositories.ItemRepository;
import app.repositories.LostItemRepository;
import app.repositories.ShoesRepository;

@Component
public class ESPInterface {
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

}
