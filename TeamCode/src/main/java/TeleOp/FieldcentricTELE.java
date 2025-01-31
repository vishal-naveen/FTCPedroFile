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
    private double power = 1;

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

    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;

    private boolean isViperAtTarget = false;
    private static final int POSITION_TOLERANCE = 20;
    private static final int POSITION_TOLERANCE_GROUND = 45;

    private int viperDownState = 0;
    private ElapsedTime viperDownTimer = new ElapsedTime();
    private boolean viperDownInProgress = false;
    private boolean lastStart = false;

    private static final double VIPER_HOLDING_POWER = 0.1;  // Power to maintain position

    private ElapsedTime groundPowerTimer = new ElapsedTime();
    private boolean isGroundTimerActive = false;
    private static final double GROUND_POWER_TIMEOUT = 5000; // 5 seconds in milliseconds

    private int lastViperPosition = 0;
    private ElapsedTime stallTimer = new ElapsedTime();
    private static final int STALL_POSITION_THRESHOLD = 5;  // How many ticks of movement to consider stalled
    private static final double STALL_TIME_THRESHOLD = 250;  // How long to wait before considering stalled (in ms)
    private boolean isStallCheckActive = false;

    private boolean isManualResetActive = false;
    private boolean lastLeftBumper1 = false; // for gamepad1 left bumper

    private int intakeCloseState = 0;
    private ElapsedTime intakeCloseTimer = new ElapsedTime();
    private boolean intakeCloseInProgress = false;

    private boolean isArmExtended = false;

    private boolean isPivotHorizontal = false;


    private void updatePivotState() {
        double pivotPosition = NintakeWristPivot.getPosition();
        isPivotHorizontal = Math.abs(pivotPosition - positions_motor.NIntakeWristPivotHorizontal) < 0.05;
    }
    private void updateArmExtensionState() {
        double armPosition = NintakeArm.getPosition();
        // Check if arm is in extended positions
        isArmExtended = (armPosition == positions_motor.NIntakeArmExtendedFull ||
                armPosition == positions_motor.NIntakeArmExtendedBack);
    }

    private void cancelGroundTimer() {
        if (isGroundTimerActive) {
            isGroundTimerActive = false;
            groundPowerTimer.reset();
        }
    }

    private void updateViperMotorState() {
        if (viperMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = viperMotor.getCurrentPosition();
            int targetPos = viperMotor.getTargetPosition();

            // Check for stall when moving down
            if (targetPos == positions_motor.VIPER_GROUND) {
                // If we just started moving down, start stall detection
                if (!isStallCheckActive) {
                    isStallCheckActive = true;
                    lastViperPosition = currentPos;
                    stallTimer.reset();
                }
                // Check if we've been in roughly the same position for a while
                else if (stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
                    if (Math.abs(currentPos - lastViperPosition) < STALL_POSITION_THRESHOLD) {
                        // Motor is stalled - reset encoders and set power to 0
                        viperMotor.setPower(0);
                        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        isStallCheckActive = false;
                        cancelGroundTimer(); // Cancel any active ground timer
                        return; // Exit the method since we're resetting
                    }
                    // Reset stall check
                    lastViperPosition = currentPos;
                    stallTimer.reset();
                }
            } else {
                // Not moving to ground, disable stall check
                isStallCheckActive = false;
            }

            // Normal position control for non-ground positions
            if (Math.abs(currentPos - targetPos) <= POSITION_TOLERANCE) {
                isViperAtTarget = true;
                viperMotor.setPower(VIPER_HOLDING_POWER);
            } else {
                isViperAtTarget = false;
                if (targetPos == positions_motor.VIPER_GROUND) {
                    viperMotor.setPower(1);
                } else {
                    viperMotor.setPower(1);
                }
            }
        }
    }

    private void checkManualReset() {
        if (isManualResetActive) {
            int currentPos = viperMotor.getCurrentPosition();

            // Check if position hasn't changed significantly
            if (stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
                if (Math.abs(currentPos - lastViperPosition) < STALL_POSITION_THRESHOLD) {
                    // Motor is stalled - reset encoders and set power to 0
                    viperMotor.setPower(0);
                    // Reset and re-enable encoders properly
                    viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    isManualResetActive = false;
                    return;
                }
                // Update last position and reset timer
                lastViperPosition = currentPos;
                stallTimer.reset();
            }
        }
    }

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

        if(gamepad2.dpad_left && !lastDpadLeft) {
            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
        }
        if(gamepad2.dpad_right && !lastDpadRight && !intakeCloseInProgress) {
            intakeCloseState = 0;
            intakeCloseTimer.reset();
            intakeCloseInProgress = true;
        }
        lastDpadLeft = gamepad2.dpad_left;
        lastDpadRight = gamepad2.dpad_right;

        if(intakeCloseInProgress) {
            switch(intakeCloseState) {
                case 0:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
                    if(intakeCloseTimer.milliseconds() > 200) {
                        intakeCloseState = 1;
                        intakeCloseTimer.reset();
                    }
                    break;

                case 1:
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    intakeCloseInProgress = false;
                    break;
            }
        }

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
                    if(pickupTimer.milliseconds() > 250) {
                        pickupState = 1;
                        pickupTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBar);
                    if(pickupTimer.milliseconds() > 10) {
                        pickupInProgress = false;
                    }
                    break;
            }
        }

        if(gamepad2.a) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }

        if(gamepad2.x) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
        }

        if(gamepad2.dpad_down) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }

        if(gamepad2.y) {
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }

        if(gamepad2.touchpad && !lastDpadUp && !transferInProgress) {
            transferState = 0;
            transferTimer.reset();
            transferInProgress = true;
        }
        lastDpadUp = gamepad2.touchpad;

        if(transferInProgress) {
            cancelGroundTimer();
            switch(transferState) {
                case 0:
                    updateArmExtensionState(); // Update arm state before starting transfer
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
                    NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);

                    if(transferTimer.milliseconds() > 500) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    if(isArmExtended) {
                        if(transferTimer.milliseconds() <= 450) {
                            // Do nothing and wait until 300ms has passed
                        } else {
                            // Execute movements after 300ms delay
                            NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                            NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);
                            OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransfer);
                            OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                            if(transferTimer.milliseconds() > 200) {
                                transferState = 2;
                                transferTimer.reset();
                            }
                        }
                    } else {
                        // Execute movements immediately if not extended
                        NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                        OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransfer);
                        OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                        if(transferTimer.milliseconds() > 200) {
                            transferState = 2;
                            transferTimer.reset();
                        }
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
                    if(transferTimer.milliseconds() > 400) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if(transferTimer.milliseconds() > 100) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    transferInProgress = false;
                    break;
            }
        }



        if(gamepad1.dpad_up)
        {
            NintakeArm.setPosition(positions_motor.NIntakeArmSpecimenPickUp);
            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
        }

        if(gamepad1.dpad_down)
        {
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
        }


        if(gamepad2.left_stick_y > 0.5) {
            NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);
        }
        if(gamepad2.left_stick_y < -0.5) {
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedFull);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        if(gamepad2.right_stick_y > 0.5) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }
        if(gamepad2.right_stick_y < -0.5) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        }

        // Outtake Claw Controls
        if(gamepad2.left_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
        }
        if(gamepad2.right_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
        }

        // Intake Wrist Controls

        if(gamepad2.back) {
            cancelGroundTimer();
//            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
            //VIPER UP
            OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
            viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

// Add these state variables at the top of the class with other state variables


// Replace the existing start button handler with this state machine
        if(gamepad2.start && !lastStart && !viperDownInProgress) {
            viperDownState = 0;
            viperDownTimer.reset();
            viperDownInProgress = true;
        }
        lastStart = gamepad2.start;

        if(viperDownInProgress) {

            switch(viperDownState) {
                case 0:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    if(viperDownTimer.milliseconds() > 750) {
                        viperDownState = 1;
                        viperDownTimer.reset();
                    }
                    break;

                case 1:
                    viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    groundPowerTimer.reset();  // Reset the ground timer
                    isGroundTimerActive = true;  // Start tracking ground timer
                    viperDownInProgress = false;
                    break;
            }
        }

        if(gamepad1.touchpad)
        {
            viperMotor.setPower(0);
            cancelGroundTimer();
        }

        if(gamepad1.y)
        {
            viperMotor.setPower(-0.3);
            cancelGroundTimer();

        }





        if(gamepad2.dpad_up)
        {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        if (gamepad1.left_bumper && !lastLeftBumper1) {
            // Start manual reset
            isManualResetActive = true;
            lastViperPosition = viperMotor.getCurrentPosition();
            stallTimer.reset();
            viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            viperMotor.setPower(-0.5);
        }
        lastLeftBumper1 = gamepad1.left_bumper;

        checkManualReset();
        updateViperMotorState();
        telemetry.addData("viper power ", viperMotor.getPower());
        telemetry.addData("viper target ", viperMotor.getTargetPosition());
        telemetry.addData("viper pos ", viperMotor.getCurrentPosition());
        telemetry.addData("Ground Timer Active", isGroundTimerActive);
        if (isGroundTimerActive) {
            telemetry.addData("Time until power off", (GROUND_POWER_TIMEOUT - groundPowerTimer.milliseconds()) / 1000.0);
        }
        // Telemetry
        telemetry.update();
    }
}