package suggestiongenerator.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.services.AssignmentImportService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
// use only one of the two below at a time
//@Commit // store test changes
@Transactional() // roll back test
//use only one of the two below at a time
//@AutoConfigureTestDatabase(replace=Replace.NONE) // run against actual db
@AutoConfigureTestDatabase(replace=Replace.ANY) // create in-memory db for test
public class AssignmentImportServiceTest {

	@Autowired
	AssignmentImportService assignmentImportService;
	
	@Test
	public void importFromDocx() throws FileNotFoundException, IOException {
		
		File docx = new File("sample-data/2016-7.docx");
		
		List<Assignment> assignments = assignmentImportService.readAssignmentsFromDocx(docx, 2017, 1);
		
		List<Assignment> persisted = assignmentImportService.save(assignments);
		
		assertThat(assignments.size()).isEqualTo(persisted.size());
		
	}
}
