package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="doubleArmTester")
public class doubleArmTester extends OpMode {

    private Servo leftArmServo = null;
    private Servo rightArmServo = null;

    // Starting positions for both servos (set to mid-range for testing)
    private double leftArmPos = 0.5;
    private double rightArmPos = 0.5;

    // The increment value for each adjustment (adjust as needed)
    private double INCREMENT = 0.05;

    // For edge detection on the touchpad
    private boolean previousTouchpad = false;

    @Override
    public void init() {
        // Map your servos using the names set in the robot configuration
        leftArmServo = hardwareMap.get(Servo.class, "OuttakeArmLeft");
        rightArmServo = hardwareMap.get(Servo.class, "OuttakeArmRight");

        // Initialize both servos to their starting positions
        leftArmServo.setPosition(leftArmPos);
        rightArmServo.setPosition(rightArmPos);
    }

    @Override
    public void loop() {
        // Change the INCREMENT value when the touchpad is newly pressed
        if (gamepad2.touchpad && !previousTouchpad) {
            if (INCREMENT == 0.05) {
                INCREMENT = 0.1;
            } else if (INCREMENT == 0.1) {
                INCREMENT = 0.0025;
            } else if (INCREMENT == 0.0025) {
                INCREMENT = 0.05;
            } else {
                INCREMENT = 0.05;
            }
        }
        previousTouchpad = gamepad2.touchpad;

        // ----- Simultaneous Opposite Control -----
        // Left bumper: Increase leftArmServo, decrease rightArmServo
        if (gamepad2.left_bumper) {
            leftArmPos += INCREMENT;
            rightArmPos -= INCREMENT;
        }
        // Right bumper: Decrease leftArmServo, increase rightArmServo
        if (gamepad2.right_bumper) {
            leftArmPos -= INCREMENT;
            rightArmPos += INCREMENT;
        }

        // Clamp the positions so they remain within the valid range [0, 1]
        leftArmPos = Math.max(0, Math.min(1, leftArmPos));
        rightArmPos = Math.max(0, Math.min(1, rightArmPos));

        // Update servo positions
        leftArmServo.setPosition(leftArmPos);
        rightArmServo.setPosition(rightArmPos);

        // Telemetry for debugging and monitoring positions and the current increment
        telemetry.addData("Left Arm Servo Position:", leftArmPos);
        telemetry.addData("Right Arm Servo Position:", rightArmPos);
        telemetry.addData("Current Increment:", INCREMENT);
        telemetry.update();
    }
}
