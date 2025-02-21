// ViperSubsystem.java
package TeleOp.Organized.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;

public class ViperSubsystem {
    private final DcMotor motor;
    private boolean isAtTarget = false;
    private int downState = 0;
    private ElapsedTime downTimer = new ElapsedTime();
    private boolean downInProgress = false;
    private boolean lastStart = false;
    private ElapsedTime groundPowerTimer = new ElapsedTime();
    private boolean isGroundTimerActive = false;
    private int lastPosition = 0;
    private ElapsedTime stallTimer = new ElapsedTime();
    private boolean isStallCheckActive = false;
    private boolean isManualResetActive = false;
    private boolean lastLeftBumper = false;

    private static final double VIPER_HOLDING_POWER = 0.1;
    private static final double GROUND_POWER_TIMEOUT = 5000;
    private static final int POSITION_TOLERANCE = 20;
    private static final int STALL_POSITION_THRESHOLD = 5;
    private static final double STALL_TIME_THRESHOLD = 250;

    public ViperSubsystem(DcMotor motor) {
        this.motor = motor;
    }

    public void initialize() {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void handleControls(Gamepad gamepad1, Gamepad gamepad2, OuttakeSubsystem outtake) {
        updateMotorState();

        if (gamepad2.start && !lastStart && !downInProgress) {
            downState = 0;
            downTimer.reset();
            downInProgress = true;
        }
        lastStart = gamepad2.start;

        if (downInProgress) {
            switch (downState) {
                case 0:
                    outtake.getArm().setPosition(positions_motor.OuttakeArmNewTransferWAIT);
                    outtake.getWrist().setPosition(positions_motor.OuttakeWristPickUpSpecimen);
                    outtake.getWristPivot().setPosition(positions_motor.OuttakeWristPivotHighBar);
                    if (downTimer.milliseconds() > 750) {
                        downState = 1;
                        downTimer.reset();
                    }
                    break;
                case 1:
                    motor.setTargetPosition(positions_motor.VIPER_GROUND);
                    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    motor.setPower(1);
                    groundPowerTimer.reset();
                    isGroundTimerActive = true;
                    downInProgress = false;
                    break;
            }
        }

        if (gamepad1.touchpad) {
            motor.setPower(0);
            cancelGroundTimer();
        }
        if (gamepad1.y) {
            motor.setPower(-0.3);
            cancelGroundTimer();
        }

        if (gamepad1.left_bumper && !lastLeftBumper) {
            isManualResetActive = true;
            lastPosition = motor.getCurrentPosition();
            stallTimer.reset();
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(-0.5);
        }
        lastLeftBumper = gamepad1.left_bumper;

        checkManualReset();
    }

    private void updateMotorState() {
        if (motor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = motor.getCurrentPosition();
            int targetPos = motor.getTargetPosition();

            if (targetPos == positions_motor.VIPER_GROUND) {
                if (!isStallCheckActive) {
                    isStallCheckActive = true;
                    lastPosition = currentPos;
                    stallTimer.reset();
                } else if (stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
                    if (Math.abs(currentPos - lastPosition) < STALL_POSITION_THRESHOLD) {
                        motor.setPower(0);
                        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        isStallCheckActive = false;
                        cancelGroundTimer();
                        return;
                    }
                    lastPosition = currentPos;
                    stallTimer.reset();
                }
            } else {
                isStallCheckActive = false;
            }

            isAtTarget = Math.abs(currentPos - targetPos) <= POSITION_TOLERANCE;
            motor.setPower(isAtTarget ? VIPER_HOLDING_POWER : 1.0);
        }
    }

    private void checkManualReset() {
        if (isManualResetActive && stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
            int currentPos = motor.getCurrentPosition();
            if (Math.abs(currentPos - lastPosition) < STALL_POSITION_THRESHOLD) {
                motor.setPower(0);
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                isManualResetActive = false;
            }
            lastPosition = currentPos;
            stallTimer.reset();
        }
    }

    public void cancelGroundTimer() {
        if (isGroundTimerActive) {
            isGroundTimerActive = false;
            groundPowerTimer.reset();
        }
    }

    public DcMotor getMotor() { return motor; }
    public boolean isGroundTimerActive() { return isGroundTimerActive; }
    public double getGroundTimerSeconds() { return groundPowerTimer.milliseconds() / 1000.0; }
}