package app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.entities.LostItem;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long>
{		
//	@Modifying
//	@Transactional
//	@Query("DELETE FROM Movie m WHERE m.title LIKE 'Harry Potter%'")
//	void deleteByTitle();
//	
//	@Query("SELECT m FROM Movie m ORDER BY m.gross DESC LIMIT 10")
//	List<Movie> findByGross();
	
	LostItem findByName(String name);
	
	@Modifying
	@Transactional
	@Query("delete from LostItem l where l.id = ?1")
	void deleteLostItembyID(Long id);
}
