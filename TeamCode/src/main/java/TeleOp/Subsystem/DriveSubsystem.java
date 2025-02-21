package TeleOp.Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import Positions.RobotPose;

public class DriveSubsystem extends SubsystemBase {
    private final Follower follower;
    private boolean isAutoActive = false;
    private double power = 1.0;

    public DriveSubsystem(HardwareMap hardwareMap) {
        follower = new Follower(hardwareMap);
        follower.setStartingPose(RobotPose.stopPose);
    }

    public void setTeleOpMovement(double forward, double strafe, double turn) {
        follower.setTeleOpMovementVectors(forward * power, strafe * power, turn * power, true);
    }

    public boolean isAutoActive() {
        return isAutoActive;
    }

    public void setAutoActive(boolean active) {
        isAutoActive = active;
    }

    public void resetHeading() {
        follower.setCurrentPoseWithOffset(new Pose(
                follower.getPose().getX(),
                follower.getPose().getY(),
                Math.toRadians(0)
        ));
    }

    @Override
    public void periodic() {
        follower.update();
    }
}
