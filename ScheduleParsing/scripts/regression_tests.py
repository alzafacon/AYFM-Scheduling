
import main
#import schedule.convert_docx
#import schedule.convert_csv

with open('schedules.txt', 'r') as schedules:
    
    for schedule in schedules:
        schedInfo = schedule.split(',')
        path = schedInfo[0]
        year = int( schedInfo[1] )
        month = int( schedInfo[2] )
        
        try:
            print('\n\nconverting ', year, month)
            main.docx_to_sql(path, year, month)
        except OSError as err:
            print(err)