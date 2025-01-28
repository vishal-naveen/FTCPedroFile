package TeleOp;

import static Positions.Commands.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.RobotPose;
import Positions.positions_motor;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name="FieldcentricTELE")
public class FieldcentricTELE extends OpMode {
    private Follower follower;
    private double power = 0.9;

    // Motors
    private DcMotor viperMotor = null;

    // Servos
    private Servo NintakeArm = null;
    private Servo NintakeWrist = null;
    private Servo NintakeWristPivot = null;
    private Servo NintakeClaw = null;
    private Servo OuttakeArm = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;
    private Servo OuttakeClaw = null;

    // State variables
    private boolean isOutakeHorizontal = false;
    private boolean lastRightBumper = false;
    private boolean isWristHorizontal = false;
    private boolean lastLeftBumper = false;
    private ElapsedTime flickTimer = new ElapsedTime();
    private boolean isFlicking = false;
    private int flickState = 0;

    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();
    private boolean transferInProgress = false;

    private int lengthWait = 500;

    private boolean lastDpadUp = false;

    private int pickupState = 0;
    private ElapsedTime pickupTimer = new ElapsedTime();
    private boolean pickupInProgress = false;
    private boolean lastB = false;  // Changed from lastA to lastB for clarity

    @Override
    public void init() {
        // Initialize Pedro Pathing
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(RobotPose.stopPose);



        // Initialize motors
        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Initialize servos
        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");
        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        flickTimer = new ElapsedTime();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {

        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "FL")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "FR")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "BL")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "BR")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Field Centric Drive Control
        if(gamepad1.a) {
            follower.setCurrentPoseWithOffset(new Pose(
                    follower.getPose().getX(),
                    follower.getPose().getY(),
                    Math.toRadians(0)
            ));
        }

        // Update movement with power scaling
        follower.setTeleOpMovementVectors(
                -gamepad1.left_stick_y * power,
                -gamepad1.left_stick_x * power,
                -gamepad1.right_stick_x * power,
                false
        );
        follower.update();

        // Wrist Pivot Controls
        if (gamepad2.left_bumper && !lastLeftBumper) {
            isWristHorizontal = !isWristHorizontal;
            NintakeWristPivot.setPosition(isWristHorizontal ?
                    positions_motor.NIntakeWristPivotHorizontal :
                    positions_motor.NIntakeWristPivotVertical);
        }
        lastLeftBumper = gamepad2.left_bumper;

        if (gamepad2.right_bumper && !lastRightBumper) {
            isOutakeHorizontal = !isOutakeHorizontal;
            OuttakeWristPivot.setPosition(isOutakeHorizontal ?
                    positions_motor.OuttakeWristPivotHighBar:
                    positions_motor.OuttakeWristPivotVertical);
        }
        lastRightBumper = gamepad2.right_bumper;

        // Intake Claw Controls
        if(gamepad2.dpad_left) {
            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
        }
        if(gamepad2.dpad_right) {
            NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
        }

        // Intake Arm Controls
        if(gamepad2.left_stick_y > 0.25) {
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }
        if(gamepad2.left_stick_y < -0.25) {
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedFull);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

//        if(gamepad2.b)
//        {
//        OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBar);
//        OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBar);
//        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
//
//        }

        if(gamepad2.b && !lastB && !pickupInProgress) {
            pickupState = 0;
            pickupTimer.reset();
            pickupInProgress = true;
        }
        lastB = gamepad2.b;

// Outtake Position Controls
        if(pickupInProgress) {
            switch(pickupState) {
                case 0:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBar);
                    if(pickupTimer.milliseconds() > lengthWait) {
                        pickupState = 1;
                        pickupTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBar);
                    if(pickupTimer.milliseconds() > lengthWait) {
                        pickupInProgress = false;
                    }
                    break;
            }
        }

        if(gamepad2.a) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }

        if(gamepad2.x) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
        }

        if(gamepad2.y) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }

        if(gamepad2.dpad_up && !lastDpadUp && !transferInProgress) {
            transferState = 0;
            transferTimer.reset();
            transferInProgress = true;
        }
        lastDpadUp = gamepad2.dpad_up;

        if(transferInProgress) {
            switch(transferState) {
                case 0:
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
                    if(transferTimer.milliseconds() > 500) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                    NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);
                    OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransfer);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    if(transferTimer.milliseconds() > 1000) {
                        transferState = 2;
                        transferTimer.reset();
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
                    if(transferTimer.milliseconds() > 500) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if(transferTimer.milliseconds() > 500) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
                    if(transferTimer.milliseconds() > 500) {
                        transferState = 5;
                        transferTimer.reset();
                    }
                    break;

                case 5:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    transferInProgress = false;
                    break;
            }
        }


        if(gamepad1.dpad_left)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
        }

        if(gamepad1.dpad_right)
        {
            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
        }

        if(gamepad1.dpad_up)
        {
            NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);
        }

        // Outtake Claw Controls
        if(gamepad2.left_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
        }
        if(gamepad2.right_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
        }

        // Intake Wrist Controls
        if(gamepad2.right_stick_y > 0.25) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }
        if(gamepad2.right_stick_y < -0.25) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        }
        if(gamepad2.back) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }


        // Telemetry
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Power", power);

        telemetry.addData("NintakeArm", NintakeArm.getPosition());
        telemetry.addData("NintakeWrist", NintakeWrist.getPosition());
        telemetry.addData("NintakeWristPivot", NintakeWristPivot.getPosition());
        telemetry.addData("NintakeClaw", NintakeClaw.getPosition());
        telemetry.addData("OuttakeArm", OuttakeArm.getPosition());
        telemetry.addData("OuttakeWrist", OuttakeWrist.getPosition());
        telemetry.addData("OuttakeWristPivot", OuttakeWristPivot.getPosition());
        telemetry.addData("OuttakeClaw", OuttakeClaw.getPosition());
        telemetry.addData("Viper: ", viperMotor.getCurrentPosition());
        telemetry.update();
    }
}