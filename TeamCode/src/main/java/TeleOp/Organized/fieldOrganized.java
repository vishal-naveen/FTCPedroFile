// FieldcentricTELE.java
package TeleOp.Organized;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;

import TeleOp.Organized.Subsystems.IntakeSubsystem;
import TeleOp.Organized.Subsystems.DriveSubsystem;
import TeleOp.Organized.Subsystems.OuttakeSubsystem;
import TeleOp.Organized.Subsystems.ViperSubsystem;
import TeleOp.Organized.Subsystems.AutoSequenceManager;
import TeleOp.Organized.Subsystems.TelemetryManager;

@TeleOp(name="fieldOrganized")
public class fieldOrganized extends CommandOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private ViperSubsystem viper;
    private AutoSequenceManager autoManager;
    private TelemetryManager telemetryManager;
    private AnalogInput ultra;

    @Override
    public void initialize() {
        ultra = hardwareMap.get(AnalogInput.class, "ultra");

        drive = new DriveSubsystem(this);
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);
        viper = new ViperSubsystem(hardwareMap.get(Motor.class, "viper1motor"));
        autoManager = new AutoSequenceManager(drive, intake, outtake, viper, ultra); // Updated to pass ultra
        telemetryManager = new TelemetryManager(telemetry, drive, viper, ultra);

        drive.initialize();
        viper.initialize();
        telemetryManager.initialize(this);
    }

    @Override
    public void run() {
        drive.updateDrive(gamepad1);
        autoManager.handleAutoSequence(gamepad1, this);

        intake.handleControls(gamepad1, gamepad2);
        outtake.handleControls(gamepad2);
        viper.handleControls(gamepad1, gamepad2);

        telemetryManager.update();
        CommandScheduler.getInstance().run();
    }
}