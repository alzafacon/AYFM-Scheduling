
#this script should not be writing back to enrollment.csv
# in fact, because there is not access to the data base
#  this script should not do any checking against enrollment.csv
#  because of the possibility that it is out of sync with the database
#  thus causing a 'data silo' (I think that is the term)
class Assignment:

    def __init__(self, csv):
        #INDECIES FOR THE SPLIT CSV LINE
        DATE = 0
        TYPE = 1
        NAME = 2
        HHLD = 3
        LSSN = 4
        SECT = 5
        
        record = csv.split(',')
        
        self.date = record[DATE]
        self.type = record[TYPE]
        self.name = record[NAME]
        self.householder = record[HHLD]
        self.lesson = record[LSSN]
        self.section = record[SECT]

#names = {}
#print('names set to {}')
        
prepInsert = """INSERT INTO assignment (date_assgn, assignee, lesson, section, assgn_type)
    SELECT '%s', id_person, %s, '%s', %s
    FROM person
    WHERE full_name = '%s';"""

prepUpdate = """UPDATE assignment
    SET householder = (SELECT id_person FROM person WHERE full_name = '%s')
    WHERE date_assgn = '%s' and assignee = (SELECT id_person FROM person WHERE full_name = '%s');"""


def makeCSV(name, info):

    namedelim = name.find(' ') # find first space (not correct when there is a middle name)

    gender = info[0]
    fname = name[ : namedelim]
    lname = name[namedelim+1 : ]
    active = info[1]

    reading = info[2]
    initial = info[3]
    returnv = info[4]
    bible   = info[5]

    csv = ','.join([gender, lname, fname, active,
                    reading, initial, returnv, bible])
    return csv

def makeDictEntry(csvLine):
    record = csvLine.split(',')

    if len(record) != 8:
        print('Bad record: '+csvLine)
        return None, None

    name = record[2] + ' ' + record[1]

    info = [record[0], record[3], record[4], record[5], record[6], record[7]]

    return name, info

# read csv into the %names% dictionary
def reloadEnrollment():

    names = {} # clear names dictionary
    
    with open('../data/enrollment/enrollment.csv', 'r') as enrollment:
        print('reloading enrollent')
        for line in enrollment:
            line = line.strip()

            if line == '':
                print('blank enrollment line')
                continue

            name, info = makeDictEntry(line)

            if name not in names:
                names[name] = info
            else:
                print(name+' is duplicated in csv')
        print(names)
        return names

#updateEnrollment = True

def to_sql(year, month):
    
    #names = reloadEnrollment()
    
    #updateEnrollment = False
    #print('name dictonary may not be available outside of this scope')
    #print('names: ')
    #print(names)
    sqlStatements = []
    
    # open csv schedule for parsing
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, 'r') as csv_f:

        for line in csv_f:
            attrb = line.strip().split(',')
            
            assgn = Assignment( line.strip() )
            
            #if assgn.name not in names.keys():
            #    print(line.strip())
            #    print(assgn.name+' may be misspelled or not in the enrollment list')
            #    print('you may insert a row into the list or correct the schedule\n')
            #    continue
            #else:
                #check if the person is allowed to give this type of assgn
            #    if assgn.type not in names[attrb[NAME]]:
            #        print(line.strip())
            #        print(assgn.type+' is not enrolled to give this type of assgn')
            #        request = input('update records(y/n)? ')
            #        if request == 'y':
            #            print('automatically udpating records\n')
            #            names[assgn.name][ int(assgn.type) + 1 ] = assgn.type
            #            updateEnrollment = True
                    
                
            #if assgn.householder != '' and assgn.householder not in names.keys():
            #    print(line)
            #    print(assgn.householder+' may be misspelled or not in the enrollment list')
            #    print('you may correct the schedule or insert a row into the list\n')
            #    continue
            
            if assgn.lesson == '':
                assgn.lesson = 'null'
            
            stmt = prepInsert % (assgn.date, assgn.lesson, assgn.section, assgn.type, assgn.name)

            sqlStatements.append(stmt)
            
            if assgn.householder != '':
                upStmt = prepUpdate % (assgn.householder, assgn.date, assgn.name)
                sqlStatements.append(upStmt)

    sqlfilename = '../sql/%d-%d.sql' % (year, month)
    with open(sqlfilename, 'w') as assgn_sql_f:
        
        for line in sqlStatements:
            assgn_sql_f.write(line+'\n')

    #if updateEnrollment == True:

        #records = []
        #for name, info in names.items():
        #    records.append( makeCSV(name, info) )

        #records = sorted(set(records))  #this sorting compares the commas too, not exactly what I want... close enough
        
        #with open('../data/enrollment/enrollment.csv', 'w') as enrollment:
        #    for rec in records:
        #        enrollment.write(rec+'\n')

if __name__ == '__main__':
    print('running as main. now exiting.')
