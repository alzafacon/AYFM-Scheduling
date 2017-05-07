package assignmentplanner;


public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to Assignment Planner.");
		
		Schedule sched = new Schedule();
		
		System.out.println("\nGenerating Schedule...");
		sched.generateSchedule();
		
		System.out.println("\n\nsched assgn count: "+sched.size());
		sched.print();
		//Eventually there will be code to create an html table and present it to the user
		//	maybe javascript could be used for this or PHP. not sure yet.
	}


	
}
