// DriveSubsystem.java
package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import Positions.RobotPose;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

public class DriveSubsystem {
    private final Follower follower;
    private final CommandOpMode opMode;
    private final double power = 1.0;
    private final DcMotorEx leftFront;
    private final DcMotorEx rightFront;
    private final DcMotorEx leftRear;
    private final DcMotorEx rightRear;
    private Path scorePath;
    private Path pickPath;

    public DriveSubsystem(CommandOpMode opMode) {
        this.opMode = opMode;
        this.follower = new Follower(opMode.hardwareMap);

        leftFront = opMode.hardwareMap.get(DcMotorEx.class, "FL");
        rightFront = opMode.hardwareMap.get(DcMotorEx.class, "FR");
        leftRear = opMode.hardwareMap.get(DcMotorEx.class, "BL");
        rightRear = opMode.hardwareMap.get(DcMotorEx.class, "BR");
    }

    public void initialize() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower.setStartingPose(RobotPose.stopPose);

        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        follower.startTeleopDrive();  // Explicitly start in teleop mode

        opMode.schedule(new RunCommand(follower::update));
    }

    public void updateDrive(Gamepad gamepad) {
        if (!isAutoActive()) {
            if (gamepad.a) {
                follower.setCurrentPoseWithOffset(new Pose(
                        follower.getPose().getX(),
                        follower.getPose().getY(),
                        Math.toRadians(0)
                ));
            }

            follower.setTeleOpMovementVectors(
                    -gamepad.left_stick_y * power,
                    -gamepad.left_stick_x * power,
                    -gamepad.right_stick_x * power,
                    false
            );
        }
    }

    public void initializePaths() {
        Pose startPose = new Pose(30, 0, Math.toRadians(0));
        Pose scorePose = new Pose(0, 0, Math.toRadians(0));

        follower.setPose(startPose);

        scorePath = new Path(new BezierCurve(new Point(startPose), new Point(scorePose)));
        scorePath.setConstantHeadingInterpolation(startPose.getHeading());
        pickPath = new Path(new BezierCurve(new Point(scorePose), new Point(startPose)));
        pickPath.setConstantHeadingInterpolation(startPose.getHeading());
    }

    public Follower getFollower() {
        return follower;
    }

    public Path getScorePath() {
        return scorePath;
    }

    public Path getPickPath() {
        return pickPath;
    }

    public boolean hasJoystickInput(Gamepad gamepad) {
        return Math.abs(gamepad.left_stick_x) > 0.1 ||
                Math.abs(gamepad.left_stick_y) > 0.1 ||
                Math.abs(gamepad.right_stick_x) > 0.1;
    }

    public boolean isAutoActive() {
        return follower.isBusy();
    }
}