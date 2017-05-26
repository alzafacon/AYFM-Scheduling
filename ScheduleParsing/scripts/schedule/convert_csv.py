
#INDECIES FOR THE SPLIT CSV LINE
DATE = 0
TYPE = 1
NAME = 2
HHLD = 3
LSSN = 4
SECT = 5

class Assignment:

    def __init__(self, csv):
        
        record = csv.split(',')
        
        self.date = record[DATE]
        self.type = record[TYPE]
        self.name = record[NAME]
        self.householder = record[HHLD]
        self.lesson = record[LSSN]
        self.section = record[SECT]
        
        
prepInsert = """INSERT INTO assignment (date_assgn, assignee, lesson, `section`, assgn_type)
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


def to_sql(year, month):
    
    sqlStatements = []
    
    # open csv schedule for parsing (this path only works when script is called from main.py)
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, 'r') as assignments:

        for assignment in assignments:
            
            assgn = Assignment( assignment.strip() )
            
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

if __name__ == '__main__':
    print('running as main. now exiting.')
