//package WasteOrOld.Subsystem;
//
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.util.Constants;
//import pedroPathing.constants.FConstants;
//import pedroPathing.constants.LConstants;
//
//public class MecanumDrive {
//    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
//    private Follower follower;
//    private boolean isAutoActive = false;
//    private double maxOutput = 1.0;
//
//    /**
//     * Constructor for the mecanum drive with follower integration.
//     *
//     * @param hardwareMap HardwareMap from OpMode
//     */
//    public MecanumDrive(HardwareMap hardwareMap) {
//        // Initialize motors
//        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront");
//        frontRight = hardwareMap.get(DcMotorEx.class, "rightFront");
//        backLeft = hardwareMap.get(DcMotorEx.class, "leftRear");
//        backRight = hardwareMap.get(DcMotorEx.class, "rightRear");
//
//        // Set motor directions
//        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
//        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        // Set zero power behavior
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        // Initialize follower
//        Constants.setConstants(FConstants.class, LConstants.class);
//        follower = new Follower(hardwareMap);
//    }
//
//    /**
//     * Sets maximum output power for the drive motors.
//     *
//     * @param power Maximum power value (0.0 to 1.0)
//     */
//    public void setMaxOutput(double power) {
//        this.maxOutput = power;
//    }
//
//    /**
//     * Gets the current power setting.
//     *
//     * @return Current maximum power setting
//     */
//    public double getMaxOutput() {
//        return maxOutput;
//    }
//
//    /**
//     * Gets the follower instance.
//     *
//     * @return The follower object
//     */
//    public Follower getFollower() {
//        return follower;
//    }
//
//    /**
//     * Sets the auto active state.
//     *
//     * @param active Whether auto mode is active
//     */
//    public void setAutoActive(boolean active) {
//        isAutoActive = active;
//    }
//
//    /**
//     * Gets whether auto mode is active.
//     *
//     * @return True if auto mode is active
//     */
//    public boolean isAutoActive() {
//        return isAutoActive;
//    }
//
//    /**
//     * Sets the current pose with offset.
//     *
//     * @param pose New robot pose
//     */
//    public void setCurrentPoseWithOffset(Pose pose) {
//        follower.setCurrentPoseWithOffset(pose);
//    }
//
//    /**
//     * Gets the current robot pose.
//     *
//     * @return Current pose
//     */
//    public Pose getPose() {
//        return follower.getPose();
//    }
//
//    /**
//     * Updates the follower state. Should be called in periodic/loop.
//     */
//    public void update() {
//        follower.update();
//    }
//
//    /**
//     * Sets teleop movement vectors for field-centric control.
//     *
//     * @param forward Forward/backward power (-1.0 to 1.0)
//     * @param strafe Left/right power (-1.0 to 1.0)
//     * @param turn Rotational power (-1.0 to 1.0)
//     * @param fieldCentric Whether to use field-centric control
//     */
//    public void setTeleOpMovementVectors(double forward, double strafe, double turn, boolean fieldCentric) {
//        if (!isAutoActive) {
//            follower.setTeleOpMovementVectors(
//                    forward * maxOutput,
//                    strafe * maxOutput,
//                    turn * maxOutput,
//                    fieldCentric
//            );
//        }
//    }
//
//    /**
//     * Sets the robot's heading to zero.
//     */
//    public void resetHeading() {
//        follower.setCurrentPoseWithOffset(new Pose(
//                follower.getPose().getX(),
//                follower.getPose().getY(),
//                Math.toRadians(0)
//        ));
//    }
//
//    /**
//     * Stops all drive motors.
//     */
//    public void stop() {
//        frontLeft.setPower(0);
//        frontRight.setPower(0);
//        backLeft.setPower(0);
//        backRight.setPower(0);
//    }
//
//    /**
//     * Sets whether the follower is busy with a path.
//     *
//     * @return True if the follower is executing a path
//     */
//    public boolean isBusy() {
//        return follower.isBusy();
//    }
//}