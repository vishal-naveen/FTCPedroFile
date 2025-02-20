// TelemetryManager.java
package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import TeleOp.Organized.Subsystems.*;

public class TelemetryManager {
    private final Telemetry telemetry;
    private final DriveSubsystem drive;
    private final ViperSubsystem viper;
    private final AnalogInput ultra;

    // Define the timeout constant here since it's private in ViperSubsystem
    private static final double GROUND_POWER_TIMEOUT = 5000;

    public TelemetryManager(Telemetry telemetry, DriveSubsystem drive,
                            ViperSubsystem viper, AnalogInput ultra) {
        this.telemetry = telemetry;
        this.drive = drive;
        this.viper = viper;
        this.ultra = ultra;
    }

    public void initialize(CommandOpMode opMode) {
        opMode.schedule(new RunCommand(this::update));
    }

    public void update() {
        double dist = 100 * (ultra.getVoltage() / 3.3);
        double distInches = dist / 2.54;

        Motor motor = viper.getMotor();
//        telemetry.addData("Auto State", /* Add autoState from AutoSequenceManager */);
//        telemetry.addData("Current Cycle", /* Add currentCycle from AutoSequenceManager */);
        telemetry.addData("Follower Busy", drive.getFollower().isBusy());
        telemetry.addData("viper power", motor.get());

        // Use the custom getTargetPosition method from the modified ViperSubsystem class
        telemetry.addData("viper target", viper.getTargetPosition());

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
}