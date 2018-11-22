import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

import static com.sun.jmx.mbeanserver.Util.cast;

class FileWatcher {

    private Path path;
    private String pattern;
    private File outputLog;

    // TODO add file types to look for
    // private String[] fileTypes;
    private WatchService watchService;
    private WatchKey key;

    FileWatcher(Path watchDir, String pattern, File outputLog) throws IOException {
        this.path = watchDir;
        this.pattern = pattern;
        this.outputLog = outputLog;
        this.watchService = FileSystems.getDefault().newWatchService();
        printListFilesInDir();
        register();
        processEvents();
    }

    private void printListFilesInDir() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        File[] files = this.path.toFile().listFiles();

        if (files != null && files.length > 0) {

            System.out.println("Files in " + this.path + ":\n");

            for (File file : files) {

                // TODO change logic based on fileTypes property.
                if (file.isFile()
                        && (FilenameUtils.getExtension(file.getName()).equals("txt")
                        || FilenameUtils.getExtension(file.getName()).equals("log"))){
                    System.out.printf("%s\t%-10s%s%n", file.getName(), file.length(), simpleDateFormat.format(file.lastModified()));
//                    System.out.println("\t" + file.getName() + "\t" + file.length() + " bytes" + "\t" + simpleDateFormat.format(file.lastModified()));
                }
            }
            System.out.println();
        }
        else {
            System.out.printf("No files found in %s\n", this.path);
        }

    }

    private void register() throws IOException {
        this.key = this.path.
                register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
    }

    private void processEvents(){
        while (true){
            try {
                key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()){
                    WatchEvent.Kind kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW){
                        System.err.println("Overflow occurred");
                        continue;
                    }

                    WatchEvent<Path> ev = cast(event);
                    handleEvent(ev);

                }
            } catch (InterruptedException e) {
                System.err.println("Error retrieving and removing next watch key:");
                System.err.println("Message: " + e.getMessage());
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                System.err.println("Stacktrace: " + sw.toString());
            }
        }
    }

    private void handleEvent(WatchEvent<Path> event) {

        Path filename = event.context();
        Path absPath = Paths.get(this.path.toString(), filename.toString());
        String extension = FilenameUtils.getExtension(filename.getFileName().toString());

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
            System.out.printf("File created %s\n", filename);
        }
        else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
            System.out.printf("File deleted %s\n",filename);
        }
        else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
            System.out.printf("File modified %s\n", filename);
            if (extension.equals("txt") || extension.equals("log")){
                try {
                    readFileContent(absPath);
                } catch (IOException e) {
                    System.err.println("ERROR: reading file content: " + e.getMessage());
                }
            }
        }

        key.reset();
    }

    private void readFileContent(Path path) throws IOException{
        File file = path.toFile();
//        final FileWriter fileWriter = new FileWriter(this.outputLog, true);
        System.out.println("Reading file " + file);

        try (FileWriter fileWriter = new FileWriter(this.outputLog, true)){
            Files.lines(Paths.get(file.getAbsolutePath()))
                    .filter(line -> line.contains(this.pattern))
                    .forEach(line -> writeToFile(fileWriter, line));
        }
    }

    private void writeToFile(FileWriter fw, String line) {
        try {
            fw.write(String.format("%s%n", line));
            System.out.printf("Wrote line %s to file%n", line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
