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

import java.util.function.BooleanSupplier;


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

    public static Command directPlacment(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.directPlacement();
        }, outtakeSubsystem);
    }

    public static Command pickUpSpecimenPreload(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.pickUpFullPreload();
        }, outtakeSubsystem);
    }

    public static Command preloadPickUpSpecimen(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.preloadPickUpFull();
        }, outtakeSubsystem);
    }

    public static Command pickUpPOS(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.pickUpPOS();
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

    public static Command setMaxPower(OuttakeSubsystem outtakeSubsystem, double speed) {
        return new InstantCommand(() -> {
            outtakeSubsystem.setMaxSpeed(speed);
        }, outtakeSubsystem);
    }

    public static Command completeScorePosition(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.completeScoringPosition();
        }, outtakeSubsystem);
    }

    public static Command closeClawThenScore(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                new WaitCommand(650),
                Commands.closeClaw(outtakeSubsystem),
                new WaitCommand(300), // Wait for 500 milliseconds (0.5 seconds)
                Commands.pickUpSpecimen(outtakeSubsystem)
        );
    }

    public static Command closeClawThenScoreCorner(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                new WaitCommand(150),
                Commands.closeClaw(outtakeSubsystem),
                new WaitCommand(300), // Wait for 500 milliseconds (0.5 seconds)
                Commands.pickUpSpecimen(outtakeSubsystem)
        );
    }

    public static Command closeClawThenDirect(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.closeClaw(outtakeSubsystem),
                new WaitCommand(125), // Wait for 500 milliseconds (0.5 seconds)
                Commands.directPlacment(outtakeSubsystem)
        );
    }

    public static Command closeClawThenScorePreload(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.pickUpSpecimenPreload(outtakeSubsystem)
        );
    }

    public static Command preloadcloseClawThenScore(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.closeClaw(outtakeSubsystem),
                new WaitCommand(75), // Wait for 500 milliseconds (0.5 seconds)
                Commands.preloadPickUpSpecimen(outtakeSubsystem)
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

    public static Command getDistance(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.distance();
        }, outtakeSubsystem);
    }

    public static BooleanSupplier getDistance1(OuttakeSubsystem outtakeSubsystem) {
        return (BooleanSupplier) new SequentialCommandGroup(
                Commands.getDistance(outtakeSubsystem)
        );
    }

    public static Command flick(OuttakeSubsystem outtakeSubsystem) {
        return new InstantCommand(() -> {
            outtakeSubsystem.flick();
        }, outtakeSubsystem);
    }

    public static Command flickWithDelay(OuttakeSubsystem outtakeSubsystem) {
        return new SequentialCommandGroup(
                Commands.flick(outtakeSubsystem),
                new WaitCommand(500),
                Commands.openClaw(outtakeSubsystem)
        );
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