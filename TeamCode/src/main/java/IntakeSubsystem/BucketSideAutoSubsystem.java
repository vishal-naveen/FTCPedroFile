package IntakeSubsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import Positions.positions_motor;

public class BucketSideAutoSubsystem extends SubsystemBase {
    private final Servo IntakeArmLeft, IntakeArmRight;
    private final Servo NintakeWrist, NintakeWristPivot, NintakeClaw;
    private final Servo OuttakeArmLeft, OuttakeArmRight;
    private final Servo OuttakeWrist, OuttakeWristPivot, OuttakeClaw;
    private final DcMotorEx viperMotor;
    private final Telemetry telemetry;

    private final ElapsedTime sequenceTimer = new ElapsedTime();
    private boolean sequenceInProgress = false;
    private int sequenceState = 0;

    private boolean transferInProgress = false;
    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();

    private boolean transferInProgressHigher = false;
    private int transferStateHigher = 0;
    private ElapsedTime transferTimerHigher = new ElapsedTime();
    private boolean isArmExtended = false;
    private boolean isPivotHorizontal = false;

    public BucketSideAutoSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

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

        viperMotor = hardwareMap.get(DcMotorEx.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void startTransfer() {
        transferState = 0;
        transferTimer.reset();
        transferInProgress = true;
        updateArmExtensionState();
        updatePivotState();
    }

    public void startTransferHigher() {
        transferStateHigher = 0;
        transferTimerHigher.reset();
        transferInProgressHigher = true;
        updateArmExtensionState();
        updatePivotState();
    }

    private void updateArmExtensionState() {
        double leftArmPosition = IntakeArmLeft.getPosition();
        double rightArmPosition = IntakeArmRight.getPosition();
        isArmExtended = (Math.abs(leftArmPosition - positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL) < 0.05 &&
                Math.abs(rightArmPosition - positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL) < 0.05);
    }

    private void updatePivotState() {
        double pivotPosition = NintakeWristPivot.getPosition();
        isPivotHorizontal = Math.abs(pivotPosition - positions_motor.NIntakeWristPivotHorizontal) < 0.05;
    }

    public void extendIntake() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
    }

    public void extendIntakeCross() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotVertical);
    }

    public void downWhileScore() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
    }

    public void wristDown() {
        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
    }

    public void wristUp() {
        NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
    }

    public void retractIntakeTransfer() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
        NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
    }

    public void IntakePivotHorizontal() {
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
    }

    public void retractIntakeFull() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
    }

    public void bucketAuto() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
    }

    public void retractSpecimenInit() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
    }

    public void openIntakeClaw() {
        NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
    }

    public void armWall() {
        IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
        IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
    }

    public void closeIntakeClaw() {
        NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
    }

    public void openOuttakeClaw() {
        OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
    }

    public void closeOuttakeClaw() {
        OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
    }

    public void waitPOS() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    public void viperDown() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER_WAIT);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER_WAIT);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
        viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public void setHighBucket() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBASEKT);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBASKET);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
        viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public void setHighBucketViper() {
        viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setPickupPOS() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    public void tempPOS() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
    }

    public void preloadOuttake() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    public void parkOuttake() {
        viperMotor.setTargetPosition(0);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
    }

    @Override
    public void periodic() {
        if (transferInProgress) {
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
                    if (transferTimer.milliseconds() > 500) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    if (isArmExtended) {
                        if (transferTimer.milliseconds() <= 500) {
                            // Wait
                        } else {
                            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                            if (transferTimer.milliseconds() > 500) {
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
                        if (transferTimer.milliseconds() > 500) {
                            transferState = 2;
                            transferTimer.reset();
                        }
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
                    if (transferTimer.milliseconds() > 500) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if (transferTimer.milliseconds() > 500) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBASEKT);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBASKET);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    if (transferTimer.milliseconds() > 250) {
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

        if (transferInProgressHigher) {
            switch (transferStateHigher) {
                case 0:
                    updateArmExtensionState();
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
                    IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
                    IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                    NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                    if (transferTimerHigher.milliseconds() > 500) {
                        transferStateHigher = 1;
                        transferTimerHigher.reset();
                    }
                    break;

                case 1:
                    if (isArmExtended) {
                        if (transferTimerHigher.milliseconds() <= 500) {
                            // Wait
                        } else {
                            OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                            OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                            OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                            if (transferTimerHigher.milliseconds() > 500) {
                                transferStateHigher  = 2;
                                transferTimerHigher.reset();
                            }
                        }
                    } else {
                        NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                        if (transferTimerHigher.milliseconds() > 500) {
                            transferStateHigher = 2;
                            transferTimerHigher.reset();
                        }
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
                    if (transferTimerHigher.milliseconds() > 500) {
                        transferStateHigher = 3;
                        transferTimerHigher.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if (transferTimerHigher.milliseconds() > 500) {
                        transferStateHigher = 4;
                        transferTimerHigher.reset();
                    }
                    break;

                case 4:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBASEKT);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBASKET);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET_HIGHER);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    if (transferTimerHigher.milliseconds() > 250) {
                        transferStateHigher = 5;
                        transferTimerHigher.reset();
                    }
                    break;

                case 5:
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    transferInProgressHigher = false;
                    break;
            }
        }
    }
}