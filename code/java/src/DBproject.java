/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
				System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	public static void AddDoctor(DBproject esql) {//1
		try {
			String docID = "", name, specialty, did; 
			//System.out.print("Enter the Doctor ID: $ "); 
			//docID = in.readLine();
				
			System.out.print("Enter the Name of the Doctor: $ "); 
			name = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Specialty of the Doctor: $ "); 
			specialty = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Department ID of the Doctor: $ "); 
			did = in.readLine(); 
			String query = "Select * from Doctor where name = " + name + " and specialty = " + specialty + " and did = " + did; 
			if (esql.executeQueryAndPrintResult(query) == 0) // nothing in return, doctor not in DB
			{
				query = "Select count(*) from Doctor"; 
				docID = esql.executeQueryAndReturnResult(query).get(0).get(0); 
				System.out.print("Adding New Doctor with Doctor ID: " + docID + "\n"); 
				query = "Insert into Doctor Values (" + docID + ", " + name + ", " + specialty + ", " + did + ")";
				esql.executeUpdate(query);
			}
			else {
				System.out.print("Doctor Exists in DB. Ending...\n"); 
				return; 
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void AddPatient(DBproject esql) {//2
		try{
			String pid = "", name, gender, age, address, numberOfAppt; 
			//System.out.print("Enter the Patient ID: $ ");
			//pid = in.readLine() + ", "; 
			System.out.print("Enter the Name of the Patient: $ "); 
			name = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Gender of the Patient (M/F): $ "); 
			gender = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Age of the Patient: $ "); 
			age = in.readLine(); 
			System.out.print("Enter the Address of the Patient: $ "); 
			address = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Number of Appointment: $ "); 
			numberOfAppt = in.readLine(); 
			String query = "Select * from Patient where name = " + name + " and gtype = " + gender + " and age = " + age + " and address = " + address + " and number_of_appts = " + numberOfAppt;
			if (esql.executeQueryAndPrintResult(query) == 0) // nothing in return, patient not in DB
			{
				query = "Select count(*) from Patient"; 
				pid = esql.executeQueryAndReturnResult(query).get(0).get(0); 
				System.out.print("Adding New Patient with Patient ID: " + pid + "\n");  
				query = "Insert into Patient Values (" + pid + "," + name + "," + gender + "," + age + "," + address + "," + numberOfAppt + ")"; 
				esql.executeUpdate(query);
			}
			else {
				System.out.print("Doctor Exists in DB. Ending...\n"); 
				return; 
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void AddAppointment(DBproject esql) {//3
		try {
			String appid, date, timeSlot, status; 
			//System.out.print("Enter the Appointment ID: $ ");
			//appid = in.readLine(); 
			System.out.print("Enter the Date of the Appointment (M/D/YYYY): $ "); 
			date = "\'" + in.readLine() + "\'"; 
			System.out.print("Enter the Time Slot of the Appointment: $ "); 
			timeSlot = "\'" + in.readLine() + "\'";  
			System.out.print("Enter the status of the Appointment:'PA' = Past, 'AC' = Active, 'AV' = Available, 'WL' = WaitList: $ "); 
			status = "\'" + in.readLine() + "\'";
			String query = "Select * from appointment where adate = " + date + " and time_slot = " + timeSlot + " and status = " + status; 
			if (esql.executeQueryAndPrintResult(query) == 0) // nothing in return, appointment not in DB
			{
				query = "Select count(*) from Appointment"; 
				appid = esql.executeQueryAndReturnResult(query).get(0).get(0); 
				System.out.print("Adding New Appointment with AppID: " + appid + "\n"); 
				query = "Insert into Appointment Values (" + appid + ", " + date + ", " + timeSlot + ", " + status + ")"; 
				esql.executeUpdate(query);
			}
			else {
				System.out.print("Appintment Exists in DB. Ending...\n");
				return; 
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
		String query; 
		try {
			boolean addNewPatient = false; 
			String pid, docID, appID; 
			String name = "", gender = "", age = "", address = "", numberOfAppt = ""; 
			System.out.print("Enter the Patient ID: $ ");
			pid = in.readLine(); 
			// check if patient exists
			// patient not exist
			query = "Select * from Patient a where patient_ID = " + pid; 
			if (esql.executeQueryAndPrintResult(query) == 0) { // nothing in return, adding patient
				// add patient info
				System.out.print("Patient does not exist in DB, Adding new patient...\n"); 
				try {
				System.out.print("Enter the Name of the Patient: $ "); 
				name = "\'" + in.readLine() + "\'"; 
				System.out.print("Enter the Gender of the Patient (M/F): $ "); 
				gender = "\'" + in.readLine() + "\'"; 
				System.out.print("Enter the Age of the Patient: $ "); 
				age = in.readLine(); 
				System.out.print("Enter the Address of the Patient: $ "); 
				address = "\'" + in.readLine() + "\'"; 
				// default to 1, if appointment not available, decrease to 0
				numberOfAppt = "1"; 
				addNewPatient = true; 
				} catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
			else {
				System.out.print("Patient Exists... Continue...\n"); 
			}

			System.out.print("Enter the Doctor ID: $ "); 
			docID = in.readLine(); 
			// check if doctor exists
			query = "Select * from Doctor where doctor_ID = " + docID; 
			if (esql.executeQueryAndPrintResult(query) == 0) {
				System.err.print("Doctor does not exists!\n"); 
				return; 
			}

			System.out.print("Enter the Appointment ID: $ "); 
			appID = in.readLine(); 
			// check if appointment exists
			query = "Select * from Appointment where appnt_ID = " + appID; 
			if (esql.executeQueryAndPrintResult(query) == 0) {
				System.err.print("Appointment does not exists!\n");
				return; 
			}
			
			query = "Select status from Appointment where appnt_ID = " + appID;
			
			 
			String currStatus = esql.executeQueryAndReturnResult(query).get(0).get(0);
			if (currStatus.equals("PA") || currStatus.equals("WL"))
			{
				if (currStatus.equals("PA"))
					System.out.print("Appointment is Pasted. Unable to Make!\n"); 
				else 
					System.out.print("Appointment is WaitListed. Unable to Make!\n"); 		
				// unavailable, numofappt for patient is 0
				numberOfAppt = "0"; 
			}
			else if (currStatus.equals("AC"))
			{
				System.out.print("Appointment is Active. Adding to Waitlist!\n"); 
				// Update status of appointment
				query = "Update appointment set status = 'WL' where appnt_ID = " + appID; 
				esql.executeUpdate(query); 
				// Insert to has_appointment
				query = "Select * from has_appointment where appt_id = " + appID + " and doctor_id = " + docID; 
				if (esql.executeQueryAndPrintResult(query) == 0) // has_appointment does not have the appointment yet, Insert
				{
					query = "Insert into has_appointment (appt_id, doctor_id) values (" + appID + ", " + docID + ")"; 
					esql.executeUpdate(query); 
				}
				// Update patient #of appts if existing patient
				if (addNewPatient == false) {
					query = "Select number_of_appts from patient where patient_ID = " + pid; 
					numberOfAppt = esql.executeQueryAndReturnResult(query).get(0).get(0); 
					int intNumofAppts = Integer.parseInt(numberOfAppt) + 1; 
					numberOfAppt = String.valueOf(intNumofAppts); 
					query = "Update patient set number_of_appts = " + numberOfAppt + " where patient_ID = " + pid; 
					esql.executeUpdate(query); 
				}
				 
			}
			else if (currStatus.equals("AV"))
			{
				System.out.print("Appointment is Available. Adding to list!\n");
				query = "Update appointment set status = 'AC' where appnt_ID = " + appID; 
				esql.executeUpdate(query); 
				// Insert to has_appointment
				query = "Insert into has_appointment (appt_id, doctor_id) values (" + appID + ", " + docID + ")"; 
				esql.executeUpdate(query); 
				// Update patient #of appts if existing patient
				if (addNewPatient == false) {
					query = "Select number_of_appts from patient where patient_ID = " + pid; 
					numberOfAppt = esql.executeQueryAndReturnResult(query).get(0).get(0); 
					int intNumofAppts = Integer.parseInt(numberOfAppt) + 1; 
					numberOfAppt = String.valueOf(intNumofAppts); 
					query = "Update patient set number_of_appts = " + numberOfAppt + " where patient_ID = " + pid; 
					esql.executeUpdate(query); 
				}
			}
			else {
				System.out.print("Status: " + currStatus + " is Incorrect\n"); 
			}
			// appointment exists
			if (addNewPatient == true) {
				query = "Insert into Patient Values (" + pid + ", " + name + ", " + gender + ", " + age + ", " + address + ", " + numberOfAppt + ")"; 
				esql.executeUpdate(query);
			}
		} catch(Exception e) {
			System.err.println(e.getMessage()); 
		}
	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
		try{
			String docID, startDate, endDate;
			System.out.print("Enter the Doctor ID: $");
			docID = in.readLine();
			System.out.print("Enter the starting date: $");
			startDate = "'" + in.readLine() + "'";
			System.out.print("Enter the ending date: $");
			endDate = "'" + in.readLine() + "'";
			String query = "select a.* from Appointment a, Doctor d, has_appointment h where a.adate between " + startDate + " and " + endDate + " and d.doctor_ID = " + docID + " and d.doctor_ID = h.doctor_id and a.appnt_ID = h.appt_id and a.status in ('AC', 'AV')";
			if (esql.executeQueryAndPrintResult(query) == 0)
				System.out.print("No Active or Available Appointments Found\n");
		}catch (Exception e){
			System.err.println(e.getMessage()); 
		}
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
		try{
			String departmentName, date; 
			System.out.print("Enter the Department Name: $ "); 
			departmentName = "'" + in.readLine() + "'"; 
			System.out.print("Enter the Date you looking for: $ "); 
			date = "'" + in.readLine() + "'"; 
			String query = "Select a.* from appointment a, searches s, department d where d.name = " + departmentName + " and a.adate = " + date + " and a.status = 'AV' and a.appnt_ID = s.aid and s.hid = d.hid"; 
			if (esql.executeQueryAndPrintResult(query) == 0)
				System.out.print("No Available Appointment Found\n"); 
		}catch (Exception e){
			System.err.println(e.getMessage()); 
		}
	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
		try{
			String query = "select d.name, count(a.appnt_ID) as num_of_appointments, a. status from Doctor d, Appointment a, has_appointment h where d.doctor_ID = h.doctor_id and h.appt_id = a.appnt_ID group by d.name, a.status order by d.name, count(a.appnt_ID) desc"
			//String query = "select d.name, a.status, count(a.appnt_ID) as num_of_appointments from Doctor d, Appointment a, has_appointment h where d.doctor_ID = h.doctor_id and h.appt_id = a.appnt_ID group by d.name, a.status order by count(a.appnt_ID) desc";
			if (esql.executeQueryAndPrintResult(query) == 0)
				System.out.print("No Results Found\n");
		}catch (Exception e){
			System.err.println(e.getMessage()); 
		}
	}

	
	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
		try{
			String status; 
			System.out.print("Enter the status you looking for: $ "); 
			status = "'" + in.readLine() + "'"; 
			String query = "Select d.name, count(*) as num from appointment a, doctor d, has_appointment h where a.status = " + status + " and a.appnt_ID = h.appt_ID and h.doctor_id = d.doctor_ID group by d.name"; 
			if (esql.executeQueryAndPrintResult(query) == 0)
				System.out.print("No Result Found\n"); 
		}catch (Exception e){
			System.err.println(e.getMessage()); 
		}

	}
}
