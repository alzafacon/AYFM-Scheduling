import argparse

import schedule.convert_docx
import schedule.convert_csv


if __name__ == '__main__':

    parser = argparse.ArgumentParser(
        description='Convert schedule file from DOCX to CSV (output to ../csv/).')
        
    parser.add_argument('-y', '--year', required=True, type=int,
        help='Year of the DOCX schedule.')
    parser.add_argument('-m', '--month', required=True, type=int,
        help='Month of the DOCX schedule.')
    parser.add_argument('file',
        help='DOCX schedule template to read assignments from.')
    parser.add_argument('--sql', action='store_true',
        help='Convert to SQL also')
        
    args = parser.parse_args()
    
    schedule.convert_docx.to_csv(args.file, args.year, args.month)
    
    if args.sql:
        schedule.convert_csv.to_sql(args.year, args.month)