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

namespace ReminderSlipPopulator
{
    public class Program
    {


        /// <summary>
        /// Command Line Interface to initialize and to populate pdf files.
        /// </summary>
        /// <param name="args">
        /// --init-form
        ///     initializes form by renaming text form fields for latter mapping
        ///     only needs to be run once
        /// -p <schedule.csv> <output.pdf>
        ///     --populate is an equivalent flag
        ///     use <schedule.docx> to populate <output_directory>
        ///     both arguments should be absolute paths
        /// </param>
        static void Main(string[] args)
        {
            // args do not include the program name... just the arguments

            if (args.Length == 3 && 
                (args[0].Equals("-p") || args[0].Equals("--populate")))
            {
                // file to read data from
                string scheduleCsv = args[1];
                // folder where filled .pdf files will be placed
                string outputPdf = args[2];

                FormFill.PopulatePdf(scheduleCsv, outputPdf);
            }
            else if (args.Length == 1 && args[0].Equals("--init-form"))
            {
                // make sure the file Directory exists
                //FileInfo file = new FileInfo(PREP_DEST);
                //file.Directory.Create();

                // update the text fields
                FormFill.NameTextFields(FormFill.NAMED_FIELDS_1, FormFill.FIRST_WEEK_SPANISH_PDF);
                FormFill.NameTextFields(FormFill.NAMED_FIELDS_8, FormFill.MID_MONTH_SPANISH_PDF);
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
