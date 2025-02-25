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

import org.firstinspires.ftc.robotcore.external.Telemetry;

import Positions.Commands;
import Subsystem.OuttakeSubsystem;
import static Subsystem.Push3Specimen.*;

public class AutoPaths {
    private final Follower follower;
    private final Telemetry telemetry;
    private final OuttakeSubsystem outtakeSubsystem;
    private final Gamepad gamepad;
    private boolean autoSequenceActive = false;
    private SequentialCommandGroup autoCommand;

    private final Pose pickUp = new Pose(4, 28.75, Math.toRadians(0));
    private final Pose scoreBefore1 = new Pose(35.5, 78.2, Math.toRadians(0));
    private final Pose score1 = new Pose(40, 78.2, Math.toRadians(0));
    private final Pose scoreBefore2 = new Pose(35.5, 76.7, Math.toRadians(0));  // 78.2 - 1.5
    private final Pose score2 = new Pose(40, 76.7, Math.toRadians(0));
    private final Pose scoreBefore3 = new Pose(35.5, 75.2, Math.toRadians(0));  // 76.7 - 1.5
    private final Pose score3 = new Pose(40, 75.2, Math.toRadians(0));

    // Define paths
    private final Path startToScoreBefore1;
    private final Path scoreBefore1ToScore1;
    private final Path score1ToPickUp;
    private final Path pickUpToScoreBefore2;
    private final Path scoreBefore2ToScore2;
    private final Path score2ToPickUp;

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Telemetry telemetry, Gamepad gamepad) throws IllegalArgumentException {
        if (hardwareMap == null || follower == null || telemetry == null || gamepad == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }
        this.follower = follower;
        follower.setMaxPower(0.9);
        this.telemetry = telemetry;
        this.gamepad = gamepad;
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);

        startToScoreBefore1 = new Path(new BezierLine(
                new Point(follower.getPose()),
                new Point(scoreBefore1)
        ));
        startToScoreBefore1.setLinearHeadingInterpolation(
                follower.getPose().getHeading(),
                scoreBefore1.getHeading()
        );

        scoreBefore1ToScore1 = new Path(new BezierLine(
                new Point(scoreBefore1),
                new Point(score1)
        ));
        scoreBefore1ToScore1.setLinearHeadingInterpolation(
                scoreBefore1.getHeading(),
                score1.getHeading()
        );

        score1ToPickUp = new Path(new BezierLine(
                new Point(score1),
                new Point(pickUp)
        ));
        score1ToPickUp.setLinearHeadingInterpolation(
                score1.getHeading(),
                pickUp.getHeading()
        );

        pickUpToScoreBefore2 = new Path(new BezierLine(
                new Point(pickUp),
                new Point(scoreBefore2)
        ));
        pickUpToScoreBefore2.setLinearHeadingInterpolation(
                pickUp.getHeading(),
                scoreBefore2.getHeading()
        );

        scoreBefore2ToScore2 = new Path(new BezierLine(
                new Point(scoreBefore2),
                new Point(score2)
        ));
        scoreBefore2ToScore2.setLinearHeadingInterpolation(
                scoreBefore2.getHeading(),
                score2.getHeading()
        );

        score2ToPickUp = new Path(new BezierLine(
                new Point(score2),
                new Point(pickUp)
        ));
        score2ToPickUp.setLinearHeadingInterpolation(
                score2.getHeading(),
                pickUp.getHeading()
        );
    }
    public boolean isActive() {
        return autoSequenceActive;
    }

    public void startAuto() {
        autoSequenceActive = true;

        SequentialCommandGroup fullSequence = new SequentialCommandGroup(
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.flick(outtakeSubsystem)),
                Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(300)
                        .andThen(Commands.openClaw(outtakeSubsystem)),
                Commands.pickUpPOS(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, score2ToPickUpBefore).withTimeout(1500))
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(100))
        );

        SequentialCommandGroup secondSequence = new SequentialCommandGroup(
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, startToScoreBefore1))
                        .andThen(Commands.followPath(follower, scoreBefore1ToScore1))
                        .andThen(Commands.flick(outtakeSubsystem))
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(300))
                        .andThen(Commands.openClaw(outtakeSubsystem))
                        .andThen(Commands.pickUpPOS(outtakeSubsystem))
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(1500))
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

        telemetry.addData("Auto Active", autoSequenceActive);
        telemetry.addData("Follower Busy", follower.isBusy());
        telemetry.update();
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