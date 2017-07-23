#https://python-docx.readthedocs.io/en/latest/user/install.html

#python-docx may be installed with pip if you have it available:
# pip install python-docx

#python-docx can also be installed using easy_install, although this is #discouraged:
# easy_install python-docx

#If neither pip nor easy_install is available, it can be installed manually by downloading the distribution from PyPI, unpacking the tarball, and running setup.py:
# tar xvzf python-docx-{version}.tar.gz
# cd python-docx-{version}
# python setup.py install

#python-docx depends on the lxml package. Both pip and easy_install will take care of satisfying those dependencies for you, but if you use this last method you will need to install those yourself.

from docx import Document
import datetime

from schedule.Assignment import *

#indices for row cells in the template table
DATE = 0
SECTION_A_PARTICIPANTS = 1
SECTION_A_LESSON = 2
SECTION_B_PARTICIPANTS = 3
SECTION_B_LESSON = 4

# csv column header
HEADER = 'Date,Type,Assignee,Householder,Lesson,Classroom'

def getWeekDate(weekHeaderRow, year, month):
    '''extract the date from the date row from table'''
    raw_date = weekHeaderRow.cells[DATE].text.strip()
    
    if raw_date == '':
        return raw_date
    
    # convert to nice date format
    
    # split up to look for a number for the day of the month
    date_parts = raw_date.split()
    # list comprehension to select numbeic strings
    numericParts = [part for part in date_parts if part.isnumeric()]
    
    # assume there will only be one
    day = int( numericParts[0] )
    # this is the format for MySQL date
    date = '{:%Y-%m-%d}'.format( datetime.date(year, month, day) )
    
    return date
    
def parseAssignmentRow(row, date, aType):
    '''parse an assignment row from table, returns an array with the assignments'''
    assignments = []
    
    # participants for first assgn
    participants = row.cells[SECTION_A_PARTICIPANTS].text.strip() 
    
    if participants != '': # if there are participants
        assgn = Assignment() # new empty assignment
        
        # begin populating fields
        assgn.date = date
        assgn.type = aType
        assgn.lesson = row.cells[SECTION_A_LESSON].text.strip()
        assgn.section = SECTION_A
        
        # be sure to strip each element of the array `students` in case split leaves white space
        students = participants.split('\n') # assume at most 2 elements, and at least one
        
        # the assignee should come 1st
        assgn.assignee = students[0].strip()
        if len(students) > 1: # the householder second
            # '> 1' in case there is an additinal helper (will be ignored)
            assgn.hholder = students[1].strip()
            
        assignments.append( assgn )
        
    # the same for the second 
    participants = row.cells[SECTION_B_PARTICIPANTS].text.strip() # participants for second assgn
    if participants != '':
        assgn = Assignment() # new empty assignment
        
        assgn.date = date
        assgn.type = aType
        assgn.lesson = row.cells[SECTION_B_LESSON].text.strip()
        assgn.section = SECTION_B
        
        students = participants.split('\n')
        
        assgn.assignee = students[0].strip()
        if len(students) > 1:
            assgn.hholder = students[1].strip()
        
        assignments.append( assgn )
    
    return assignments

def to_csv(path, year, month):
    '''path to docx file, year and month as int, will convert into a csv file'''
    
    docxsched = Document(path)
    
    #find tables in the doc
    tables = docxsched.tables

    if len(tables) != 1:
        #should be raising an exception...
        print('uh oh, there should be exactly one table in the document')
        
    #select the first table
    table = tables[0]
    
    # Assume everything else is as expected
    
    #there are 5 weeks for every schedule
    # pick up the date 
    # then the type 
    # if a name and a lesson are found write the csv String
    # if only a name is found write the csv string
    # if no name is found continue looking for date or type (which ever appears first)
    
    csvSched = [] #this is an array of strings for the csv file
    
    row_iter = iter(table.rows[1:]) #skipping the first row (header)
    row = next(row_iter)
    
    # The first week of every month is different (has only 1 assgn)
    
    date = getWeekDate(row, year, month)
    
    #advance to the only participation for first week (Reading)
    row = next(row_iter)
    
    assgnRow = parseAssignmentRow(row, date, READING)
    
    for assgn in assgnRow:
        csvSched.append(assgn.makeCSV() + '\n')
        
        
    # Now continue with the remaining 4 weeks
    for week in range(4):
        row = next(row_iter)
        
        # Extract date for this week
        date = getWeekDate(row, year, month)
        
        if date == '': # no date will be available for assignments
            continue
            
        for assgnType in TYPES:
            row = next(row_iter)
            assgnRow = parseAssignmentRow(row, date, assgnType)
            
            for assgn in assgnRow:
                # print(assgn)
                csvSched.append( assgn.makeCSV() + '\n' )
                
    #path will only work when called from main.py      
    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, encoding='utf-8', mode='w') as parsed:
        parsed.write(HEADER+'\n')
        for line in csvSched:
            parsed.write(line)
    
if __name__ == '__main__':
    print('Running as main. Doing nothing.')
