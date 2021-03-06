import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.PathConverter;
import validators.DirectoryParameterValidator;
import validators.FileParameterValidator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Parameters(separators = "=")
class MainCLParameters {

    @Parameter(
            names = {"-h", "--h", "help", "-help", "--help"},
            help = true,
            description = "Displays help information")
    private boolean help;

    @Parameter(
            names = {"-wd", "--wd", "--watchdir", "-watchdir"},
            required = true,
            //variableArity = true
            description = "Absolute path to the directory to watch for file changes",
            validateWith = DirectoryParameterValidator.class,
            converter = PathConverter.class
    )
    Path watchDir;
    // public List<Path> paths = new ArrayList<>();

    @Parameter(
            names = {"-p", "--p", "--pattern", "-pattern"},
            required = true,
            description = "Pattern to search for"
    )
    String pattern;

    @Parameter(
            names = {"-of", "--of", "--outputfile", "-outputfile"},
//            required = false,
            description = "Absolute path to the created log. Default path where app is run.",
            validateWith = FileParameterValidator.class,
            converter = FileConverter.class
    )
    File outputFile;

    boolean isHelp() {
        return help;
    }

    String getOutputFilePath(){
        return this.outputFile.getPath();
    }

    @Override
    public String toString() {
        return "\n\twatchDir=" + watchDir +
                "\n\tpattern=" + pattern +
                "\n\toutputDir=" + outputFile +
                "\n";
    }
}
