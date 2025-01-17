package IntakeSubsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import Positions.Constants;

public class BucketSideAutoSubsystem extends SubsystemBase {
    // Hardware components
    private final Servo NintakeArm, NintakeWrist, NintakeWristPivot, NintakeClaw;
    private final Servo OuttakeArm, OuttakeWrist, OuttakeWristPivot, OuttakeClaw;
    private final DcMotorEx viperMotor;
    private final Telemetry telemetry;

    // Timer for sequences
    private final ElapsedTime sequenceTimer = new ElapsedTime();
    private boolean sequenceInProgress = false;
    private int sequenceState = 0;

    // Hover positions for outtake wrist
    public enum HoverOrientation {
        HORIZONTAL,
        VERTICAL,
        SLANT_FORWARD,
        SLANT_BACKWARD
    }

    // Outtake States
    public enum OuttakeState {
        HOVER_HORIZONTAL(Constants.OuttakeArmPickUpSpecimen, 0.3, Constants.OuttakeWristPivotHorizontal, 0),
        HOVER_VERTICAL(Constants.OuttakeArmPickUpSpecimen, 0.3, Constants.OuttakeWristPivotVertical, 0),
        HOVER_SLANT_FORWARD(Constants.OuttakeArmPickUpSpecimen, 0.3, 0.4, 0),
        HOVER_SLANT_BACKWARD(Constants.OuttakeArmPickUpSpecimen, 0.3, 0.6, 0),
        TRANSFER(Constants.OuttakeArmTransfer, Constants.OuttakeWristTransfer, Constants.OuttakeWristPivotHorizontal, 0),
        HIGH_BUCKET(Constants.OuttakeArmBucket, Constants.OuttakeWristBucket, Constants.OuttakeWristPivotHighBar, Constants.VIPER_HIGHBASKET);

        private final double armPos, wristPos, wristPivotPos;
        private final int viperPos;

        OuttakeState(double armPos, double wristPos, double wristPivotPos, int viperPos) {
            this.armPos = armPos;
            this.wristPos = wristPos;
            this.wristPivotPos = wristPivotPos;
            this.viperPos = viperPos;
        }
    }

    public BucketSideAutoSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        // Initialize servos
        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        // Initialize viper motor
        viperMotor = hardwareMap.get(DcMotorEx.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    // Intake Arm Extension Methods
    public void extendIntakeFullWithHover(HoverOrientation hoverOrientation) {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedFull);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
        switch (hoverOrientation) {
            case HORIZONTAL:
                setOuttakeState(OuttakeState.HOVER_HORIZONTAL);
                break;
            case VERTICAL:
                setOuttakeState(OuttakeState.HOVER_VERTICAL);
                break;
            case SLANT_FORWARD:
                setOuttakeState(OuttakeState.HOVER_SLANT_FORWARD);
                break;
            case SLANT_BACKWARD:
                setOuttakeState(OuttakeState.HOVER_SLANT_BACKWARD);
                break;
        }
    }

    // Intake transfer methods
    public void retractIntakeForTransfer() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
        // Set outtake to transfer position
        setOuttakeState(OuttakeState.TRANSFER);
    }

    public void retractIntakeOnly() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
        // Outtake remains in its current position
    }

    public void armBack() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        // Outtake remains in its current position
    }

    public void startIntakeOnly() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
        // Outtake remains in its current position
    }

    // Basic Intake Claw Control
    public void openIntakeClaw() {
        NintakeClaw.setPosition(Constants.NIntakeClawOpen);
    }

    public void closeIntakeClaw() {
        NintakeClaw.setPosition(Constants.NIntakeClawClose);
    }

    // Basic Outtake Claw Control
    public void openOuttakeClaw() {
        OuttakeClaw.setPosition(Constants.OuttakeClawOpen);
    }

    public void closeOuttakeClaw() {
        OuttakeClaw.setPosition(Constants.OuttakeClawClose);
    }

    // Automated Intake Sequences
    public void startIntakeSequence(String endBehavior) {
        sequenceState = 0;
        sequenceTimer.reset();
        sequenceInProgress = true;
        updateIntakeSequence(endBehavior);
    }

    // Add these two methods to BucketSideAutoSubsystem class

    public void intakeWristPickup() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedFull);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
    }

    public void intakeArmBack() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
    }

    private void updateIntakeSequence(String endBehavior) {
        if (!sequenceInProgress) return;

        switch (sequenceState) {
            case 0: // Open claw
                openIntakeClaw();
                if (sequenceTimer.milliseconds() > 250) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 1: // Move wrist to pickup
                NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
                if (sequenceTimer.milliseconds() > 500) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 2: // Close claw
                closeIntakeClaw();
                if (sequenceTimer.milliseconds() > 500) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 3: // End behavior
                switch (endBehavior) {
                    case "transfer":
                        retractIntakeForTransfer();
                        break;
                    case "transfer_intake_only":
                        retractIntakeOnly();
                        break;
                    case "pickup_ready":
                        NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
                        break;
                }
                sequenceInProgress = false;
                break;
        }
    }

    // Outtake Control Methods
    private void setOuttakeState(OuttakeState state) {
        OuttakeArm.setPosition(state.armPos);
        OuttakeWrist.setPosition(state.wristPos);
        OuttakeWristPivot.setPosition(state.wristPivotPos);

        // Set viper motor position
        viperMotor.setTargetPosition(state.viperPos);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public void setOuttakeToTransferPosition() {
        setOuttakeState(OuttakeState.TRANSFER);
        OuttakeClaw.setPosition(Constants.OuttakeClawClose);
    }

    public void setOuttakeToHighBucket() {
        setOuttakeState(OuttakeState.HIGH_BUCKET);
    }



    public void outtakePark() {
        viperMotor.setTargetPosition(0);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        OuttakeArm.setPosition(Constants.OuttakeArmBucket);
    }

    public void startOuttakeTransferSequence() {
        sequenceState = 0;
        sequenceTimer.reset();
        sequenceInProgress = true;
        updateTransferSequence();
    }

    public void intakeWristUp() {
        NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
    }

    private void updateTransferSequence() {
        if (!sequenceInProgress) return;

        switch (sequenceState) {
            case 0: // Prepare positions and retract viper
                setOuttakeState(OuttakeState.TRANSFER); // This will also set viper to 0
                if (sequenceTimer.milliseconds() > 500) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 1: // Close claw
                OuttakeClaw.setPosition(Constants.OuttakeClawClose);
                if (sequenceTimer.milliseconds() > 500) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 2: // Open intake claw
                NintakeClaw.setPosition(Constants.NIntakeClawOpen);
                if (sequenceTimer.milliseconds() > 500) {
                    sequenceState++;
                    sequenceTimer.reset();
                }
                break;

            case 3: // Move to high bucket with viper extended
                setOuttakeState(OuttakeState.HIGH_BUCKET); // This will extend viper to high position
                sequenceInProgress = false;
                break;
        }
    }


    @Override
    public void periodic() {
        if (sequenceInProgress) {
            updateIntakeSequence("transfer_both");
            updateTransferSequence();
        }

        // Telemetry
        telemetry.addData("Sequence in Progress", sequenceInProgress);
        telemetry.addData("Sequence State", sequenceState);
        telemetry.addData("Timer", sequenceTimer.milliseconds());
        telemetry.addData("Viper Position", viperMotor.getCurrentPosition());
        telemetry.addData("Viper Target", viperMotor.getTargetPosition());
        telemetry.addData("Intake Arm", NintakeArm.getPosition());
        telemetry.addData("Intake Wrist", NintakeWrist.getPosition());
        telemetry.addData("Intake Pivot", NintakeWristPivot.getPosition());
        telemetry.addData("Intake Claw", NintakeClaw.getPosition());
        telemetry.addData("Outtake Arm", OuttakeArm.getPosition());
        telemetry.addData("Outtake Wrist", OuttakeWrist.getPosition());
        telemetry.addData("Outtake Pivot", OuttakeWristPivot.getPosition());
        telemetry.addData("Outtake Claw", OuttakeClaw.getPosition());
        telemetry.update();
    }
}