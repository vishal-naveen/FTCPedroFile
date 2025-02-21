// TelemetryManager.java
package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import TeleOp.FieldcentricTELE;
import TeleOp.Organized.fieldOrganized;

public class TelemetryManager {
    private final Telemetry telemetry;
    private final DriveSubsystem drive;
    private final ViperSubsystem viper;
    private final AnalogInput ultra;
    private final CommandOpMode opMode;  // Add reference to OpMode
    private double dist;
    private double distInches;

    private static final double GROUND_POWER_TIMEOUT = 5000;

    public TelemetryManager(Telemetry telemetry, DriveSubsystem drive,
                            ViperSubsystem viper, AnalogInput ultra, CommandOpMode opMode) {
        this.telemetry = telemetry;
        this.drive = drive;
        this.viper = viper;
        this.ultra = ultra;
        this.opMode = opMode;  // Store OpMode reference
    }

    public void initialize() {
        opMode.schedule(new RunCommand(this::update));  // Schedule parameterless update
    }

    public void update() {  // Parameterless method for scheduling
        dist = 100 * (ultra.getVoltage() / 3.3);
        distInches = dist / 2.54;

        DcMotor motor = viper.getMotor();
        // Access autoState and currentCycle via reflection or cast to FieldcentricTELE
        // For simplicity, assuming FieldcentricTELE has public fields or getters
        fieldOrganized teleOp = (fieldOrganized) opMode;
        telemetry.addData("Auto State", teleOp.getAutoState());
        telemetry.addData("Current Cycle", teleOp.getCurrentCycle());
        telemetry.addData("Follower Busy", drive.getFollower().isBusy());
        telemetry.addData("viper power", motor.getPower());
        telemetry.addData("viper target", motor.getTargetPosition());
        telemetry.addData("viper pos", motor.getCurrentPosition());
        telemetry.addData("Ground Timer Active", viper.isGroundTimerActive());
        if (viper.isGroundTimerActive()) {
            telemetry.addData("Time until power off",
                    (GROUND_POWER_TIMEOUT/1000) - viper.getGroundTimerSeconds());
        }
        telemetry.addData("sensor cm", dist);
        telemetry.addData("sensor Inches", distInches);
        telemetry.update();
    }

    public double getDistInches() {
        return distInches;
    }
}