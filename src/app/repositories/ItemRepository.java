package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.entities.Clothes;
import app.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>
{		
//	@Modifying
//	@Transactional
//	@Query("DELETE FROM Movie m WHERE m.title LIKE 'Harry Potter%'")
//	void deleteByTitle();
//	
//	@Query("SELECT m FROM Movie m ORDER BY m.gross DESC LIMIT 10")
//	List<Movie> findByGross();
	
	
	@Query("SELECT i FROM Item i WHERE i.name LIKE :name%")
	List<Item> findByName(String name);
	
	@Query("SELECT m FROM Item m WHERE m.price <= :price%")
	List<Item> findByPrice(Double price);
	
	@Modifying
	@Transactional
	@Query("delete from Item i where i.id = ?1")
	void deleteItembyID(Long id);
}
