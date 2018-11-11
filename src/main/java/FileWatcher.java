import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    FileWatcher(Path path, String pattern) throws IOException{
        this.path = path;
        this.pattern = pattern;
        this.watchService = FileSystems.getDefault().newWatchService();
        printListFilesInDir();
        register();
        processEvents();
    }

    FileWatcher(Path watchDir, String pattern, File outputDir) throws IOException {
        this.path = watchDir;
        this.pattern = pattern;
        this.outputLog = outputDir;
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

    private void handleEvent(WatchEvent<Path> event){

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
            if (extension.equals("txt")){
                readFileContent(absPath);
            }
        }

        key.reset();
    }

    private void readFileContent(Path path){
        File file = path.toFile();
        System.out.println("Reading file " + file);

        try(Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath())).filter(line -> line.contains(this.pattern))){

            stream.forEach(System.out::println);
//            writeToOutputFile(stream);

        } catch (IOException e) {
            System.err.println("Error reading file:");
            System.err.println("Message: " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.err.println("Stacktrace: " + sw.toString());
        }
    }

//    private void writeToOutputFile(Stream<String> lines) throws IOException {
//        PrintWriter output;
//        System.out.println("writeToOutputFile: " + this.outputLog.getAbsolutePath());
//        output = new PrintWriter(this.outputLog.getAbsolutePath(), "UTF-8");
//        lines.forEachOrdered(output::println);
//
//    }
}
