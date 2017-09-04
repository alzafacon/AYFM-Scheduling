# Reminder Slip Populator

## Starting this Visual Studio Project

In this project, C# is used to read a .docx (Microsoft Word) file and fill the form fields of a .pdf file.
This project is more like a document level customization not a VSTO (Visual Studio Tools for Office) add-in or Office Add-in. Still it may be necessary to have the [appropriate version of VS](https://msdn.microsoft.com/en-us/library/bb398242.aspx) installed. Your installed version of Microsoft Word should be 2013 or 2016.

## Dependencies

There are two packages that need to be installed:

1. itext7 (for manipulating .pdf)
  - using NuGet Package Manager Console: "Install-Package itext7"
  - or from menu bar ```Project -> Manage NuGet Packages...``` and search for itext7
2. Microsoft.Office.Interop.Word (for manipulating .docx)
  - from menu bar ```Project -> Add Reference```, under ```Assemblies/Extensions```

## Command line arguments

There is a command line interface to this program. To pass arguments to the program using Visual Studio (VS):
1. In the Solution Explorer right-click on the project name
2. Go to "properties" (a new tab will open)
3. Go to "Debug" (on the left of the tab that opened)
4. Set the working directory to the project folder (..\\ReminderSlipPopulator)
5. Enter any command line arguments you like

## packaging with java code
All of the .dll's together with the .pdf's and the .exe in /ReminderSlipPopulator/bin/Debug/ are needed by the JAR.
During installation all of the files mentioned above should saved at "C:\Program Files\AYFM\PdfReminderSlipPopulator\\"
