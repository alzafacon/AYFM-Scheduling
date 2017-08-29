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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.entities.Person;
import suggestiongenerator.services.ImportService;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
// Even if uncommented will still commit to database (only with gradlew build)
@Transactional(propagation = Propagation.NOT_SUPPORTED) 
@AutoConfigureTestDatabase(replace=Replace.NONE) // uncomment to run against actual db
public class ImportServiceTest {

	@Autowired
	ImportService importService;
	
	@Test
	public void importStudents( ) throws Exception {
		
//		 read data
//		List<Person> students = 
//			importService.readStudentsWithCsvMapReader("<your-file-here>");
//		
//		for (Person p : students) {
//			System.out.println("person name: "+p.getFullName());
//		}
//		
//		List<Person> persisted = importService.saveStudents(students);
//		
//		System.out.println("these people are persisted");
//		for (Person p : persisted) {
//			System.out.print(p.getId()+ " ");
//			System.out.println(p.getFirstName());
//		}
//		
//		assertThat(persisted.size()).isEqualTo(students.size());
	}
	
	@Test
	public void importAssignments() throws Exception {
		
//		List<Assignment> assignmentsCsv =
//				importService.readAssignmentsWithCsvMapReader("<your-file-here>");
//		
//		List<Assignment> persisted = importService.saveAssignments(assignmentsCsv);
//		
//		assertThat(persisted.size()).isNotEqualTo(0);
//		
//		assertThat(persisted.size()).isEqualTo(assignmentsCsv.size());
	}
	
	
}
