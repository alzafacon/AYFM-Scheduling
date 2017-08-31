package io.fidelcoria.ayfmPlanner.domain;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * AUTO IMPLEMENTED by Spring into a Bean called assignmentRepository 
 * when AssignmentRepository'@Autowired'
 * 
 * @author FidelCoria
 *
 */
@Repository("assignmentRepository")
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

	final String RECENT_ASSIGNMENT =
		"FROM Assignment a  " + 
		"WHERE a.assignee.id = :id " + 
			"OR a.householder.id = :id " + 
		"ORDER BY a.week DESC ";
	
	/**
	 * Look up assignments in reverse chronological order where student has participated.
	 * @param student id to find
	 * @param pagable used to simulate a LIMIT BY
	 * @return Assignments
	 */
	@Query(RECENT_ASSIGNMENT)
	public List<Assignment> findAllParticipations(@Param("id") int student, Pageable pagable);
	
	/**
	 * Look up most recent assignment for a given student
	 * @param student id to find
	 * @return if found assignment has one record, 0 records otherwise
	 */
	default List<Assignment> findMostRecentAssignment(int student) {
		return findAllParticipations(student, new PageRequest(0, 1));
	}
}
