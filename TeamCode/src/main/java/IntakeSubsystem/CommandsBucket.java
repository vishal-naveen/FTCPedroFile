package IntakeSubsystem;

import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import IntakeSubsystem.BucketSideAutoSubsystem;
import IntakeSubsystem.BucketSideAutoSubsystem.HoverOrientation;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;

public class CommandsBucket {
    // Path Following Commands
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

    public static Command followPath(Follower follower, PathChain pathChain) {
        return new FollowPathCommand(follower, pathChain);
    }

    // Intake Extension Commands
    public static Command extendIntakeFull(BucketSideAutoSubsystem subsystem, HoverOrientation orientation) {
        return new InstantCommand(() -> {
            subsystem.extendIntakeFullWithHover(orientation);
        }, subsystem);
    }

    public static Command retractIntakeForTransfer(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.retractIntakeForTransfer();
        }, subsystem);
    }

    public static Command retractIntakeOnly(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.retractIntakeOnly();
        }, subsystem);
    }

    // Basic Claw Commands
// Basic Claw Commands
    public static Command openIntakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.openIntakeClaw();
        }, subsystem);
    }

    public static Command closeIntakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.closeIntakeClaw();
        }, subsystem);
    }

    public static Command openOuttakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.openOuttakeClaw();
        }, subsystem);
    }

    public static Command closeOuttakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.closeOuttakeClaw();
        }, subsystem);
    }

    public static Command outtakePark(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.outtakePark();
        }, subsystem);
    }

    // Intake Sequence Commands
    public static Command startIntakePickupSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                openIntakeClaw(subsystem),
                new WaitCommand(250),
                // Move wrist down to pickup position
                new InstantCommand(() -> subsystem.startIntakeSequence("pickup_position")),
                new WaitCommand(500),
                // Close claw to grab
                closeIntakeClaw(subsystem),
                new WaitCommand(500),
                // Move wrist back up to hover
                new InstantCommand(() -> subsystem.startIntakeSequence("pickup_hover"))
        );
    }

    public static Command startIntakeAndOuttakeTransferSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                openIntakeClaw(subsystem),
                new WaitCommand(250),
                new InstantCommand(() -> subsystem.startIntakeSequence("transfer")),
                new WaitCommand(500),
                closeIntakeClaw(subsystem)
        );
    }

    public static Command startIntakeOnlyTransferSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                openIntakeClaw(subsystem),
                new WaitCommand(250),
                new InstantCommand(() -> subsystem.startIntakeSequence("transfer_intake_only")),
                new WaitCommand(500),
                closeIntakeClaw(subsystem)
        );
    }

    // Outtake Commands
    public static Command setOuttakeToTransferPosition(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.setOuttakeToTransferPosition();
        }, subsystem);
    }

    public static Command setOuttakeToHighBucket(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> {
            subsystem.setOuttakeToHighBucket();
        }, subsystem);
    }

    // Full Outtake Transfer Sequence
    public static Command startFullOuttakeTransferSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                setOuttakeToTransferPosition(subsystem),
                new WaitCommand(500),
                new InstantCommand(() -> subsystem.startOuttakeTransferSequence()),
                new WaitCommand(1500)
        );
    }

    // Combined Sequences
    public static Command pickupAndTransferToOuttake(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                startIntakePickupSequence(subsystem),
                new WaitCommand(500),
                startFullOuttakeTransferSequence(subsystem)
        );
    }

    public static Command pickupAndTransferIntakeOnly(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                startIntakePickupSequence(subsystem),
                new WaitCommand(500),
                startIntakeOnlyTransferSequence(subsystem)
        );
    }

    // Wait Commands
    public static Command sleep(long milliseconds) {
        return new WaitCommand(milliseconds);
    }

    public static Command pauseFor(long milliseconds) {
        return new WaitCommand(milliseconds);
    }
}