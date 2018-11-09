# LogHecate
#####  Create filtered log files

#### To run:

1) `cd [PATH_TO_CLONED_DIR]/logHecate/build/libs`.
2) Run the following command:

<code>java -jar logHecate.jar -p PATTERN -wd path/to/dir</code>
 
 For help, type <code>java -jar logHecate.jar help </code>
 
 ```
 USAGE: logHecate [arguments]
 
      arguments:
       
      * -wd, --wd, --watchdir, -watchdir
          Absolute path to the directory to watch for file changes.
          
      * -p, --p, --pattern, -pattern
                Pattern to search for within -watchdir.
         
        -od, --od, --outputdir, -outputdir
          Absolute path to the created log. Default is path where app is run.
          
        -h, --h, help, -help, --help
          Displays help information.

      * indicates mandatory arguments          
```       
   
#### Example: 
To fetch logs that might contain errors
 
 UNIX:   
 <code>java -jar logHecate.jar -p error -wd /Users/admin/some/software/logs</code>
 
 Windows:  
 <code>java -jar logHecate.jar -p error -wd C://some//software//logs</code>