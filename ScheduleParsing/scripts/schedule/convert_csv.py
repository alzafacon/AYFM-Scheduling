
from schedule.Assignment import *
        
        
prepInsertNoHHold = """INSERT INTO assignment (date_assgn, assignee, lesson, `section`, assgn_type)
    SELECT '{a.date}', id_person, {a.lesson}, '{a.section}', {a.type}
    FROM person
    WHERE full_name = '{a.assignee}';"""

    
prepInsertWithHHold = """INSERT INTO assignment (date_assgn, assignee, householder, lesson, `section`, assgn_type)
    SELECT '{a.date}', publisher.id_person, hholder.id_person, {a.lesson}, '{a.section}', {a.type}
    FROM person as publisher
    JOIN person as hholder
    WHERE publisher.full_name = '{a.assignee}' and hholder.full_name = '{a.hholder}'"""

prepUpdate = """UPDATE assignment
    SET householder = (SELECT id_person FROM person WHERE full_name = '%s')
    WHERE date_assgn = '%s' and assignee = (SELECT id_person FROM person WHERE full_name = '%s');"""

def to_sql(year, month):
    
    sqlStatements = []
    
    # open csv schedule for parsing (this path only works when script is called from main.py)
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, 'r') as assignments:
        assgn = Assignment() # object used for holding an assignment
        
        for assignment in assignments:
            
            assgn.setFromCSV( assignment.strip() )
            
            if assgn.lesson == '':
                assgn.lesson = 'null'
                
            # hhold will be automatically set to null by ommision in prepInsertNoHHold
            
            if assgn.hholder == '':
                stmt = prepInsertNoHHold.format(a = assgn)
                sqlStatements.append(stmt)
            
            else:
                stmt = prepInsertWithHHold.format(a = assgn)
                sqlStatements.append(stmt)
            

    sqlfilename = '../sql/%d-%d.sql' % (year, month)
    with open(sqlfilename, 'w') as assgn_sql_f:
        
        for line in sqlStatements:
            assgn_sql_f.write(line+'\n')

if __name__ == '__main__':
    print('running as main. now exiting.')
