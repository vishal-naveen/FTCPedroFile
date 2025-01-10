package WasteOrOld;// commands/WasteOrOld.Commands.java


import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;

public class Commands {
    public static Command followPath(Follower follower, Path path) {
        return new RunCommand(() -> follower.followPath(path));
    }

    public static Command followPath(Follower follower, PathChain pathChain) {
        return new RunCommand(() -> follower.followPath(pathChain));
    }

    public static Command sleep(long milliseconds) {
        return new WaitCommand(milliseconds);
    }
}