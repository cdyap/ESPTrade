package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.entities.Clothes;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long>
{		
//	@Modifying
//	@Transactional
//	@Query("DELETE FROM Movie m WHERE m.title LIKE 'Harry Potter%'")
//	void deleteByTitle();
//	
	@Query("SELECT m FROM Clothes m WHERE m.brand LIKE '?1%'")
	List<Clothes> findByBrand(String name);
	
	@Query("SELECT m FROM Clothes m WHERE m.price <= ?1")
	List<Clothes> findByPrice(Double price);
	
	@Query("SELECT m FROM Clothes m WHERE m.size = ?1")
	List<Clothes> findBySize(String size);
	
	@Query("SELECT m FROM Clothes m WHERE m.color = ?1")
	List<Clothes> findByColor(String color);
	
	
	@Modifying
	@Transactional
	@Query("delete from Clothes c where c.id = ?1")
	void deleteClothesbyID(Long id);
}
