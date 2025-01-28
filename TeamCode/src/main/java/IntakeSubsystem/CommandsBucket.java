package IntakeSubsystem;

import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;

public class CommandsBucket {
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

    public static Command followPath(Follower follower, Path path) {
        return new FollowPathCommand(follower, path);
    }

    public static Command followPath(Follower follower, PathChain pathChain) {
        return new FollowPathCommand(follower, pathChain);
    }

    // Basic commands
    public static Command startTransfer(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.startTransfer(), subsystem);
    }

    public static Command extendIntake(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.extendIntake(), subsystem);
    }

    public static Command extendIntakeCross(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.extendIntakeCross(), subsystem);
    }


    public static Command downWhileScore(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.downWhileScore(), subsystem);
    }

    public static Command retractIntakeTransfer(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.retractIntakeTransfer(), subsystem);
    }

    public static Command openIntakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.openIntakeClaw(), subsystem);
    }

    public static Command closeIntakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.closeIntakeClaw(), subsystem);
    }

    public static Command IntakePivotHorizontal(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.IntakePivotHorizontal(), subsystem);
    }

    public static Command openOuttakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.openOuttakeClaw(), subsystem);
    }

    public static Command closeOuttakeClaw(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.closeOuttakeClaw(), subsystem);
    }

    public static Command setHighBucketViper(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.setHighBucketViper(), subsystem);
    }

    public static Command setHighBucket(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.setHighBucket(), subsystem);
    }

    public static Command pickUpPOS(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.setPickupPOS(), subsystem);
    }

    public static Command setArmTempPOS(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.tempPOS(), subsystem);
    }

    public static Command setViperDown(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.viperDown(), subsystem);
    }

    public static Command parkOuttake(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.parkOuttake(), subsystem);
    }

    public static Command wristDown(BucketSideAutoSubsystem subsystem) {
        return new InstantCommand(() -> subsystem.wristDown(), subsystem);
    }

    // Sequences
    public static Command pickupSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                openIntakeClaw(subsystem),
                wristDown(subsystem),
                new WaitCommand(500),
                closeIntakeClaw(subsystem),
                new WaitCommand(500),
                retractIntakeTransfer(subsystem)
        );
    }

    public static Command pickupSequenceCross(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                openIntakeClaw(subsystem),
                wristDown(subsystem),
                new WaitCommand(500),
                closeIntakeClaw(subsystem),
                new WaitCommand(100),
                IntakePivotHorizontal(subsystem),
                new WaitCommand(500),
                retractIntakeTransfer(subsystem)
        );
    }

    public static Command transferSequence(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                startTransfer(subsystem),
                new WaitCommand(2500)
        );
    }

    public static Command pickupAndTransfer(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                pickupSequence(subsystem),
                new WaitCommand(500),
                transferSequence(subsystem)
        );
    }

    public static Command pickupAndTransferCross(BucketSideAutoSubsystem subsystem) {
        return new SequentialCommandGroup(
                pickupSequenceCross(subsystem),
                new WaitCommand(500),
                transferSequence(subsystem)
        );
    }

    public static Command sleep(long milliseconds) {
        return new WaitCommand(milliseconds);
    }
}