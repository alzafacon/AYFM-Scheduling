import argparse
import csv

# format strings for generating SQL
insert_person = '''INSERT IGNORE INTO person 
(first_name, last_name, gender, is_active, \
is_eligible_reading, is_eligible_init_call, is_eligible_ret_visit, \
is_eligible_bib_study, is_eligible_talk) 
VALUES '''
insert_person_value = """('{r[First Name]}', '{r[Last Name]}', '{r[Gender]}', {r[Active]}, \
{r[Reading]}, {r[Initial Call]}, {r[Return Visit]}, \
{r[Bible Study]}, {r[Talk]})"""

fieldnames = ['Gender', 'Last Name', 'First Name', 'Active',
    'Reading', 'Initial Call', 'Return Visit', 'Bible Study', 'Talk']

def column_to_bool(column, default_to_true=True, false_value='f', true_value='t'):
    '''Converts a column value into a python boolean value.
    
    Args:
        column (str): column value to be converted
        default_to_true (bool): Always returns True unless false_value explicitly matched
        false_value (str): value to strictly match for a False return on default_to_true=True
        true_value (str): value to strictly match for a True return on default_to_true=False
        
    Returns:
        bool: converted column value
    '''
    
    if default_to_true == True:
        if column == false_value:
            return False
        else:
            return True
    else:
        if column == true_value:
            return True
        else:
            return False
        
def enrollmentCSV_to_SQL(enrollment):
    '''
    '''

    persons = []
    
    with open(enrollment, 'r', newline='') as csvFile:

        # reader = csv.DictReader(csvFile, fieldnames=fieldnames)
        reader = csv.DictReader(csvFile)
    
        for record in reader:
            record['Active'] = column_to_bool(record['Active'])
            record['Reading'] = column_to_bool(record['Reading'], false_value='')
            record['Initial Call'] = column_to_bool(record['Initial Call'], false_value='')
            record['Return Visit'] = column_to_bool(record['Return Visit'], false_value='')
            record['Bible Study'] = column_to_bool(record['Bible Study'], false_value='')
            record['Talk'] = column_to_bool(record['Talk'], false_value='')
            
            persons.append( insert_person_value.format(r = record) )

    
    with open('populatePerson.sql', encoding='utf-8', mode='w') as populateTables:

        populateTables.write(insert_person+'\n')
        populateTables.write(',\n'.join(persons)+'; ')
            
            
if __name__ == '__main__':
    
    parser = argparse.ArgumentParser(
        description='Generate SQL for populating `person` and `works_on` tables.')
    parser.add_argument('file', 
        help='''CSV file to generate SQL. Columns for CSV are: {}'''.format(fieldnames))
    
    args = parser.parse_args()
    
    if (args.file):
        enrollmentCSV_to_SQL(args.file)
    
    
