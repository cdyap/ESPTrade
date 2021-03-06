package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.entities.Clothes;
import app.entities.Shoes;

@Repository
public interface ShoesRepository extends JpaRepository<Shoes, Long>
{		
//	@Modifying
//	@Transactional
//	@Query("DELETE FROM Movie m WHERE m.title LIKE 'Harry Potter%'")
//	void deleteByTitle();
//	
//	@Query("SELECT m FROM Movie m ORDER BY m.gross DESC LIMIT 10")
//	List<Movie> findByGross();
	
	@Query("SELECT m FROM Shoes m WHERE m.brand LIKE CONCAT(?1, '%')")
	List<Shoes> findByBrand(String brand);
	
	@Query("SELECT m FROM Shoes m WHERE m.price <= ?1")
	List<Shoes> findByPrice(Double price);
	
	@Query("SELECT m FROM Shoes m WHERE m.size = ?1")
	List<Shoes> findBySize(Integer size);
	
	@Query("SELECT m FROM Shoes m WHERE m.color = ?1")
	List<Shoes> findByColor(String color);
	
	List<Shoes> findBySold(Boolean size);
	
	@Modifying
	@Transactional
	@Query("delete from Shoes s where s.id = ?1")
	void deleteShoesbyID(Long id);
}
