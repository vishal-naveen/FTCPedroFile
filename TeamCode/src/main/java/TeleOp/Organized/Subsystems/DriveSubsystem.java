// DriveSubsystem.java
package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
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
    private final DcMotorEx[] driveMotors;

    public DriveSubsystem(CommandOpMode opMode) {
        this.opMode = opMode;
        this.follower = new Follower(opMode.hardwareMap);

        driveMotors = new DcMotorEx[] {
                opMode.hardwareMap.get(DcMotorEx.class, "FL"),
                opMode.hardwareMap.get(DcMotorEx.class, "FR"),
                opMode.hardwareMap.get(DcMotorEx.class, "BL"),
                opMode.hardwareMap.get(DcMotorEx.class, "BR")
        };
    }

    public void initialize() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower.setStartingPose(RobotPose.stopPose);

        for (DcMotorEx motor : driveMotors) {
            motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        }

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

    public Follower getFollower() {
        return follower;
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