// Copyright (C) 2017  Fidel Coria

/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

using System;
using System.IO;

namespace ReminderSlipPopulator
{
    public class Program
    {
        // pdf files for the reminder slips (both files are exactly the same, except for the names)
        // This file will have text form fields named.
        public const string PREP_DEST = "S-89-S 8.pdf";
        // This file has its text form fields populated with the future names for the fields in `PREP_DEST`.
        public const string PREP_SRC = "filledForm.pdf";

        // after initialization this pdf file will be used as a the source
        public const string SRC = "S-89-S 8.pdf";

        /// <summary>
        /// Command Line Interface to initialize and to populate pdf files.
        /// </summary>
        /// <param name="args">
        /// --init-form
        ///     initializes form by renaming text form fields for latter mapping
        ///     only need to be ran once
        /// -p <schedule.csv> <output.pdf>
        ///     use <schedule.docx> to populate <output_directory>
        ///     both arguments should be absolute paths
        /// </param>
        static void Main(string[] args)
        {
            // args do not include the program name... just the arguments

            if (args.Length == 3 && args[0].Equals("-p"))
            {
                // file to read data from
                string scheduleCsv = args[1];
                // file to be populated with data
                string outputPdf = args[2];

                FormFill.PopulatePdf(scheduleCsv, outputPdf);
            }
            else if (args.Length == 1 && args[0].Equals("--init-form"))
            {
                // make sure the file Directory exists
                FileInfo file = new FileInfo(PREP_DEST);
                file.Directory.Create();

                // update the text fields
                FormFill.NameTextFields(PREP_SRC, PREP_DEST);
            }
            else
            {
                Console.WriteLine("usage: program [--init-form] [-p <schedule.docx> <output.pdf>]");
                Console.WriteLine("  --init-form    renames the text form fields");
                Console.WriteLine("  -p             use <schedule.docx> to populate <output.pdf>");
            }
        }
    }
}
