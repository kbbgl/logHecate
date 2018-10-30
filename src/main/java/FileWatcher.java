import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.stream.Stream;

import static com.sun.jmx.mbeanserver.Util.cast;

class FileWatcher {

    private Path path;
    private String pattern;
    private WatchService watchService;
    private WatchKey key;

    FileWatcher(String path, String pattern) throws IOException {
        this.path = Paths.get(path);
        this.pattern = pattern;
        this.watchService = FileSystems.getDefault().newWatchService();
        printListFilesInDir();
        register();
        processEvents();
    }

    private void printListFilesInDir() {

        File[] files = this.path.toFile().listFiles();

        if (files != null && files.length > 0) {

            System.out.println("Files in " + this.path + ":");

            for (File file : files) {

                if (file.isFile()){
                    System.out.println("\t" + file.getName());
                }
            }
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

        try(Stream<String > stream = Files.lines(Paths.get(file.getAbsolutePath())).filter(line -> line.contains(this.pattern))){

            stream.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error reading file:");
            System.err.println("Message: " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.err.println("Stacktrace: " + sw.toString());
        }
    }
}
