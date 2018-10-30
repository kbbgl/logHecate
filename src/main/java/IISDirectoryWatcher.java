import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import static com.sun.jmx.mbeanserver.Util.cast;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class IISDirectoryWatcher {

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

        System.out.println("Files in " + this.path + ":");
        try(Stream<Path> paths = Files.walk(Paths.get(path.toAbsolutePath().toString()))){
            paths.filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void register() throws IOException {
        this.key = this.path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        System.out.printf("path %s registered\n", this.path);
    }

    private void processEvents(){
        while (true){
            try {
                key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()){
                    WatchEvent.Kind kind = event.kind();

                    if (kind == OVERFLOW){
                        continue;
                    }

                    WatchEvent<Path> ev = cast(event);
                    Path filename = ev.context();
                    Path absPath = Paths.get(this.path.toString(), filename.toString());

                    String extension = FilenameUtils.getExtension(filename.getFileName().toString());

                    if (extension.equals("txt")){
                        System.out.format("%s: %s\n", event.kind().name(), filename);
                        readFileContent(absPath);
                    }

                    key.reset();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
