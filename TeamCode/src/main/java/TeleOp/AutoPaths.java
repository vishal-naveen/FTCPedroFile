package TeleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutoPaths {
    private final Follower follower;
    private final AnalogInput ultra;
    private final Telemetry telemetry;
    private Path scorePath;
    private Path pickPath;
    private boolean autoSequenceActive = false;
    private int currentCycle = 0;
    private int maxCycles = 3;
    private ElapsedTime pathTimer = new ElapsedTime();
    private static final double PATH_TIMEOUT = 3000; // 3 seconds timeout
    private boolean isFollowingPath = false;
    private Path currentPath = null;

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Telemetry telemetry) {
        this.follower = follower;
        this.ultra = hardwareMap.get(AnalogInput.class, "ultra");
        this.telemetry = telemetry;
        initializePaths();
    }

    private void initializePaths() {
        Pose startPose = new Pose(0, 0, Math.toRadians(0));
        Pose scorePose = new Pose(10, 10, Math.toRadians(0));

        scorePath = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
        scorePath.setConstantHeadingInterpolation(startPose.getHeading());
        pickPath = new Path(new BezierLine(new Point(scorePose), new Point(startPose)));
        pickPath.setConstantHeadingInterpolation(startPose.getHeading());
    }

    public double getDistanceInches() {
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
            follower.followPath(scorePath);
            isFollowingPath = true;
            currentPath = scorePath;
            pathTimer.reset();
        }
    }

    public void startAuto() {
        autoSequenceActive = true;
        currentCycle = 0;
        follower.startTeleopDrive();
    }

    public void stopAuto() {
        autoSequenceActive = false;
        currentCycle = 0;
        follower.breakFollowing();
        follower.startTeleopDrive();
        isFollowingPath = false;
        currentPath = null;
    }

    private boolean isPathComplete() {
        return !follower.isBusy() ||
                getDistanceInches() <= 10 ||
                pathTimer.milliseconds() >= PATH_TIMEOUT;
    }

    public void executeScoreCycle() {
        if (!isFollowingPath) {
            // Start following score path
            follower.followPath(scorePath);
            isFollowingPath = true;
            currentPath = scorePath;
            pathTimer.reset();
        } else if (isPathComplete()) {
            if (currentPath == scorePath) {
                // Switch to pick path after a small delay
                follower.followPath(pickPath);
                currentPath = pickPath;
                pathTimer.reset();
            } else if (currentPath == pickPath) {
                // Cycle complete
                currentCycle++;
                if (currentCycle >= maxCycles) {
                    stopAuto();
                } else {
                    // Start next cycle
                    follower.followPath(scorePath);
                    currentPath = scorePath;
                    pathTimer.reset();
                }
            }
        }
    }

    public void update() {
        if (isActive()) { // Changed to check autoSequenceActive directly
            follower.update();
            executeScoreCycle(); // Always call this to handle the sequence

        }
    }

    public void setManualDrive(double x, double y, double rotate, double power) {
        if (!isActive()) {
            follower.setTeleOpMovementVectors(
                    y * power,
                    x * power,
                    rotate * power,
                    true
            );
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