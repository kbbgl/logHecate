import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {

    private final MainCLParameters mainArgs = new MainCLParameters();
    private final String appName = "logHecate";

    // TODO add console color printing support
    // https://github.com/fusesource/jansi

    public static void main(String[] args) {

        // TODO add file extension as an option, i.e. -e "txt"

        App app = new App();
        app.handleInputArgs(args);
        app.run();
    }

    private void run() {
        System.out.println("\nStart time: " + appRunTime());
        System.out.printf("Running %s with arguments:\n", appName);
        System.out.println(mainArgs);

        if (mainArgs.outputDir == null) {
            try {
                new FileWatcher(mainArgs.watchDir, mainArgs.pattern);
            } catch (IOException e) {
                System.out.printf("ERROR: unable to launch FileWatcher - %s", e.getMessage());
            }
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

    private String appRunTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
