package TeleOp;

import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.ParallelRaceGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.Commands;
import Subsystem.OuttakeSubsystem;
import static Subsystem.Push3Specimen.*;

public class AutoPaths {
    private final Follower follower;
    private final OuttakeSubsystem outtakeSubsystem;
    private final Gamepad gamepad;
    private boolean autoSequenceActive = false;
    private SequentialCommandGroup autoCommand;

    private final Pose pickUp = new Pose(12, 28.75, Math.toRadians(0));
    private final Pose scoreBefore1 = new Pose(35.5, 75.2, Math.toRadians(0));
    private final Pose score1 = new Pose(40, 75.2, Math.toRadians(0));
    private final Pose scoreBefore2 = new Pose(32.5, 76.7, Math.toRadians(0));
    private final Pose score2 = new Pose(40, 76.7, Math.toRadians(0));
    private final Pose scoreBefore3 = new Pose(35.5, 75.2, Math.toRadians(0));
    private final Pose score3 = new Pose(40, 75.2, Math.toRadians(0));

    private final Path startToScoreBefore1;
    private final Path scoreBefore1ToScore1;
    private final Path score1ToPickUp;
    private final Path pickUpToScoreBefore2;
    private final Path scoreBefore2ToScore2;
    private final Path score2ToPickUp;

    // Use an explicit time marker for drive-forward state
    private ElapsedTime driveForwardTimer = new ElapsedTime();
    private long driveUntilTime = 0; // Time in ms when drive should stop

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Gamepad gamepad) throws IllegalArgumentException {
        if (hardwareMap == null || follower == null || gamepad == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }
        this.follower = follower;
        this.gamepad = gamepad;
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, null, this.follower);

        startToScoreBefore1 = new Path(new BezierLine(
                new Point(pickUp),
                new Point(scoreBefore1)
        ));
        startToScoreBefore1.setConstantHeadingInterpolation(Math.toRadians(0));

        scoreBefore1ToScore1 = new Path(new BezierLine(
                new Point(scoreBefore1),
                new Point(score1)
        ));
        scoreBefore1ToScore1.setConstantHeadingInterpolation(Math.toRadians(0));

        score1ToPickUp = new Path(new BezierLine(
                new Point(score1),
                new Point(pickUp)
        ));
        score1ToPickUp.setConstantHeadingInterpolation(Math.toRadians(0));

        pickUpToScoreBefore2 = new Path(new BezierLine(
                new Point(pickUp),
                new Point(scoreBefore2)
        ));
        pickUpToScoreBefore2.setConstantHeadingInterpolation(Math.toRadians(0));

        scoreBefore2ToScore2 = new Path(new BezierLine(
                new Point(scoreBefore2),
                new Point(score2)
        ));
        scoreBefore2ToScore2.setConstantHeadingInterpolation(Math.toRadians(0));

        score2ToPickUp = new Path(new BezierLine(
                new Point(score2),
                new Point(pickUp)
        ));
        score2ToPickUp.setConstantHeadingInterpolation(Math.toRadians(0));

        // Initialize timer
        driveForwardTimer.reset();
    }

    public boolean isActive() {
        return autoSequenceActive;
    }

    // A specialized two-part command approach for the forward drive
    private class TwoPhaseDriveCommand extends CommandBase {
        private final int durationMs;
        private boolean drivingForward = false;
        private boolean completed = false;
        private final ElapsedTime commandTimer = new ElapsedTime();

        public TwoPhaseDriveCommand(int durationMs) {
            this.durationMs = durationMs;
        }

        @Override
        public void initialize() {
            commandTimer.reset();
            completed = false;
            drivingForward = false;

            // First phase: Stop any ongoing movement
            follower.breakFollowing();
            follower.setTeleOpMovementVectors(0, 0, 0, false);
            follower.startTeleopDrive();
        }

        @Override
        public void execute() {
            // Phase 1: Wait 20ms to ensure any previous movement has stopped
            if (!drivingForward && commandTimer.milliseconds() >= 20) {
                // Start movement and set the global timer
                driveForwardTimer.reset();
                driveUntilTime = System.currentTimeMillis() + durationMs;

                // Mark driving flag
                drivingForward = true;

                // Start forward movement at 0.3 speed (slower than before)
                follower.breakFollowing(); // Double check
                follower.setTeleOpMovementVectors(0.3, 0, 0, false);
            }

            // Phase 2: Check if we've reached the drive duration
            if (drivingForward && commandTimer.milliseconds() >= durationMs + 20) {
                // Stop movement
                follower.setTeleOpMovementVectors(0, 0, 0, false);
                follower.breakFollowing(); // Double check
                completed = true;

                // Reset global drive flag
                driveUntilTime = 0;
            }
        }

        @Override
        public boolean isFinished() {
            return completed;
        }

        @Override
        public void end(boolean interrupted) {
            // Always ensure we stop the robot
            follower.breakFollowing();
            follower.setTeleOpMovementVectors(0, 0, 0, false);

            // Reset global drive flag if interrupted
            if (interrupted) {
                driveUntilTime = 0;
            }
        }
    }

    public void startAuto() {
        autoSequenceActive = true;

        SequentialCommandGroup fullSequence = new SequentialCommandGroup(
                // First cycle
                Commands.closeClawThenScore(outtakeSubsystem),
                Commands.followPath(follower, pickUpToScoreBefore2),
                Commands.flick(outtakeSubsystem),
                new TwoPhaseDriveCommand(300),  // Two-phase approach for more reliable timing
                Commands.openClaw(outtakeSubsystem),
                Commands.pickUpPOS(outtakeSubsystem)
                        .andThen(new ParallelRaceGroup(
                                Commands.followPath(follower, score2ToPickUp),
                                new WaitCommand(2000)
                        ).andThen(new InstantCommand(() -> {
                            // Force stopping when path is done or timed out
                            follower.breakFollowing();
                            follower.setTeleOpMovementVectors(0, 0, 0, false);
                        }))),

                // Second cycle
                Commands.closeClawThenScore(outtakeSubsystem),
                Commands.followPath(follower, pickUpToScoreBefore2),
                Commands.flick(outtakeSubsystem),
                new TwoPhaseDriveCommand(300),  // Two-phase approach for more reliable timing
                Commands.openClaw(outtakeSubsystem),
                Commands.pickUpPOS(outtakeSubsystem)
                        .andThen(new ParallelRaceGroup(
                                Commands.followPath(follower, score2ToPickUp),
                                new WaitCommand(2000)
                        ).andThen(new InstantCommand(() -> {
                            // Force stopping when path is done or timed out
                            follower.breakFollowing();
                            follower.setTeleOpMovementVectors(0, 0, 0, false);
                        }))),

                // Third cycle
                Commands.closeClawThenScore(outtakeSubsystem),
                Commands.followPath(follower, pickUpToScoreBefore2),
                Commands.flick(outtakeSubsystem),
                new TwoPhaseDriveCommand(300),  // Two-phase approach for more reliable timing
                Commands.openClaw(outtakeSubsystem),
                Commands.pickUpPOS(outtakeSubsystem)
                        .andThen(new ParallelRaceGroup(
                                Commands.followPath(follower, score2ToPickUp),
                                new WaitCommand(2000)
                        ).andThen(new InstantCommand(() -> {
                            // Force stopping when path is done or timed out
                            follower.breakFollowing();
                            follower.setTeleOpMovementVectors(0, 0, 0, false);
                        })))
        );

        autoCommand = fullSequence;
        CommandScheduler.getInstance().schedule(autoCommand);
    }

    public void cancelSequence() {
        if (autoCommand != null) {
            CommandScheduler.getInstance().cancel(autoCommand);
        }
        autoSequenceActive = false;
        driveUntilTime = 0; // Reset drive timer flag
        follower.breakFollowing();
        follower.setTeleOpMovementVectors(0, 0, 0, false);
        follower.startTeleopDrive();
    }

    public void update() {
        CommandScheduler.getInstance().run();

        // Additional failsafe: Always check the global drive timeout
        if (driveUntilTime > 0 && System.currentTimeMillis() >= driveUntilTime) {
            // Stop movement and reset flag
            follower.breakFollowing();
            follower.setTeleOpMovementVectors(0, 0, 0, false);
            driveUntilTime = 0;
        }

        // Update follower
        follower.update();

        // Check for sequence completion
        if (autoCommand != null && !autoCommand.isScheduled()) {
            autoSequenceActive = false;
            driveUntilTime = 0; // Reset drive timer flag
            follower.breakFollowing();
            follower.setTeleOpMovementVectors(0, 0, 0, false);
            follower.startTeleopDrive();
        }
    }

    public void setManualDrive(double x, double y, double rotate, double power) {
        if (!isActive()) {
            follower.setTeleOpMovementVectors(y * power, x * power, rotate * power, false);
        }
    }

    public void resetHeading() {
        follower.setCurrentPoseWithOffset(new Pose(
                follower.getPose().getX(),
                follower.getPose().getY(),
                Math.toRadians(0)
        ));
    }
}