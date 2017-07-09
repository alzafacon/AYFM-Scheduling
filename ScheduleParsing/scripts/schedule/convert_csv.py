
from schedule.Assignment import *

# prepared statement for assignment with only one participant (assignee)
prepInsertNoHHold = \
    """INSERT INTO assignment (`week`, assignee, lesson, `classroom`, `type`)
    SELECT '{a.date}', id, {a.lesson}, '{a.section}', {a.type}
    FROM person
    WHERE full_name = '{a.assignee}';"""

# prepated statement for assignment with two participants (assignee, householder)
prepInsertWithHHold = \
    """INSERT INTO assignment (`week`, assignee, householder, lesson, `classroom`, `type`)
    SELECT '{a.date}', publisher.id, hholder.id, {a.lesson}, '{a.section}', {a.type}
    FROM person as publisher
    JOIN person as hholder
    WHERE publisher.full_name = '{a.assignee}' and hholder.full_name = '{a.hholder}';"""


def to_sql(year, month):
    '''Convert csv to sql'''
    sqlStatements = []
    
    # open csv schedule for parsing (this path only works when script is called from convert.py)
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, 'r') as assignments:
        assgn = Assignment() # object used for holding an assignment
        
        # each line is an assignment
        for assignment in assignments:
            
            assgn.setFromCSV( assignment.strip() ) # strip newline stuff
            
            if assgn.lesson == '':
                assgn.lesson = 'null'
                
            # hhold will be automatically set to null, by ommision in prepInsertNoHHold
            
            if assgn.hholder == '': # there is only one participant
            
                stmt = prepInsertNoHHold.format(a = assgn)
                sqlStatements.append(stmt)
            
            else: # there is a householder
            
                stmt = prepInsertWithHHold.format(a = assgn)
                sqlStatements.append(stmt)
            

    sqlfilename = '../sql/%d-%d.sql' % (year, month)
    with open(sqlfilename, 'w') as assgn_sql_f:
        
        for line in sqlStatements:
            assgn_sql_f.write(line+'\n')

if __name__ == '__main__':
    print('running as main. now exiting.')
