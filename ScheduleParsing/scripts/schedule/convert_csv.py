
from schedule.Assignment import *

# prepared statement for assignment with only one participant (assignee)
prepInsertNoHHold = \
    """INSERT INTO assignment (`week`, assignee, lesson, `classroom`, `type`)
    SELECT '{a.date}', p.id, {a.lesson}, {a.section}, {a.type}
    FROM person p
    WHERE CONCAT(p.first_name, ' ', p.last_name) = '{a.assignee}';"""

# prepated statement for assignment with two participants (assignee, householder)
prepInsertWithHHold = \
    """INSERT INTO assignment (`week`, assignee, householder, lesson, `classroom`, `type`)
    SELECT '{a.date}', p.id, h.id, {a.lesson}, {a.section}, {a.type}
    FROM person as p
    JOIN person as h
    WHERE CONCAT(p.first_name, ' ', p.last_name) = '{a.assignee}'
    and CONCAT(h.first_name, ' ', h.last_name) = '{a.hholder}';"""


def to_sql(year, month):
    '''Convert csv to sql'''
    sql_statements = []

    # open csv schedule for parsing (this path only works when script is called from convert.py)
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, encoding='utf-8', mode='r') as assignments:
        assgn = Assignment() # object used for holding an assignment
        assignments.__next__() # skip the column headers
        # each line is an assignment
        for assignment in assignments:

            assgn.setFromCSV(assignment.strip()) # strip newline stuff

            if assgn.lesson == '':
                assgn.lesson = 0

            # hhold will be automatically set to null, by ommision in prepInsertNoHHold

            if assgn.hholder == '': # there is only one participant

                stmt = prepInsertNoHHold.format(a=assgn)
                sql_statements.append(stmt)

            else: # there is a householder

                stmt = prepInsertWithHHold.format(a=assgn)
                sql_statements.append(stmt)


    sqlfilename = '../sql/%d-%d.sql' % (year, month)
    with open(sqlfilename, encoding='utf-8', mode='w') as assgn_sql_f:

        for line in sql_statements:
            assgn_sql_f.write(line+'\n')

if __name__ == '__main__':
    print('running as main. now exiting.')
