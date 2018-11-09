# LogHecate
####  Create filtered log files by reading other static and live-written logs

To run:

1) `cd [PATH_TO_CLONED_DIR]/logHecate/build/libs`.
2) Run the following command:

<code>java -jar logHecate.java -p PATTERN -wd path/to/dir</code>

i.e. to fetch logs that might contain errors
 
 UNIX:   
 <code>java -jar logHecate.jar -p "err" -d "/Users/admin/some/software/logs"</code>
 
 Windows:  
 <code>java -jar logHecate.jar -p "err" -d "C://some//software//logs"</code>
 
 For help, type <code>java -jar logHecate.jar help </code>:
 
 ```
 Usage: logHecate [options]
      Options:
        -h, --h, help, -help, --help
          Displays help information
        -od, --od, --outputdir, -outputdir
          Absolute path to the created log. Default is path where app is run.
      * -p, --p, --pattern, -pattern
          Pattern to search for
      * -wd, --wd, --watchdir, -watchdir
          Absolute path to the directory to watch for file changes```