//package WasteOrOld.Subsystem;
//
//import com.arcrobotics.ftclib.command.SubsystemBase;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.util.Constants;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.hardware.IMU;
//
//import Positions.RobotPose;
//import pedroPathing.constants.FConstants;
//import pedroPathing.constants.LConstants;
//
//public class DriveSubsystem extends SubsystemBase {
//    private MecanumDrive mecanumDrive;
//    private final Follower follower;
//    private boolean isAutoActive = false;
//    private double power = 1.0;
//
//    public DriveSubsystem(HardwareMap hardwareMap) {
//        // Initialize MecanumDrive
//        mecanumDrive = new MecanumDrive(hardwareMap);
//
//
//        // Initialize follower
//        Constants.setConstants(FConstants.class, LConstants.class);
//        follower = new Follower(hardwareMap);
//        follower.setStartingPose(RobotPose.stopPose);
//    }
//
//    public void setTeleOpMovement(double forward, double strafe, double turn) {
//        if (!isAutoActive) {
//            follower.setTeleOpMovementVectors(
//                    forward * power,
//                    strafe * power,
//                    turn * power,
//                    true  // Always field-centric
//            );
//        }
//    }
//
//
//
//    public boolean isAutoActive() {
//        return isAutoActive;
//    }
//
//    public void setAutoActive(boolean active) {
//        isAutoActive = active;
//    }
//
//    public void resetHeading(IMU imu) {
//        imu.resetYaw();
//    }
//
//    public void resetHeading() {
//        follower.setCurrentPoseWithOffset(new Pose(
//                follower.getPose().getX(),
//                follower.getPose().getY(),
//                Math.toRadians(0)
//        ));
//    }
//
//    public Follower getFollower() {
//        return follower;
//    }
//
//    @Override
//    public void periodic() {
//        follower.update();
//    }
//}