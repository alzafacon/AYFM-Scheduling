package io.fidelcoria.ayfmPlanner.service;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import io.fidelcoria.ayfmPlanner.domain.Assignment;
import io.fidelcoria.ayfmPlanner.service.PdfFormFillService;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
// use only one of the two below at a time
//@Commit // store test changes
@Transactional() // roll back test
//use only one of the two below at a time
@AutoConfigureTestDatabase(replace=Replace.NONE) // run against actual db
//@AutoConfigureTestDatabase(replace=Replace.ANY) // create in-memory db for test
public class PdfFormFillServiceTest {

	
	
	@Autowired
	AssignmentImportService assignmentImportService;
	
	@Autowired
	PdfFormFillService pdfFormFillService;
	
	@Test
	public void formFill() {
		
    	try {
    		File docx = new File("sample-data/2016-7.docx");
    		
    		List<Assignment> assignments = assignmentImportService.readAssignmentsFromDocx(docx, 2017, 1);
    		
    		List<Assignment> persisted = assignmentImportService.save(assignments);
    		
    		pdfFormFillService.formFill(2016, 7, "sample-data/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
