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
        public const string SRC = "S-89-S 8.pdf";

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
            // Jagged 3-D Array to store assignments used to fill pdf
            Assignment[][][] weeks = new Assignment[NUM_WEEKS_PER_SCHEDULE][][];
            // alternatively an array of Maps could be used
            // IDictionary<string, Assignment>[] weeks = new Dictionary<>[NUM_WEEKS_PER_SCHEDULE];

            // start a Microsoft.Office.Interop.Word Application to open .docx files
            Application WordApplication = new Application();

            // Object Revert; Optional Object. 
            // Controls what happens if FileName is the name of an open document. 
            //  True to discard any unsaved changes to the open document and reopen the file. 
            //  False to activate the open document.

            // open the schedule as a MS Word Doc
            Document document = WordApplication
                                    .Documents
                                    .Open(FileName: scheduleDocx, ReadOnly: true,
                                        AddToRecentFiles: false, Revert: false, Visible: false);

            // tables are indexed from 1... can you believe it! (I think all other document collection objects are as well)
            // how to handle tables: https://msdn.microsoft.com/en-us/library/w1702h4a.aspx
            Table schedule = document.Tables[1];

            try
            {
                // proceed through the table row-wise

                int week = 0;
                int row = date_row(week);
                string dateText;

                // Week one is a special case with only one assignment (reading)
                weeks[week] = new Assignment[1][];

                dateText = getCellText(schedule, row, COL_DATE);
                row = date_row(week) + (int)AssignmentType.READING;

                weeks[week][0] = parseAssignmentRow(schedule, row, dateText, AssignmentType.READING);
                week++; // done with first week

                // continue with remaining 4 weeks
                for (; week < NUM_WEEKS_PER_SCHEDULE; week++)
                {
                    row = date_row(week);
                    dateText = getCellText(schedule, row, COL_DATE);

                    weeks[week] = new Assignment[NUM_ASSGN_TYPES][];

                    foreach (AssignmentType assignmentType in Enum.GetValues(typeof(AssignmentType)))
                    {
                        // skip the default value of enum AssignmentType
                        if (assignmentType.Equals(AssignmentType.UNKNOWN))
                        {
                            continue;
                        }

                        row = date_row(week) + (int)assignmentType;
                        int typeIndex = (int)assignmentType - 1; // -1 to make zero indexed
                        weeks[week][typeIndex] = parseAssignmentRow(schedule, row, dateText, assignmentType);
                    }
                }

            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception caught");
            }
            finally
            {
                document.Close();
                WordApplication.Quit();
            }


            // open pdf document to read from `src` and to write to `dest`
            // named `reminders` because the pdf document is for reminder slips
            PdfDocument reminders = null;

            PdfAcroForm acroForm = null;

            IDictionary<string, PdfFormField> fields = null;

            string key;
            string value;
            try
            {
                string[] sectionName = new string[] { CLASS_A_S, CLASS_B_S };
                // skipping week 0 (the first)
                for (int week = 1; week < NUM_WEEKS_PER_SCHEDULE; week++)
                {
                    reminders = new PdfDocument(new PdfReader(SRC), new PdfWriter(destinationPdfForm + $"week{week}.pdf"));
                    acroForm = PdfAcroForm.GetAcroForm(document: reminders, createIfNotExist: true);
                    fields = acroForm.GetFormFields();

                    for (int assgnType = 0; assgnType < 4; assgnType++)
                    {
                        for (int section = 0; section < NUM_SECTIONS; section++)
                        {
                            if (weeks[week][assgnType][section] != null)
                            {
                                key = buildKey(DATE, (assgnType + 1).ToString(), sectionName[section]);
                                value = weeks[week][assgnType][section].date;
                                if (value != null)
                                {
                                    fields[key].SetValue(value);
                                }

                                key = buildKey(LESSON, (assgnType + 1).ToString(), sectionName[section]);
                                value = weeks[week][assgnType][section].lesson;
                                if (value != null)
                                {
                                    fields[key].SetValue(value);
                                }

                                key = buildKey(ASSIGNEE, (assgnType + 1).ToString(), sectionName[section]);
                                value = weeks[week][assgnType][section].assignee;
                                if (value != null)
                                {
                                    fields[key].SetValue(value);
                                }

                                key = buildKey(HOUSEHOLDER, (assgnType + 1).ToString(), sectionName[section]);
                                value = weeks[week][assgnType][section].householder;
                                if (value != null)
                                {
                                    fields[key].SetValue(value);
                                }
                            }
                        }
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

            return rawText.Trim(new char[] { '\r', '\a' }); ;
        }

        /// <summary>
        /// Parses the table row passed into assignments and then inserts into the given map `assignments`.
        /// </summary>
        /// <param name="date">The date for the assignments in the given row.</param>
        /// <param name="row">Row from the schedule MS Word Table.</param>
        /// <param name="assignmentType">Type of the assignments on the given row.</param>
        /// <returns>An array with two elements. First is the section a and second is section b. If one in not assigned, will be left null.</returns>
        private static Assignment[] parseAssignmentRow(Table schedule, int row, string date, AssignmentType assignmentType)
        {
            Assignment[] assgn = new Assignment[NUM_SECTIONS];
            string participants;

            // conviniece arrays for easier iteration
            int[] participantsCol = new int[] { COL_SEC_A_PARTICIPANTS, COL_SEC_B_PARTICIPANTS };
            int[] lessonCol = new int[] { COL_SEC_A_LESSON, COL_SEC_B_LESSON };
            string[] sectionName = new string[] { CLASS_A_S, CLASS_B_S };

            for (int section = 0; section < NUM_SECTIONS; section++)
            {
                participants = getCellText(schedule, row, participantsCol[section]);

                if (participants.Equals(""))
                {
                    assgn[section] = null;
                }
                else
                {
                    assgn[section] = new Assignment();

                    assgn[section].date = date;
                    assgn[section].section = sectionName[section];
                    assgn[section].type = ((int)assignmentType).ToString();
                    assgn[section].lesson = getCellText(schedule, row, lessonCol[section]);

                    string[] names = participants.Split('\r'); // MS Word uses \r for newline

                    assgn[section].assignee = names[0].Trim();
                    if (names.Length > 1)
                    {
                        assgn[section].householder = names[1].Trim();
                    }
                }
            }

            return assgn;
        }

        /// <summary>
        /// Find the row number in the schedule template of the row containing the date for a week.
        /// </summary>
        /// <param name="week">Number of the week (0 to 4) for which the date is needed.</param>
        /// <returns>If the week number is not 0 to 4, the result returned is 2.</returns>
        public static int date_row(int week)
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
