package io.fidelcoria.ayfmap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.AssignmentRepository;
import io.fidelcoria.ayfmap.domain.PersonRepository;

/**
 * Import assignments
 * @author FidelCoria
 *
 */
@Service
public class AssignmentImportService {

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	PersonRepository personRepository;
	
	
	public AssignmentImportService() {
		
	}
	
	/**
	 * Save all given entities
	 * @param assignments list to be persisted
	 * @return saved entities (include id)
	 */
	public List<Assignment> save(List<Assignment> assignments) {
		
		if (assignments == null) {
			return null;
		}
		
		return assignmentRepository.save(assignments);
	}
}
