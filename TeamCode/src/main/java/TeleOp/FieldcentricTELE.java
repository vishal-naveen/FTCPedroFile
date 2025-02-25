package TeleOp;

import com.arcrobotics.ftclib.command.CommandScheduler;
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

import IntakeSubsystem.BucketSideAutoSubsystem;
import Positions.Commands;
import Positions.RobotPose;
import Positions.positions_motor;
import Subsystem.OuttakeSubsystem;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name="FieldcentricTELE")
public class FieldcentricTELE extends OpMode {
    private Follower follower;
    private AutoPaths autoPaths;
    private double power = 1;

    private OuttakeSubsystem outtakeSubsystem;
    private BucketSideAutoSubsystem bucketSubsystem;

    private DcMotor viperMotor = null;

    private Servo IntakeArmLeft = null;  // Dual intake arms
    private Servo IntakeArmRight = null;
    private Servo NintakeWrist = null;
    private Servo NintakeWristPivot = null;
    private Servo NintakeClaw = null;
    private Servo OuttakeArmLeft = null;
    private Servo OuttakeArmRight = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;
    private Servo OuttakeClaw = null;

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
    private boolean lastB = false;

    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;

    private boolean isViperAtTarget = false;
    private static final int POSITION_TOLERANCE = 20;

    private int viperDownState = 0;
    private ElapsedTime viperDownTimer = new ElapsedTime();
    private boolean viperDownInProgress = false;
    private boolean lastStart = false;

    private static final double VIPER_HOLDING_POWER = 0.1;
    private static final double VIPER_MAX_POWER = 1.0;

    private ElapsedTime groundPowerTimer = new ElapsedTime();
    private boolean isGroundTimerActive = false;
    private static final double GROUND_POWER_TIMEOUT = 5000;

    private boolean isManualResetActive = false;
    private boolean lastLeftBumper1 = false;

    private int intakeCloseState = 0;
    private ElapsedTime intakeCloseTimer = new ElapsedTime();
    private boolean intakeCloseInProgress = false;

    private boolean isArmExtended = false;
    private boolean isPivotHorizontal = false;

    private boolean bPressed = false;

    private void updatePivotState() {
        double pivotPosition = NintakeWristPivot.getPosition();
        isPivotHorizontal = Math.abs(pivotPosition - positions_motor.NIntakeWristPivotHorizontal) < 0.05;
    }

    private void updateArmExtensionState() {
        double leftArmPosition = IntakeArmLeft.getPosition();
        double rightArmPosition = IntakeArmRight.getPosition();
        isArmExtended = (Math.abs(leftArmPosition - positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL) < 0.05 &&
                Math.abs(rightArmPosition - positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL) < 0.05);
    }

    private void cancelGroundTimer() {
        if (isGroundTimerActive) {
            isGroundTimerActive = false;
            groundPowerTimer.reset();
            viperMotor.setPower(0); // Ensure motor stops when timer is canceled
        }
    }

    private void updateViperMotorState() {
        if (viperMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = viperMotor.getCurrentPosition();
            int targetPos = viperMotor.getTargetPosition();
            double powerToApply = (Math.abs(currentPos - targetPos) > POSITION_TOLERANCE) ? VIPER_MAX_POWER : VIPER_HOLDING_POWER;

            if (Math.abs(currentPos - targetPos) <= POSITION_TOLERANCE) {
                isViperAtTarget = true;
                viperMotor.setPower(VIPER_HOLDING_POWER); // Hold position
            } else {
                isViperAtTarget = false;
                viperMotor.setPower(VIPER_MAX_POWER); // Move to target
            }
        }
        // If not in RUN_TO_POSITION mode, power is controlled manually elsewhere
    }

    private void checkManualReset() {
        if (isManualResetActive) {
            viperMotor.setPower(0);
            viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            isManualResetActive = false;
            isViperAtTarget = false; // Reset target state
        }
    }

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(RobotPose.stopPose);
        follower.setMaxPower(0.9);
        try {
            autoPaths = new AutoPaths(hardwareMap, follower, gamepad1);
        } catch (Exception e) {
            // No telemetry for AutoPaths error; silently handle
        }

        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, null, this.follower); // No telemetry
        this.bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, null); // No telemetry

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // Reset encoder first
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // Start in manual mode
        viperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // Brake when power is 0

        IntakeArmLeft = hardwareMap.get(Servo.class, "IntakeArmLeft");
        IntakeArmRight = hardwareMap.get(Servo.class, "IntakeArmRight");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");
        OuttakeArmLeft = hardwareMap.get(Servo.class, "OuttakeArmLeft");
        OuttakeArmRight = hardwareMap.get(Servo.class, "OuttakeArmRight");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        CommandScheduler.getInstance().run();
        follower.update();
        autoPaths.update();

        boolean hasJoystickInput = Math.abs(gamepad1.left_stick_x) > 0.1 ||
                Math.abs(gamepad1.left_stick_y) > 0.1 ||
                Math.abs(gamepad1.right_stick_x) > 0.1;

        if (!autoPaths.isActive()) {
            follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, false);

            if (gamepad1.b && !bPressed) {
                bPressed = true;
                autoPaths.startAuto();
                follower.setCurrentPoseWithOffset(new Pose(4, 28.75, Math.toRadians(0)));
            }
        } else if (hasJoystickInput) {
            autoPaths.cancelSequence();
        }

        if (!gamepad1.b) {
            bPressed = false;
        }

        if (gamepad1.a) {
            follower.setCurrentPoseWithOffset(new Pose(4, 28.75, Math.toRadians(0)));
        }

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
                    positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR :
                    positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
        }
        lastRightBumper = gamepad2.right_bumper;

        if (gamepad2.dpad_left && !lastDpadLeft) {
            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
        }
        if (gamepad2.dpad_right && !lastDpadRight) {
            NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
        }
        lastDpadLeft = gamepad2.dpad_left;
        lastDpadRight = gamepad2.dpad_right;

        if (gamepad2.b && !lastB && !pickupInProgress) {
            pickupState = 0;
            pickupTimer.reset();
            pickupInProgress = true;
        }
        lastB = gamepad2.b;

        if (pickupInProgress) {
            switch (pickupState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    if (pickupTimer.milliseconds() > 250) {
                        pickupState = 1;
                        pickupTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    if (pickupTimer.milliseconds() > 10) {
                        pickupInProgress = false;
                    }
                    break;
            }
        }

        if (gamepad2.a) {
            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
            OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }

        if (gamepad2.x) {
            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_FLICK);
            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_FLICK);
            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        }

        if (gamepad2.dpad_down) {
            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
            OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
        }

        if (gamepad2.y) {
            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER_WAIT);
            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER_WAIT);
            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        }

        if (gamepad2.touchpad && !lastDpadUp && !transferInProgress) {
            transferState = 0;
            transferTimer.reset();
            transferInProgress = true;
        }
        lastDpadUp = gamepad2.touchpad;

        if (transferInProgress) {
            cancelGroundTimer();
            switch (transferState) {
                case 0:
                    updateArmExtensionState();
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
                    IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
                    IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                    NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                    if (transferTimer.milliseconds() > 1000) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    if (isArmExtended) {
                        if (transferTimer.milliseconds() <= 1000) {
                        } else {
                            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                            if (transferTimer.milliseconds() > 1000) {
                                transferState = 2;
                                transferTimer.reset();
                            }
                        }
                    } else {
                        NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                        if (transferTimer.milliseconds() > 1000) {
                            transferState = 2;
                            transferTimer.reset();
                        }
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
                    if (transferTimer.milliseconds() > 1000) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if (transferTimer.milliseconds() > 1000) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(VIPER_MAX_POWER); // Ensure power is set
                    if (transferTimer.milliseconds() > 1000) {
                        transferState = 5;
                        transferTimer.reset();
                    }
                    break;
                case 5:
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    transferInProgress = false;
                    break;
            }
        }

        if (gamepad1.dpad_up) {
            IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
            IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
        }

        if (gamepad1.dpad_down) {
            IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
            IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
        }

        if (gamepad2.left_stick_y > 0.5) {
            IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
            IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
        }
        if (gamepad2.left_stick_y < -0.5) {
            IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
            IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        if (gamepad2.right_stick_y > 0.5) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }
        if (gamepad2.right_stick_y < -0.5) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        }

        if (gamepad2.left_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
        }
        if (gamepad2.right_trigger > 0.25) {
            OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
            Commands.closeClaw(outtakeSubsystem).raceWith(Commands.sleep(100));
        }

        if (gamepad2.back) {
            cancelGroundTimer();
            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
            OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
            viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(VIPER_MAX_POWER); // Ensure power is set
        }

        if (gamepad2.start && !lastStart && !viperDownInProgress) {
            viperDownState = 0;
            viperDownTimer.reset();
            viperDownInProgress = true;
        }
        lastStart = gamepad2.start;

        if (viperDownInProgress) {
            switch (viperDownState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    if (viperDownTimer.milliseconds() > 750) {
                        viperDownState = 1;
                        viperDownTimer.reset();
                    }
                    break;

                case 1:
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_GROUND);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(VIPER_MAX_POWER); // Ensure power is set
                    groundPowerTimer.reset();
                    isGroundTimerActive = true;
                    viperDownInProgress = false;
                    break;
            }
        }

        if (gamepad1.touchpad) {
            viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // Switch to manual mode
            viperMotor.setPower(0);
            cancelGroundTimer();
        }

        if (gamepad1.y) {
            viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // Switch to manual mode
            viperMotor.setPower(-0.3);
            cancelGroundTimer();
        }

        if (gamepad2.dpad_up) {
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        if (gamepad1.left_bumper && !lastLeftBumper1) {
            isManualResetActive = true;
        }
        lastLeftBumper1 = gamepad1.left_bumper;

        // Check if ground timer expires
        if (isGroundTimerActive && groundPowerTimer.milliseconds() >= GROUND_POWER_TIMEOUT) {
            viperMotor.setPower(0);
            isGroundTimerActive = false;
        }

        updateViperMotorState();
        checkManualReset();

        // Viper-specific telemetry
        telemetry.addData("Viper Power", viperMotor.getPower());
        telemetry.addData("Viper Target", viperMotor.getTargetPosition());
        telemetry.addData("Viper Position", viperMotor.getCurrentPosition());
        telemetry.addData("Viper Mode", viperMotor.getMode().toString());
        telemetry.addData("Is Viper At Target", isViperAtTarget);
        telemetry.update();
    }
}