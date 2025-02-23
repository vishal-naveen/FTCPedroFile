package TeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import Positions.Commands;
import Subsystem.OuttakeSubsystem;

public class AutoPaths {
    private final Follower follower;
    private final Telemetry telemetry;
    private final OuttakeSubsystem outtakeSubsystem;
    private Path scorePath;
    private Path pickPath;
    private boolean autoSequenceActive = false;
    private int currentCycle = 0;
    private ElapsedTime pathTimer = new ElapsedTime();
    private static final double PATH_TIMEOUT = 3000;
    private static final int WAIT_TIME = 100;
    private Gamepad gamepad;
    private int currentState = 0; // 0 = idle, 1 = scoring, 2 = waiting to open, 3 = picking
    private long lastStateChangeTime = 0;
    private int maxCycles = 5;

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Telemetry telemetry, Gamepad gamepad) throws IllegalArgumentException {
        if (hardwareMap == null || follower == null || telemetry == null || gamepad == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }
        this.follower = follower;
        this.telemetry = telemetry;
        this.gamepad = gamepad;
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);
        initializePaths();
    }

    private void initializePaths() {
        Pose startPose = new Pose(0, 0, Math.toRadians(0));
        Pose scorePose = new Pose(20, 20, Math.toRadians(0));
        scorePath = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
        scorePath.setConstantHeadingInterpolation(startPose.getHeading());
        pickPath = new Path(new BezierLine(new Point(scorePose), new Point(startPose)));
        pickPath.setConstantHeadingInterpolation(startPose.getHeading());
    }

    public boolean isActive() {
        return autoSequenceActive;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void startAuto() {
        autoSequenceActive = true;
        currentCycle = 0;
        currentState = 1;
        follower.followPath(scorePath);
        lastStateChangeTime = System.currentTimeMillis();
    }

    public void cancelSequence() {
        autoSequenceActive = false;
        currentCycle = 0;
        currentState = 0;
        follower.breakFollowing();
        follower.startTeleopDrive();
    }

    private void handleAutomatedSequence() {
        long currentTime = System.currentTimeMillis();

        switch (currentState) {
            case 1: // Scoring
                if (!follower.isBusy()) {
                    currentState = 2;
                    lastStateChangeTime = currentTime;
                }
                break;

            case 2: // Waiting to open
                if (currentTime - lastStateChangeTime >= WAIT_TIME) {
                    Commands.openClaw(outtakeSubsystem);
                    currentState = 3;
                    follower.followPath(pickPath);
                    lastStateChangeTime = currentTime;
                }
                break;

            case 3: // Picking
                if (!follower.isBusy()) {
                    currentCycle++;
                    if (currentCycle >= maxCycles) {
                        cancelSequence();
                    } else {
                        currentState = 1;
                        follower.followPath(scorePath);
                        lastStateChangeTime = currentTime;
                    }
                }
                break;
        }
    }

    public void update() {
        if (isActive()) {
            handleAutomatedSequence();
        }
        follower.update();

        telemetry.addData("Current State", currentState);
        telemetry.addData("Current Cycle", currentCycle);
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