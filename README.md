# LogHecate
####  Create filtered log files by reading other static and live-written logs

To run:

1) Change into `[PATH_TO_CLONED_DIR]/iisDirectoryWatcher/build/libs`.
2) Run the following command:

<code>java -jar logHecate.java -p "PATTERN" -d "path/to/dir""</code>

i.e. to fetch logs that might contain errors
 
 UNIX:   
 <code>java -jar logHecate.java -p "err" -d "/Users/admin/some/software/logs"</code>
 
 Windows:  
 <code>java -jar logHecate.java -p "err" -d "C://some//software//logs"</code>