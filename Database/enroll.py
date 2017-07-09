import argparse
import csv

# format strings for generating SQL
insert_person = 'INSERT IGNORE INTO person (full_name, gender, isactive) VALUES '
insert_person_value = "('{r[First Name]} {r[Last Name]}', '{r[Gender]}', {active})"

insert_works_on = '''INSERT IGNORE INTO works_on (person_id, type_id)
SELECT p.id, t.id
FROM
    person AS p
        JOIN
    (SELECT a.id FROM assignment_type AS a WHERE a.id IN ({types})) AS t
WHERE p.full_name = '{r[First Name]} {r[Last Name]}';'''

assignment_type = {'Reading':'1', 'Initial Call':'2', 'Return Visit':'3', 'Bible Study':'4'}

fieldnames = ['Gender', 'Last Name', 'First Name', 'Active', 'Reading', 'Initial Call', 'Return Visit', 'Bible Study']

def enrollmentCSV_to_SQL(enrollment):

    persons = []
    works_on_scripts = []
    
    with open(enrollment, 'r') as csvFile:

        reader = csv.DictReader(csvFile, fieldnames=fieldnames)
        
        for record in reader:
            active = record['Active'] != 'f'
            persons.append( insert_person_value.format(r = record,active = active) )
            
            works_on = []
            
            for atype in assignment_type.keys():
                
                if record[atype] != '':
                    works_on.append( assignment_type[atype] )

            works_on_scripts.append(
                insert_works_on.format(types=', '.join(works_on), r=record))
    
    with open('populatePersonAndWorksOn.sql', encoding='utf-8', mode='w') as populateTables:

        populateTables.write(insert_person+'\n')
        populateTables.write(', \n'.join(persons)+';\n\n')
        
        for script in works_on_scripts:
            populateTables.write(script+'\n\n')
            
            
if __name__ == '__main__':
    
    parser = argparse.ArgumentParser(
        description='Generate SQL for populating `person` and `works_on` tables.')
    parser.add_argument('file', 
        help='''CSV file to generate SQL. Columns for CSV are: {}'''.format(fieldnames))
    
    args = parser.parse_args()
    
    if (args.file):
        enrollmentCSV_to_SQL(args.file)
    
    
