package validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.FilePermission;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlException;
import java.security.AccessController;

public class DirectoryParameterValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        Path pathToWatchDir = Paths.get(value);
        if (!exists(pathToWatchDir)){
            String message = String.format("The [%s] directory [%s] doesn't exist: ", name, value);
            throw new ParameterException(message);
        }

        if (!Files.isDirectory(pathToWatchDir)){
            String message = String.format("The [%s] directory specified [%s] is not a directory", name, value);
            throw new ParameterException(message);
        }

//        if (!checkPermissions(value)){
//            String message = String.format("Application doesn't have read/write permissions to [%s]", value);
//            throw new ParameterException(message);
//        }
    }

//    private boolean checkPermissions(String path) {
//        try {
//            AccessController.checkPermission(new FilePermission(path, "read,write"));
//            return true;
//        }
//        catch (AccessControlException e){
//            return false;
//        }
//    }

    private boolean exists(Path path) {
        return (Files.exists(path));
    }


}
