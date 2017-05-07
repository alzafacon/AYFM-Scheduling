
#TODO the path will need to change or have some way of being specified by the user

from unidecode import unidecode
#Installation
# To install the latest version of Unidecode from the Python package index, use these commands:
#   $ pip install unidecode 
# To install Unidecode from the source distribution and run unit tests, use:
#   $ python setup.py install
#   $ python setup.py test


#remove the coments to actually remove the accents
def removeDiacritics(year, month):

    txtfilename = '../TXT Schedules/%d-%d.txt' % (year, month)
    with open(txtfilename, 'r+') as sched_f:

        data = ''

        buf = sched_f.read()
        while buf != '':
            data += buf
            buf = sched_f.read()

        temp = unidecode(data)

        temp.encode('ascii') # eventually a move to UTF-8 will be appropriate

        if temp != data:    #just to see if there is any files with accents
            print('%d has accents to be removed.\n' % (month))

            sched_f.seek(0) #if the file shrinks for some reason... there could be left over bytes from the original file
            sched_f.write(temp)
        else:
            print('No accents to remove.\n')

if __name__ == '__main__':
    remove = input('Would you like to remove diacritics? ')
    
    if remove in ['y', 'yes', 1]:
        year = int(input('year? '))
        month = int(input('month? '))
        
        removeDiacritics(year, month)
    
    print('Goodbye.')
