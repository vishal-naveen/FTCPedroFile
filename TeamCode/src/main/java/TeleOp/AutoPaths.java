package TeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import Positions.Commands;
import Subsystem.OuttakeSubsystem;

public class AutoPaths {
    private final Follower follower;
    private final AnalogInput ultra;
    private final Telemetry telemetry;
    private final OuttakeSubsystem outtakeSubsystem;
    private Path scorePath;
    private Path pickPath;
    private boolean autoSequenceActive = false;
    private int currentCycle = 0;
    private ElapsedTime pathTimer = new ElapsedTime();
    private static final double PATH_TIMEOUT = 3000;
    private static final double STOP_DISTANCE = 5.0;
    private static final double JOYSTICK_THRESHOLD = 0.1;
    private static final double JOYSTICK_DEBOUNCE_TIME = 100; // Reduced for responsiveness
    private boolean isFollowingPath = false;
    private Path currentPath = null;
    private Gamepad gamepad;
    private ElapsedTime joystickDebounceTimer = new ElapsedTime();
    private boolean isTransitioningToManual = false;
    private double transitionProgress = 0.0;
    private static final double TRANSITION_DURATION = 500;

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Telemetry telemetry, Gamepad gamepad) throws IllegalArgumentException {
        if (hardwareMap == null || follower == null || telemetry == null || gamepad == null) {
            throw new IllegalArgumentException("Inputs cannot be null");
        }
        this.follower = follower;
        this.telemetry = telemetry;
        this.gamepad = gamepad;
        try {
            this.ultra = hardwareMap.get(AnalogInput.class, "ultra");
            this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);
        } catch (Exception e) {
            telemetry.addData("Error", "Failed to initialize hardware: " + e.getMessage());
            throw new IllegalStateException("Hardware initialization failed", e);
        }
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

    public double getDistanceInches() {
        if (ultra == null) {
            telemetry.addData("Warning", "Ultrasonic sensor not initialized");
            return Double.MAX_VALUE;
        }
        return (100 * (ultra.getVoltage() / 3.3)) / 2.54;
    }

    public boolean isActive() {
        return autoSequenceActive;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void startFollowingScorePath() {
        if (!isFollowingPath) {
            isFollowingPath = true;
            currentPath = scorePath;
            pathTimer.reset();
            if (!followPathWithDistanceCheck(scorePath)) {
                transitionToManual();
            }
        }
    }

    public void startAuto() {
        autoSequenceActive = true;
        currentCycle = 0;
        follower.startTeleopDrive();
        isTransitioningToManual = false;
        transitionProgress = 0.0;
    }

    public void stopAuto() {
        autoSequenceActive = false;
        currentCycle = 0;
        follower.breakFollowing();
        follower.startTeleopDrive();
        isFollowingPath = false;
        currentPath = null;
        isTransitioningToManual = false;
        transitionProgress = 0.0;
    }

    public boolean isTooClose() {
        return getDistanceInches() <= STOP_DISTANCE;
    }

    private boolean isJoystickActive() {
        return Math.abs(gamepad.left_stick_x) > JOYSTICK_THRESHOLD ||
                Math.abs(gamepad.left_stick_y) > JOYSTICK_THRESHOLD ||
                Math.abs(gamepad.right_stick_x) > JOYSTICK_THRESHOLD;
    }

    private boolean isPathComplete() {
        return !follower.isBusy() || pathTimer.milliseconds() >= PATH_TIMEOUT;
    }

    //issue. telegram check
    private boolean followPathWithDistanceCheck(Path path) {
        follower.followPath(path);
        while (follower.isBusy() && !isJoystickActive()) {
            follower.update();
            telemetry.addData("Distance (inches)", "%.2f", getDistanceInches());
            telemetry.addData("Joystick Active", isJoystickActive());
            telemetry.update();

            if (path == pickPath && isTooClose()) {
                follower.breakFollowing();
                return false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        if (isJoystickActive()) {
            follower.breakFollowing();
            return false;
        }
        return true;
    }

    //ts buns. still working
    //NEED TO FIX  <----------
    private void transitionToManual() {
        if (!isTransitioningToManual) {
            isTransitioningToManual = true;
            joystickDebounceTimer.reset();
        }
    }

    public void executeScoreCycle() {
        if (!isFollowingPath) {
            isFollowingPath = true;
            currentPath = scorePath;
            pathTimer.reset();
            if (!followPathWithDistanceCheck(scorePath)) {
                stopAuto();
                return;
            }
        } else if (isPathComplete()) {
            if (currentPath == scorePath) {
                currentPath = pickPath;
                Commands.sleep(10);
                Commands.openClaw(outtakeSubsystem);
                pathTimer.reset();
                if (!followPathWithDistanceCheck(pickPath)) {

                    currentCycle++;
                    isFollowingPath = false;
                    return;
                }
            } else if (currentPath == pickPath) {
                currentCycle++;
                currentPath = scorePath;
                pathTimer.reset();
                isFollowingPath = false;
                if (!followPathWithDistanceCheck(scorePath)) {
                    stopAuto();
                    return;
                }
            }
        }
    }

    public void update() {
        if (isActive()) {
            double distance = getDistanceInches();
            follower.update();
            telemetry.addData("Distance (inches)", "%.2f", distance);
            telemetry.addData("Is Too Close", isTooClose());
            telemetry.addData("Joystick Active", isJoystickActive());
            telemetry.addData("Transition Progress", "%.2f", transitionProgress);
            telemetry.addData("Current Cycle", currentCycle);

            if (isJoystickActive()) {
                if (!isTransitioningToManual) {
                    transitionToManual();
                }
            }

            if (isTransitioningToManual) {
                if (joystickDebounceTimer.milliseconds() >= JOYSTICK_DEBOUNCE_TIME) {
                    transitionProgress += (1000.0 / TRANSITION_DURATION) * (pathTimer.milliseconds() / 1000.0);
                    pathTimer.reset();
                    if (transitionProgress >= 1.0) {
                        stopAuto();
                        return;
                    }
                    double autoWeight = 1.0 - transitionProgress;
                    double manualWeight = transitionProgress;
                    double manualX = gamepad.left_stick_x * manualWeight;
                    double manualY = gamepad.left_stick_y * manualWeight;
                    double manualRotate = gamepad.right_stick_x * manualWeight;
                    follower.setTeleOpMovementVectors(manualY, manualX, manualRotate, true);
                }
            } else {
                executeScoreCycle();
            }
        }
    }

    public void setManualDrive(double x, double y, double rotate, double power) {
        if (!isActive() || isTransitioningToManual) {
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