import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {

    private final MainCLParameters mainArgs = new MainCLParameters();
    private final String appName = "logHecate";
    private final Date appRunDatetime = new Date();

    // TODO add console color printing support
    // https://github.com/fusesource/jansi

    public static void main(String[] args) {

        // TODO add file extension as an option, i.e. -e "txt"

        App app = new App();
        app.handleInputArgs(args);
        app.run();
    }

    private void run() {
        System.out.println("\nStart time: " + appRunTime(appRunDatetime));
        System.out.printf("Running %s with arguments:\n", appName);
        System.out.println(mainArgs);

        try {
            outputFile();
            new FileWatcher(mainArgs.watchDir, mainArgs.pattern, outputFile());
            } catch (IOException e) {
                System.out.printf("ERROR: unable to launch FileWatcher - %s", e.getMessage());
        } catch (URISyntaxException e) {
            System.out.printf("ERROR: unable to create output file - %s", e.getMessage());
        }
    }

    private void handleInputArgs(String[] args) {
        JCommander jCommander = new JCommander(mainArgs);
        jCommander.setProgramName(appName);

        try {
            jCommander.parse(args);
        }
        catch (ParameterException pe){
            System.err.println(pe.getMessage());
            showUsage(jCommander);
        }

        if (mainArgs.isHelp()){
            showUsage(jCommander);
        }
    }

    private void showUsage(JCommander jCommander) {
        jCommander.usage();
        System.exit(0);
    }

    private String appRunTime(Date runTime){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return dateFormat.format(runTime);
    }

    private File outputFile() throws URISyntaxException, IOException {

        File file;

        if (mainArgs.outputFile == null) {

            // Get jar launch location\
            String jarLocation;
            jarLocation = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalPath();
            Path path = Paths.get(Paths.get(jarLocation).getParent() + "/result_" + appRunDatetime.getTime() +  ".log");

            System.out.println("Output file flag not specified. Creating file in " + path.toAbsolutePath().toString());
            file = new File(path.toAbsolutePath().toString());
            file.createNewFile();

        }else {

            System.out.println("Output file flag specified. Writing to file " + mainArgs.getOutputFilePath());
            file = new File(mainArgs.getOutputFilePath());
        }

        return file;
    }
}
