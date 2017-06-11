# for a command line feel run this script as __main__
import schedule.convert_docx
import schedule.convert_csv

def docx_to_sql(path, year, month):
    
    print('Converting docx to csv.\n')
    schedule.convert_docx.to_csv(path, year, month)
    
    print('Writing sql insert statment script.')
    schedule.convert_csv.to_sql(year, month)

def dateInput():

    validYearInput = False
    validMonthInput = False

    while validYearInput == False:
        try:
            year = int(input('year: '))
            
            validYearInput = True
        except ValueError:
             print('Please enter an integer for the year.')

    while validMonthInput == False:
        try:
            month = int(input('month: '))

            validMonthInput = True
        except ValueError:
            print('Please enter an integer for the month.')

    return (year, month)

if __name__ == '__main__':

    selection = '0'
    
    while selection != '2':

        print('\n\nMENU')
        print('1) schedules -> sql statements')
        print('2) exit\n')

        selection = input('action: ')
        print('') #newline
        
        if selection == '1':
            year, month = dateInput()

            path = input('path to docx schedule: ')
            
            try:
                with open(path) as trialOpen:
                    trialOpen.close()
                
            except OSError as err:
                print(err)
            else:
                 docx_to_sql(path, year, month)
                 
        elif selection == '2':
            print('Exiting...')
            break;
        else:
            print('Try a different option.')

        input('enter to continue...')
    
    print('leaving main.py')