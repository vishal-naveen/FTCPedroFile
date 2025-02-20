// ViperSubsystem.java
package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;

public class ViperSubsystem {
    public final Motor motor;
    public boolean isAtTarget = false;
    public int downState = 0;
    public ElapsedTime downTimer = new ElapsedTime();
    public boolean downInProgress = false;
    public boolean lastStart = false;
    public ElapsedTime groundPowerTimer = new ElapsedTime();
    public boolean isGroundTimerActive = false;
    public int lastPosition = 0;
    public ElapsedTime stallTimer = new ElapsedTime();
    public boolean isStallCheckActive = false;
    public boolean isManualResetActive = false;
    public boolean lastLeftBumper = false;

    // Added to track current mode and target position
    public Motor.RunMode currentRunMode;
    public int targetPosition = 0;

    public static final double VIPER_HOLDING_POWER = 0.1;
    public static final double GROUND_POWER_TIMEOUT = 5000;
    public static final int POSITION_TOLERANCE = 20;
    public static final int STALL_POSITION_THRESHOLD = 5;
    public static final double STALL_TIME_THRESHOLD = 250;

    public ViperSubsystem(Motor motor) {
        this.motor = motor;
        this.currentRunMode = Motor.RunMode.RawPower; // Default mode
    }

    public void initialize() {
        setRunMode(Motor.RunMode.RawPower);
        motor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        motor.resetEncoder();
    }

    // Custom method to set run mode and track it
    public void setRunMode(Motor.RunMode mode) {
        currentRunMode = mode;
        motor.setRunMode(mode);
    }

    // Custom method to set target position and track it
    public void setTargetPosition(int position) {
        targetPosition = position;
        motor.setTargetPosition(position);
    }

    // Custom method to get current run mode
    public Motor.RunMode getRunMode() {
        return currentRunMode;
    }

    // Custom method to get target position
    public int getTargetPosition() {
        return targetPosition;
    }

    public void handleControls(Gamepad gamepad1, Gamepad gamepad2) {
        updateMotorState();

        if (gamepad2.start && !lastStart && !downInProgress) {
            downState = 0;
            downTimer.reset();
            downInProgress = true;
        }
        lastStart = gamepad2.start;

        if (downInProgress) {
            switch (downState) {
                case 0:  // Added case 0 to match original intent
                    downState = 1;
                    downTimer.reset();
                    break;
                case 1:
                    setRunMode(Motor.RunMode.PositionControl);
                    setTargetPosition(positions_motor.VIPER_GROUND);
                    motor.set(1.0);
                    groundPowerTimer.reset();
                    isGroundTimerActive = true;
                    downInProgress = false;
                    break;
            }
        }

        if (gamepad1.touchpad) {
            motor.set(0);
            cancelGroundTimer();
        }
        if (gamepad1.y) {
            motor.set(-0.3);
            cancelGroundTimer();
        }

        if (gamepad1.left_bumper && !lastLeftBumper) {
            isManualResetActive = true;
            lastPosition = motor.getCurrentPosition();
            stallTimer.reset();
            setRunMode(Motor.RunMode.RawPower);
            motor.set(-0.5);
        }
        lastLeftBumper = gamepad1.left_bumper;

        checkManualReset();
    }

    public void updateMotorState() {
        // Check if running in position control mode
        if (getRunMode() == Motor.RunMode.PositionControl) {
            int currentPos = motor.getCurrentPosition();
            int targetPos = getTargetPosition();

            if (targetPos == positions_motor.VIPER_GROUND) {
                if (!isStallCheckActive) {
                    isStallCheckActive = true;
                    lastPosition = currentPos;
                    stallTimer.reset();
                } else if (stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
                    if (Math.abs(currentPos - lastPosition) < STALL_POSITION_THRESHOLD) {
                        motor.stopMotor();
                        motor.resetEncoder();
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
            motor.set(isAtTarget ? VIPER_HOLDING_POWER : 1.0);
        }
    }

    public void checkManualReset() {
        if (isManualResetActive && stallTimer.milliseconds() > STALL_TIME_THRESHOLD) {
            int currentPos = motor.getCurrentPosition();
            if (Math.abs(currentPos - lastPosition) < STALL_POSITION_THRESHOLD) {
                motor.stopMotor();
                motor.resetEncoder();
                setRunMode(Motor.RunMode.RawPower);
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

    public Motor getMotor() { return motor; }
    public boolean isGroundTimerActive() { return isGroundTimerActive; }
    public double getGroundTimerSeconds() { return groundPowerTimer.milliseconds() / 1000.0; }
}