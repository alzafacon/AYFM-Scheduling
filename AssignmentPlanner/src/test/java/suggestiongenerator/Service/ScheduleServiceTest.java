package suggestiongenerator.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import suggestiongenerator.services.ScheduleService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleServiceTest {

	@Autowired
	ScheduleService scheduleService;
	
	@Test
	public void generateSchedule() throws FileNotFoundException, IOException {
		
		scheduleService.setYearMonth(2017, 8);
		
		scheduleService.generateSchedule();
		
		File outputDocx = new File("C:\\Users\\FidelCoria\\Documents\\AYFM\\schedule.docx");
		
		scheduleService.saveToDocxSchedule(outputDocx);
	}
}
