import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import static com.sun.jmx.mbeanserver.Util.cast;

class IISDirectoryWatcher {

    private Path path;
    private WatchService watchService;
    private WatchKey key;

    IISDirectoryWatcher(String path) throws IOException {
        this.path = Paths.get(path);
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
                e.printStackTrace();
            }
        }
    }

    private void handleEvent(WatchEvent<Path> event){

        Path filename = event.context();
        Path absPath = Paths.get(this.path.toString(), filename.toString());
        String extension = FilenameUtils.getExtension(filename.getFileName().toString());

        if (extension.equals("txt")){
            System.out.format("%s: %s\n", event.kind().name(), filename);
            readFileContent(absPath);
        }


        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
            System.out.printf("File created %s\n", event.context());
        }
        else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
            System.out.printf("File deleted %s\n", event.context());
        }
        else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
            System.out.printf("File modified %s\n", event.context());
        }

        key.reset();
    }

    private void readFileContent(Path path){
        File file = path.toFile();
        System.out.println("Reading file " + file);

        try(Stream<String > stream = Files.lines(Paths.get(file.getAbsolutePath()))){

            stream.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
