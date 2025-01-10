package Positions;

import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import Subsystem.OuttakeSubsystem;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;


public class Commands {

    // Advanced Path Following Command
    public static class FollowPathCommand extends CommandBase {
        private final Follower follower;
        private final Path path;
        private final PathChain pathChain;
        private final boolean isPathChain;

        public FollowPathCommand(Follower follower, Path path) {
            this.follower = follower;
            this.path = path;
            this.pathChain = null;
            this.isPathChain = false;
        }

        public FollowPathCommand(Follower follower, PathChain pathChain) {
            this.follower = follower;
            this.path = null;
            this.pathChain = pathChain;
            this.isPathChain = true;
        }

        @Override
        public void initialize() {
            if (isPathChain) {
                follower.followPath(pathChain, true);
            } else {
                follower.followPath(path);
            }
        }

        @Override
        public void execute() {
            follower.update();
        }

        @Override
        public boolean isFinished() {
            return !follower.isBusy();
        }
    }



    // Path following methods
    public static Command followPath(Follower follower, Path path) {
        return new FollowPathCommand(follower, path);
    }

    // Add this to your Commands class
    public static Command preloadPosition(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.preloadPosition();
        }, outtakeSubsystem);
    }

    // You might also want a sequential command that ensures proper timing
    public static Command setPreloadPosition(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.closeClaw(outtakeSubsystem),  // Close claw first (0.9)
                new WaitCommand(250),  // Wait a bit
                preloadPosition(outtakeSubsystem)  // Then move to preload position
        );
    }

    public static Command followPath(Follower follower, PathChain pathChain) {
        return new FollowPathCommand(follower, pathChain);
    }

    // Outtake Subsystem Commands
    public static Command pickUpSpecimen(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.pickUpFull();
        }, outtakeSubsystem);
    }

    // In the Commands class
    public static Command openClawThenPickUp(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.openClaw(outtakeSubsystem),
                new WaitCommand(500), // Wait for 750 milliseconds (0.75 seconds)
                Commands.pickUpSpecimen(outtakeSubsystem)
        );
    }

    public static Command prepareViperForScore(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.prepareScoreViper();
        }, outtakeSubsystem);
    }

    public static Command completeScorePosition(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.completeScoringPosition();
        }, outtakeSubsystem);
    }

    public static Command closeClawThenScore(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.closeClaw(outtakeSubsystem),
                new WaitCommand(500), // Wait for 500 milliseconds (0.5 seconds)
                Commands.prepareViperForScore(outtakeSubsystem),
                new WaitCommand(1250), // Wait another 500 milliseconds
                Commands.completeScorePosition(outtakeSubsystem)
        );
    }

    public static Command scoreSpecimen(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.scoreFull();
        }, outtakeSubsystem);
    }

    public static Command openClaw(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.openClaw();
        }, outtakeSubsystem);
    }

    public static Command closeClaw(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.closeClaw();
        }, outtakeSubsystem);
    }

    // Enhanced Sleep Command
    public static Command sleep(long milliseconds) {
        return new WaitCommand(milliseconds);
    }

    // Additional Utility Commands
    public static Command pauseFor(long milliseconds) {
        return new WaitCommand(milliseconds);
    }

    // Composite Commands
}