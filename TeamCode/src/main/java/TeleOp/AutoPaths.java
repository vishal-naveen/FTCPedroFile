package TeleOp;

import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

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
        score2ToPickUp.setLinearHeadingInterpolation(score2.getHeading(), pickUp.getHeading());
    }

    public boolean isActive() {
        return autoSequenceActive;
    }

    public void startAuto() {
        autoSequenceActive = true;

        SequentialCommandGroup fullSequence = new SequentialCommandGroup(
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.flick(outtakeSubsystem))
//                Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(1000) // Increased to 1s
                        .andThen(Commands.openClaw(outtakeSubsystem)),
                Commands.pickUpPOS(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(2000)) // Increased to 2s
        );

        autoCommand = fullSequence;
        CommandScheduler.getInstance().schedule(autoCommand);
    }

    public void cancelSequence() {
        if (autoCommand != null) {
            CommandScheduler.getInstance().cancel(autoCommand);
        }
        autoSequenceActive = false;
        follower.breakFollowing();
        follower.startTeleopDrive();
    }

    public void update() {
        CommandScheduler.getInstance().run();
        follower.update();
        if (autoCommand != null && !autoCommand.isScheduled()) {
            autoSequenceActive = false; // Reset state when command finishes or is interrupted
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