


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

if __name__ == '__main__':
    print('run as main. doing nothing.')
