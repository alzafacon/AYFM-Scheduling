package suggestiongenerator.Service;

import org.junit.Test;

import suggestiongenerator.services.PdfFormFillService;

import static org.assertj.core.api.Assertions.assertThat;

public class PdfFormFillServiceTest {

	@Test
	public void formFill() {
		
		PdfFormFillService fill = new PdfFormFillService();
    	
    	int ret = fill.formFill("C:\\Users\\FidelCoria\\git\\AYFM-Scheduling\\AssignmentPlanner\\sample-data\\2016-7.docx", "C:\\Users\\FidelCoria\\git\\AYFM-Scheduling\\AssignmentPlanner\\sample-data\\");
    	
    	assertThat(ret).isEqualTo(0);
	}
}
