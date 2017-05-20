#https://python-docx.readthedocs.io/en/latest/user/install.html

#python-docx may be installed with pip if you have it available:

#pip install python-docx

#python-docx can also be installed using easy_install, although this is #discouraged:

#easy_install python-docx

#If neither pip nor easy_install is available, it can be installed manually by downloading the distribution from PyPI, unpacking the tarball, and running setup.py:

#tar xvzf python-docx-{version}.tar.gz
#cd python-docx-{version}
#python setup.py install

#python-docx depends on the lxml package. Both pip and easy_install will take care of satisfying those dependencies for you, but if you use this last method you will need to install those yourself.

from docx import Document

class Assignment:
    '''Class for managing assignments'''
    def __init__(self):
        '''each assignment in csv contains the following fields'''
        self.date = ''
        self.type = ''
        self.assignee = ''
        self.hholder = ''
        self.lesson = ''
        self.section = ''
        
    def makeCSV(self):
        return ','.join([self.date, self.type, self.assignee, self.hholder, self.lesson, self.section])
    
    def clear(self):
        self.date = ''
        self.type = ''
        self.assignee = ''
        self.hholder = ''
        self.lesson = ''
        self.section = ''

READING = '1'
INIT_CALL = '2'
RET_VISIT = '3'
BIB_STUDY = '4' #this may also happen to be a talk by a brother

SECTION_A = 'a'
SECTION_B = 'b'

parsingDir = r'C:\Users\FidelCoria\git\AYFM-Scheduling\ScheduleParsing\\'
txtDir = parsingDir + r'TXT Schedules\\'

def to_txt(path, year, month):
    
    #docxsched = Document(docxDir + docxfilename)
    docxsched = Document(path)
    
    #find tables in the doc
    tables = docxsched.tables

    if len(tables) != 1:
        #throw a fit!
        print('uh oh, there should be exactly one table in the document')
        
    #select the first table
    table = tables[0]

    #save table as formatted string
    # from cell to cell there is a tab,
    # from row to row there is a newline.

    txtsched = ''

    for row in table.rows:
        for colindex in range(len(row.cells)):
            txtsched += row.cells[colindex].text + '\t'
        txtsched += '\n'

    txtfilename = '%d-%d.txt' % (year, month)
    with open(txtDir + txtfilename, 'w') as txt_f:
        txt_f.write(txtsched)


def to_csv(path, year, month):
    #docxsched = Document(docxDir + docxfilename)
    docxsched = Document(path)
    
    #find tables in the doc
    tables = docxsched.tables

    if len(tables) != 1:
        #throw a fit!
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

    csvSched = ''
    
    a = Assignment()
    
    rows = table.rows
    r_ix = 1 # row index #skipping the first row (header)
    
    # The first week of every month is different (has only 1 assgn):
    raw_date = rows[r_ix].cells[0].text
    # convert to nice date format
    day = raw_date[ : raw_date.find(' ')] # extract day of the month
    date = '{:%Y-%m-%d}'.format( datetime.date(int(year), int(month), int(day)) )
    
    #advance to only part for first week (Reading)
    r_ix += 1
    
    a.type = READING
    a.section = SECTION_A
    a.assignee = rows[r_ix].cells[1].text
    a.lesson = rows[r_ix].cells[2].text
    
    print('name in first week', a.assignee)
    
    if a.assignee != '':
        csvSched += a.makeCSV()
        csvSched += '\n'
        
    r_ix += 1 # move to next week
    
    # Now continue with the remaining 4 weeks
    a.clear()
    for week in range(4):
        # Extract date for this week
        raw_date = rows[r_ix].cells[0].text
        # convert to nice date format
        day = raw_date[ : raw_date.find(' ')] # extract day of the month
        date = '{:%Y-%m-%d}'.format( datetime.date(int(year), int(month), int(day)) )
        
        #TODO: go through the four assignments and both sections
        
    
        
if __name__ == '__main__':
    print('run as main. doing nothing.')
