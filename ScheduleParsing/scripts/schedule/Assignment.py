
#Assignment Types
READING = '1'
INIT_CALL = '2'
RET_VISIT = '3'
BIB_STUDY = '4' #this may also happen to be a talk by a brother
TYPES = (READING, INIT_CALL, RET_VISIT, BIB_STUDY)

# The Classrooms
SECTION_A = '1'
SECTION_B = '2'
CLASSROOMS = (SECTION_A, SECTION_B)

class Assignment:
    '''Class for managing assignments'''
    def __init__(self):
        '''each assignment contains the following fields. init all as empty str'''
        self.date = ''
        self.type = ''
        self.assignee = ''
        self.hholder = ''
        self.lesson = ''
        self.section = ''

    def setFromCSV(self, csv):
        '''use csv row to populate attrbs'''
        record = csv.split(',')

        #INDECIES FOR THE SPLIT CSV LINE
        DATE = 0
        TYPE = 1
        NAME = 2
        HHLD = 3
        LSSN = 4
        SECT = 5

        self.date = record[DATE]
        self.type = record[TYPE]
        self.assignee = record[NAME]
        self.hholder = record[HHLD]
        self.lesson = record[LSSN]
        self.section = record[SECT]


    def __str__(self):
        return self.makeCSV()

    def makeCSV(self):
        return ','.join([self.date, self.type, self.assignee, self.hholder, self.lesson, self.section])

    def clear(self):
        self.date = ''
        self.type = ''
        self.assignee = ''
        self.hholder = ''
        self.lesson = ''
        self.section = ''
