package io.fidelcoria.ayfmap.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.AssignmentRepository;

@Service
public class PdfFormFillService {

	@Autowired
	AssignmentRepository assignmentRepository;
	
	private final static int NUM_WEEKS_PER_SCHEDULE = 5;
	
	private final static String FIRST_WEEK_SPANISH_PDF = "S-89-S_1.pdf";
	private final static String MID_MONTH_SPANISH_PDF = "S-89-S_8.pdf";
	
	// The following values are for concatenating to make the keys to the form fields
	private final static String ASSIGNEE = "Assignee";
	private final static String HOUSEHOLDER = "Householder";
	private final static String DATE = "Date";
	private final static String LESSON = "Lesson";

	private final static String TYPE = "_type-";		// _T for type
	private final static String SECTION = "_section-"; 	// _S for section
	
	@Value("${installation.directory.templates}")
	private String templateDir;
	
	public PdfFormFillService () {
		
	}
	
	public int formFill(int year, int month, String destinationDirectory) throws Exception {

		int countFilled = 0;
		
		// find the first week of the assignment schedule
		LocalDate firstWeek = LocalDate.of(year, month, 1).with(firstInMonth(DayOfWeek.MONDAY));
		int offset = 0;
		
		// load assignments from database (sorted by date)
		List<Assignment> monthlySchedule = assignmentRepository.findAllByMonthAndYear(month, year);
		
		// TODO should be logged
		System.out.println("the actual data drawn from db is: "+monthlySchedule.size());
		
		// each week of assignments is to be saved as a list element in `weeks`
		List< List<Assignment> > weeks = new LinkedList< List<Assignment> >();
		
		for (int i = 0; i < NUM_WEEKS_PER_SCHEDULE; i++) {
			weeks.add(new LinkedList<>());
		}
		
		// split the assignments by week 
		for (Assignment assignment : monthlySchedule) {
			// maybe using before and after is a more flexible way to compare week dates 
			while (!assignment.getWeek().equals(firstWeek.plusWeeks(offset))) {
				offset++;
				if (offset >= NUM_WEEKS_PER_SCHEDULE) {
					// the schedule has week not on a Monday
					// or for some other reason the week is not an exact match
					throw new Exception("Identifying weeks failed.");
				}
			}
			
			weeks.get(offset).add(assignment);
		}
		
		
		// write assignments from database into pdf

		// open pdf document to read from `src` and to write to `dest`
        // named `reminders` because the pdf document is for reminder slips
        PdfDocument reminders = null;
        
        PdfAcroForm acroForm = null;

        Map<String, PdfFormField> fields = null;
		
        try {
        	
        	for (List<Assignment> weekOfAssignments : weeks) {
				if (weekOfAssignments.size() == 0) {
					System.out.println("week has no assignments...");
					continue;
				}
				
				LocalDate week = weekOfAssignments.get(0).getWeek();
				System.out.println("week: "+week);
				String pdfTemplate = templateDir + 
						(week.equals(firstWeek)? FIRST_WEEK_SPANISH_PDF : MID_MONTH_SPANISH_PDF);
								
				reminders = new PdfDocument(
						new PdfReader(pdfTemplate), 
						new PdfWriter(destinationDirectory+week+".pdf"));
				
				acroForm = PdfAcroForm.getAcroForm(reminders, true);
	        	fields = acroForm.getFormFields();
	        	
	        	Set<String> fieldKeys = fields.keySet();
	        	
	        	System.out.println("the form keys are...");
	        	for (String key : fieldKeys) {
	        		System.out.println(key);
	        	}
	        	
	        	for (Assignment assgn : weekOfAssignments) {
	        		String suffix = buildKey("",Integer.toString(assgn.getAssignmentType().toInt()), assgn.getClassroom().toString());
	            	System.out.println("looking up key: "+DATE+suffix);
	            	fields.get(DATE+suffix).setValue(assgn.getWeek().toString());
	            	
	            	fields.get(ASSIGNEE+suffix).setValue(assgn.getAssignee().getFullName());
	            	
	            	if (assgn.getHouseholder() != null) {
	            		fields.get(HOUSEHOLDER+suffix).setValue(assgn.getHouseholder().getFullName());
	            	}
	            	
	            	fields.get(LESSON+suffix).setValue(Integer.toString(assgn.getLesson()));
	            	
	            	countFilled++;
	        	}
	        	
	        	reminders.close();
			}
        	
        	
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        	if (reminders != null && !reminders.isClosed()) {
                reminders.close();
            }
        }
        
		return countFilled;
	}
	
    /**
     * Uses a string builder to create a key describing an attribute of an assignment.
     * Please use the `static final` strings in this class meant for concatenation.
     * @param attribute The attribute of the assignment, i.e. Assignee, date, lesson, etc.
     * @param type The type of assignment as a string.
     * @param section The section of the assignment as a string.
     * @return
     */
    private static String buildKey(String attribute, String type, String section)
    {
        return new StringBuilder().append(attribute)
        		.append(TYPE).append(type)
        		.append(SECTION).append(section).toString();
    }
}
