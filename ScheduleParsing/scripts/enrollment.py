

prepIns_person = "INSERT IGNORE INTO person (full_name, gender, isactive) VALUES ('%s', '%s', %s);"
#always end with a semicolon
prepIns_workson = "INSERT IGNORE INTO works_on (person_id, assignment_id) VALUES "
value = '(LAST_INSERT_ID(), %d)'

class Student:
    
    def __init__(self, csv):
        #indecies for the csv split (column numbers)
        GENDER = 0
        LNAME = 1
        FNAME = 2
        IS_ACTIVE = 3
        
        #Assignment Types
        READING = 4
        INIT_CALL = 5
        RETN_VIST = 6
        BIBL_STDY = 7
        
        studentRecord = csv.split(',')
        
        self.gender = studentRecord[GENDER]
        self.fullName = studentRecord[FNAME]+' '+studentRecord[LNAME]
        
        # setting isactive to true is favored in the case of unknown input
        if studentRecord[IS_ACTIVE] == 'f':
            self.isactive = 'FALSE'
        else:
            #print('Unknown value for "isactive" column. Setting to TRUE.')
            self.isactive = 'TRUE'
        
        self.eligibleType = ['Type Numbers: 1-4']
        
        for type in range (READING, BIBL_STDY+1): # plus one so that BIBL_STDY is also included
            if studentRecord[type] != '':
                self.eligibleType.append( True )
            else:
                self.eligibleType.append( False )
            
def enrollmentCSV_to_SQL():
    insertSQL = []
    absolutePath = r'C:\Users\FidelCoria\git\AYFM-Scheduling\Apply Yourself to the Field Ministry\enrollment\\'
    with open(absolutePath + 'enrollment.csv', 'r') as enrolled:

        for record in enrolled:

            record = record.strip() #remove white space from the front and back (new line)

            #line may be empty after removing whitespace
            if record == '':
                continue

            student = Student(record)

            insertSQL.append( prepIns_person % (student.fullName, student.gender, student.isactive) )
            
            works_on = []
            
            # assignment types are numbered 1 through 4
            for typeNum in range(1, 5):
                if student.eligibleType[typeNum] == True:
                     works_on.append( value % typeNum ) 
            
            insertSQL.append(prepIns_workson + ', '.join(works_on) + ';')
    
    with open('../sql/populatePerson+WorksOn.sql', 'w') as populateTables:

        for line in insertSQL:
            populateTables.write(line+'\n');

            
if __name__ == '__main__':
    enrollmentCSV_to_SQL()
