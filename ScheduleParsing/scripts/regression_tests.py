

import schedule.convert_docx
import schedule.convert_csv

with open('schedules.txt', 'r') as schedules:
    
    for sched in schedules:
        schedInfo = sched.split(',')
        path = schedInfo[0]
        year = int( schedInfo[1] )
        month = int( schedInfo[2] )
        
        try:
            print('\n\nconverting ', year, month)
            schedule.convert_docx.to_csv(path, year, month)
            schedule.convert_csv.to_sql(year, month)
        except OSError as err:
            print(err)
