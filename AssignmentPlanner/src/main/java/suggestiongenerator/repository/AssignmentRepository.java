package suggestiongenerator.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import suggestiongenerator.entities.Assignment;

@Repository("assignmentRepository")
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

	String RECENT_ASSIGNMENT =
			"FROM Assignment a  " + 
			"WHERE a.assignee.id = :id " + 
			"OR a.householder.id = :id " + 
			"ORDER BY a.week DESC ";
	
	@Query(RECENT_ASSIGNMENT)
	public List<Assignment> findAllParticipations(@Param("id") int student, Pageable pagable);
	
	default List<Assignment> findMostRecentAssignment(int student) {
		return findAllParticipations(student, new PageRequest(0, 1));
	}
}
