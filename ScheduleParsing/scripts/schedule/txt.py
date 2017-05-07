
import datetime

spanishMonths = {'enero':1, 'febrero':2, 'marzo':3, 'abril':4, 'mayo':5,
                 'junio':6, 'julio':7, 'agosto':8, 'septiembre':9,
                 'octubre':10, 'noviembre':11, 'diciembre':12}

spanishType = {'lectura':1, 'primer visita':2, 'primera visita':2, 'primera conversacion':2,
               'revisita':3, 'curso biblico':4}

sectionName = {1:'a', 2:'b', 3:'c'}


#TODO: need to be more consistent with the types passed in
def writeCSVto(parsed, date, assgnType, assignee, hholder, lesson, section):
    csvLine = ','.join([date, str(assgnType), assignee, hholder,  str(lesson), section])
    parsed.write(csvLine+'\n')
    print(csvLine)


def to_csv(year, month):
    
    day = 0

    date = ''
    assgnType = 0
    assignee = ''
    hholder = ''
    lesson = 0
    section = 0
    
    stream = ''

    txtfilename = '../txt/%d-%d.txt' % (year, month)
    with open(txtfilename, 'r') as schedule:
        
        buf = schedule.read()
        while buf != '':
            stream += buf
            buf = schedule.read()

    stream = stream.replace('\n', ' \t') #makes parsing easier (at least a little bit) (notice the space before the tab)

    csvfilename = '../csv/%d-%d.csv' % (year, month)
    with open(csvfilename, 'w') as parsed:
        
        lookingFor_date = True
        lookingFor_type = True
        lookingFor_assignee = True

        while len(stream) > 0:

            #look for date first
            if lookingFor_date:
                fp = 0  #front string pointer (index to items)
                while fp < len(stream) and stream[fp].isnumeric() == False:
                    fp += 1

                stream = stream[fp : ] #'truncate' as you go for convinience

                #date located!
                day = int(stream[ : stream.find(' ')])

                #remove day
                stream = stream[stream.find(' ')+1 : ]
                #remove ' de '
                stream = stream[stream.find(' ')+1 : ]

                #look for month (which will be folowed by whitespace)
                fp = 0
                while fp < len(stream) and stream[fp].isspace() == False:   # .isspace() checks for any whitespace
                    fp += 1

                month = spanishMonths[ stream[ : fp].lower() ]

                d = datetime.date(year, month, day)
                date = '{:%Y-%m-%d}'.format(d)  #will be used to prepend all strings writen to file
                lookingFor_date = False

                stream = stream[fp : ]  #drop month

                #if there is a special message then remove the message
                #there is always a tab after the date (there may be unexpected spaces entered by user)
                stream = stream[stream.find('\t') : ]
                #find the end of the line and start inspecting from there
                stream = stream[stream.find(' \t') : ].lstrip() #.lstrip() #it is polite to leave whitespace at the end of stream (helps handle end of file)

                if len(stream) == 0 or stream[0].isnumeric():   # if we are looking at a date the week with the message was canceled
                    lookingFor_date = True
                    continue

            #look for type next
            if lookingFor_type:
                while stream[ : stream.find('\t')].strip().lower() not in spanishType:  #strip bc >=type2 can have space character before the tab
                    stream = stream[stream.find('\t')+1 : ]
                assgnType = spanishType[ stream[ : stream.find('\t')].strip().lower() ]
                lookingFor_type = False

                stream = stream[stream.find('\t')+1 : ].lstrip()

            if lookingFor_assignee: #look for a name next
                assignee = stream[ : stream.find('\t')].strip()
                lookingFor_assignee = False

                stream = stream[stream.find('\t')+1 : ].lstrip()

            #The next item is either a lesson or a hholder (depending on the type of assgn)

            #look for a lesson next
            if assgnType == 1:  #reading
                lesson = stream[ : stream.find('\t')].strip()
                section += 1

                writeCSVto(parsed, date, assgnType, assignee, '', lesson, sectionName[section])

                stream = stream[stream.find('\t')+1 : ].lstrip()

            #next item is a hholder
            if assgnType > 1:
                hholder = stream[ : stream.find('\t')].strip() #maybe strip is redundant

                stream = stream[stream.find('\t')+1 : ]

                #now find a lesson; lesson may be null
                if stream[0].isspace():
                    lesson = ''
                else:
                    lesson = stream[ : stream.find('\t')].strip()

                section += 1

                writeCSVto(parsed, date, assgnType, assignee, hholder, lesson, sectionName[section])

                stream = stream[stream.find('\t')+1 : ].lstrip() #no strip, leave space to indicate whether there is a second section assgn

            if len(stream) == 0:    #TODO need to handle end of file better than this
                continue

            if stream[0].isnumeric():  #there is not a second section; next is a date
                lookingFor_date = True
                lookingFor_type = True
                lookingFor_assignee = True
            elif stream[ : stream.find('\t')].strip().lower() in spanishType:
                lookingFor_date = False
                lookingFor_type = True
                lookingFor_assignee = True
            else:   #there is a second
                lookingFor_date = False    #current date is still valid
                lookingFor_type = False    #current type id still valid
                lookingFor_assignee = True
                continue    #there is a second section

            section = 0

if __name__ == '__main__':
    year = int(input('year: '))
    month = int(input('month: '))

    schedTXT_to_CSV(year, month)
    
        
