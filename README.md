# LogHecate
#####  Create filtered log files

[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/badges/shields.svg?style=popout)](https://github.com/kbbgl/logHecate)

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
 <code>java -jar logHecate.jar -p error -wd /Users/admin/some/software/logs   
 -of /Users/admin/temp/output.log</code>
 
 Windows:  
 <code>java -jar logHecate.jar -p error -wd C://some//software//logs -of C://temp//output_log.log</code>
 
 
##### Limitations:
 
 * If you choose to specify a file path using the `-of` flag, make sure that the file has already been created.
 This limitation will be addressed in next versions.
 
 * Currently supports reading `.txt` and `.log` files in directory.