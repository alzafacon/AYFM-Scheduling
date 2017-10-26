package io.fidelcoria.ayfmap.service;

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
import org.springframework.transaction.annotation.Transactional;

import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.service.StudentImportService;

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
public class StudentImportServiceTest {

	@Autowired
	StudentImportService studentImportService;
	
	@Test
	public void importStudentsCsv() throws Exception {
		
		// read data
		List<Person> students = studentImportService.readStudentsWithCsvMapReader("sample-data/enrollment.csv");
		
		List<Person> persisted = studentImportService.saveStudents(students);
		
		// everything actually gets persisted
		assertThat(persisted.size()).isEqualTo(students.size());
	}
}
