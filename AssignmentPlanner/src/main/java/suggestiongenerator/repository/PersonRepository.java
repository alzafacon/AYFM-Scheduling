package suggestiongenerator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import suggestiongenerator.entities.Person;
import util.Participation;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

@Repository("personRepository")
public interface PersonRepository extends JpaRepository<Person, Integer> {
	
	@Query("SELECT p FROM Person p WHERE CONCAT(p.firstName, ' ', p.lastName) = ?1")
	public Person findByFullName(String fullName);
	
	@Query("select new util.Participation(p) from Person p where p.isActive = true ")
	public List<Participation> findAllActiveStudents();
}
