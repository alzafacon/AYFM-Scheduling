package suggestiongenerator.ServiceTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.entities.Person;
import suggestiongenerator.services.ImportService;

/**
 * Typically tests run in order but there is no guarantee of this.
 * @author FidelCoria
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
// use only one of the two below at a time
//@Commit // store test changes
@Transactional() // roll back test
//use only one of the two below at a time
//@AutoConfigureTestDatabase(replace=Replace.NONE) // run against actual db
@AutoConfigureTestDatabase(replace=Replace.ANY) // create in-memory db for test
public class ImportServiceTest {

	@Autowired
	ImportService importService;
	
	@Test
	public void importStudents( ) throws Exception {
		
		// read data
		List<Person> students = 
			importService.readStudentsWithCsvMapReader("C:\\Users\\FidelCoria\\git\\AYFM-Scheduling\\Database\\enrollment.csv");
		
		List<Person> persisted = importService.saveStudents(students);
		
		// everything actually gets persisted
		assertThat(persisted.size()).isEqualTo(students.size());
	}
	
	@Test
	public void importAssignments() throws Exception {
		
		List<Assignment> assignmentsCsv =
				importService.readAssignmentsWithCsvMapReader("C:\\Users\\FidelCoria\\git\\AYFM-Scheduling\\ScheduleParsing\\csv\\2016-10.csv");
		
		List<Assignment> persisted = importService.saveAssignments(assignmentsCsv);
		
		assertThat(persisted.size()).isEqualTo(assignmentsCsv.size());
	}
	
	
}
