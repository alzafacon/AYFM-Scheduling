using System;
using System.Collections.Generic;

using iText.Forms;
using iText.Forms.Fields;
using iText.Kernel.Pdf;

using Microsoft.Office.Interop.Word;
using System.Text;

namespace ReminderSlipPopulator
{
    class FormFill
    {
        // pdf form template
        public const string PDF_1_ASSGN_SLIP = "S-89-S.pdf"; // Spanish, 1 Reminder slip for the first week of every month
        public const string PDF_8_ASSGN_SLIPS = "S-89-S 8.pdf"; // Spanish, 8 reminder slips for 8 assignments in one week

        // The following values are for concatinating to make the keys to the form fields
        public const string ASSIGNEE = "Assignee";
        public const string HOUSEHOLDER = "Householder";
        public const string DATE = "Date";
        public const string LESSON = "Lesson";

        public const string TYPE = "_type-";
        public const string READING_T = "1";        // _T for type
        public const string INITIAL_CALL_T = "2";
        public const string RETURN_VISIT_T = "3";
        public const string BIBILE_STUDY_T = "4";

        public const string SECTION = "_section-";
        public const string CLASS_A_S = "a";        // _S for section
        public const string CLASS_B_S = "b";

        // Indecies for reading from the table
        public const int COL_DATE = 1,
            COL_SEC_A_PARTICIPANTS = 2, COL_SEC_A_LESSON = 3,
            COL_SEC_B_PARTICIPANTS = 4, COL_SEC_B_LESSON = 5;

        enum AssignmentType { UNKNOWN = 0, READING = 1, INITIAL_CALL = 2, RETURN_VISIT = 3, BIBLE_STUDY = 4 }

        public const int NUM_WEEKS_PER_SCHEDULE = 5;
        public const int NUM_ASSGN_TYPES = 4;
        public const int NUM_SECTIONS = 2;

        public static void PopulatePdf(String scheduleDocx, String destinationPdfForm)
        {
            // each week of assignments is stored as a map. 
            // The keys in `weeks` are the suffixes for the pdf form keys.
            IDictionary<string, Assignment>[] weeks = new Dictionary<string, Assignment>[NUM_WEEKS_PER_SCHEDULE];

            // An MS Word app instance is needed to open .docx files.
            Application WordApplication = null;

            // Object Revert; Optional Object. 
            // Controls what happens if FileName is the name of an open document. 
            //  True to discard any unsaved changes to the open document and reopen the file. 
            //  False to activate the open document.
            Document document = null;

            // tables are indexed from 1... can you believe it! (I think all other document collection objects are as well)
            // how to handle tables: https://msdn.microsoft.com/en-us/library/w1702h4a.aspx
            Table schedule = null;

            try
            {   // Assuming the template has not been tampered with
                // start a Microsoft.Office.Interop.Word Application to open .docx files
                WordApplication = new Application();

                // open the schedule as a MS Word Doc
                document = WordApplication
                                    .Documents
                                    .Open(FileName: scheduleDocx, ReadOnly: true,
                                        AddToRecentFiles: false, Revert: false, Visible: false);
                
                // take first table (should be the only one)
                schedule = document.Tables[1];

                // proceed through the table row-wise

                int week = 0;
                int rowNum;
                string dateText;

                week = 0;
                rowNum = rowNumForDateOnWeekNumber(week);
                dateText = getRowCellText(schedule.Rows[rowNum], COL_DATE);
                rowNum++; // advance to next row

                weeks[week] = new Dictionary<string, Assignment>();
                parseAssignmentRow(schedule.Rows[rowNum], dateText, AssignmentType.READING, weeks[week]);
                week++; // advance to next week

                // continue with remaining 4 weeks
                for (; week < NUM_WEEKS_PER_SCHEDULE; week++)
                {
                    rowNum = rowNumForDateOnWeekNumber(week);
                    dateText = getRowCellText(schedule.Rows[rowNum], COL_DATE);

                    weeks[week] = new Dictionary<string, Assignment>();

                    foreach (AssignmentType assignmentType in Enum.GetValues(typeof(AssignmentType)))
                    {
                        // skip the default value of enum AssignmentType
                        if (assignmentType.Equals(AssignmentType.UNKNOWN))
                        {
                            continue;
                        }

                        // row number for each assgn is offset from the date-row 
                        //  by the number (caridnality) assigned to the assignment type
                        // (the int value in the enum is this cardinality)
                        rowNum = rowNumForDateOnWeekNumber(week) + (int)assignmentType; 
                        parseAssignmentRow(schedule.Rows[rowNum], dateText, assignmentType, weeks[week]);
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception caught");
            }
            finally
            {
                if (document != null)
                {
                    document.Close();
                    //document.Close(); // will this cause an exception??? yes it does how to catch if the document was not open...

                }
                WordApplication.Quit();
            }


            // open pdf document to read from `src` and to write to `dest`
            // named `reminders` because the pdf document is for reminder slips
            PdfDocument reminders = null;

            PdfAcroForm acroForm = null;

            IDictionary<string, PdfFormField> fields = null;

            try
            {
                // skipping week 0 (the first)
                for (int week = 1; week < NUM_WEEKS_PER_SCHEDULE; week++)
                {
                    reminders = new PdfDocument(new PdfReader(PDF_8_ASSGN_SLIPS), new PdfWriter(destinationPdfForm + $"week{week}.pdf"));
                    acroForm = PdfAcroForm.GetAcroForm(document: reminders, createIfNotExist: true);
                    fields = acroForm.GetFormFields();

                    foreach (string baseKey in weeks[week].Keys)
                    {
                        fields[DATE + baseKey].SetValue(weeks[week][baseKey].date);
                        
                        fields[ASSIGNEE + baseKey].SetValue(weeks[week][baseKey].assignee);

                        if (weeks[week][baseKey].householder != null)
                        {
                            fields[HOUSEHOLDER + baseKey].SetValue(weeks[week][baseKey].householder);
                        }

                        fields[LESSON + baseKey].SetValue(weeks[week][baseKey].lesson);
                    }

                    reminders.Close();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"null not allowed?{ex}");
            }
            finally
            {
                if (reminders != null)
                {
                    reminders.Close();
                }
            }
            Console.WriteLine("press key to exit.....");
            Console.ReadKey();
        }

        private static string buildKey(string role, string type, string section)
        {
            return new StringBuilder().Append(role).Append(TYPE).Append(type).Append(SECTION).Append(section).ToString();
        }

        /// <summary>
        /// Convinience method for reading text from a table. 
        /// Removes two control characters discovered to be inserted by MS Word 2016. Namely '/r' and '/a'.
        /// May throw an exception if index is out of bounds or table is null.
        /// </summary>
        /// <param name="table">MS Word document table object to read data from. Expected to be a schedule template. 
        /// Note: Table objects are indexed from 1, not 0.</param>
        /// <param name="row">Row in the table to read text from. No check is made that the index is vaid.</param>
        /// <param name="col">Column in the table to read text from. No check is made that the index is vaid.</param>
        /// <returns>Cleaned text at row and col parameters.</returns>
        private static string getCellText(Table table, int row, int col)
        {
            string rawText = table.Cell(row, col).Range.Text;

            return rawText.Trim(new char[] { '\r', '\a' });
        }

        private static string getRowCellText(Row row, int col)
        {
            string rawText = row.Cells[col].Range.Text;

            return rawText.Trim(new char[] { '\r', '\a' });
        }
        
        private static void parseAssignmentRow(Row row, string date, AssignmentType assignmentType, IDictionary<string, Assignment> assgns)
        {
            Assignment assgn = null;
            string participants;

            String key = null;

            // conviniece arrays for easier iteration
            int[] participantsCol = new int[] { COL_SEC_A_PARTICIPANTS, COL_SEC_B_PARTICIPANTS };
            int[] lessonCol = new int[] { COL_SEC_A_LESSON, COL_SEC_B_LESSON };

            for (int section = 0; section < NUM_SECTIONS; section++)
            {
                participants = getRowCellText(row, participantsCol[section]);

                if (participants == null || participants.Equals(""))
                {
                    assgn = null;
                }
                else
                {
                    assgn = new Assignment();

                    assgn.date = date;
                    assgn.type = ((int)assignmentType).ToString();

                    string[] names = participants.Split('\r'); // MS Word uses \r for newline

                    assgn.assignee = names[0].Trim();
                    if (names.Length > 1)
                    {
                        assgn.householder = names[1].Trim();
                    }

                    assgn.lesson = getRowCellText(row, lessonCol[section]);
                    assgn.section = sectionName(section);
                    
                    // role is left empty on purpose, this key will be used to map each value to the pdf
                    key = buildKey(string.Empty, assgn.type, assgn.section);
                    assgns.Add(key.ToString(), assgn);
                }
            }
        }

        /// <summary>
        /// Find the row number in the schedule template of the row containing the date for a week.
        /// </summary>
        /// <param name="week">Number of the week (0 to 4) for which the date is needed.</param>
        /// <returns>If the week number is not 0 to 4, the result returned is 2.</returns>
        public static int rowNumForDateOnWeekNumber(int week)
        {
            switch (week)
            {
                case 0:
                    return 2;
                case 1:
                    return 4;
                case 2:
                    return 9;
                case 3:
                    return 14;
                case 4:
                    return 19;
                default:
                    return 2;
            }
        }

        public static string sectionName(int sec)
        {
            switch (sec)
            {
                case 0:
                    return CLASS_A_S;
                case 1:
                    return CLASS_B_S;

                default:
                    return null;
                    
            }
        }

        /// <summary>
        /// Renames the text form fields for easier manipulation.  
        /// </summary>
        /// <param name="src">Text in the 'text form fields' will become the name of the text field in the `dest` param.</param>
        /// <param name="dest">This file will have nicely named 'text form fields'.</param>
        public static void NameTextFields(String src, String dest)
        {
            // open pdf document to read from `src` and to write to `dest`
            // named `reminders` because the pdf document is for reminder slips
            PdfDocument reminders = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

            // PdfAcroForm represents the static form technology AcroForm on a PDF file.
            PdfAcroForm form = PdfAcroForm.GetAcroForm(reminders, /*createIfNotExists = */ true); // retrieve AcroForm from the document

            // Get IDictionary map of form fields
            IDictionary<String, PdfFormField> fields = form.GetFormFields();

            // This is the part where the fields are renamed.
            String formFieldContent;
            foreach (String key in fields.Keys)
            {
                formFieldContent = fields[key].GetValueAsString();

                // text fields with text inside get renamed
                if (fields[key].GetFormType().Equals(PdfName.Tx) && !formFieldContent.Equals(""))
                {
                    // the field value is actually copied  automatically from PREP_SRC, this is undesiered
                    // clear the field value in PREP_DEST
                    fields[key].SetValue("");
                    // rename form field in PREP_DEST
                    fields[key].SetFieldName(formFieldContent);
                    fields[key].SetMappingName(formFieldContent);
                }
            }

            reminders.Close();
        }
    }
}
