package TeleOp.Subsystem;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.RobotPose;
import Positions.positions_motor;
import Subsystem.OuttakeSubsystem;
import TeleOp.Subsystem.Commands.ManualViperResetCommand;
import TeleOp.Subsystem.Commands.PickupCommand;
import TeleOp.Subsystem.Commands.TransferCommand;
import TeleOp.Subsystem.Commands.ViperGroundPositionCommand;
import TeleOp.Subsystem.DriveSubsystem;
import TeleOp.Subsystem.ViperSubsystem;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name="FieldcentricTELE")
public class FieldcentricTELE extends CommandOpMode {
    // Subsystems
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private ViperSubsystem viper;

    // Controllers
    private GamepadEx driverController, operatorController;

    // Gamepad Buttons
    private GamepadButton intakeClawOpen, intakeClawClose;
    private GamepadButton intakeWristToggle, outtakeWristToggle;
    private GamepadButton pickupPosition;
    private boolean lastTouchpad = false;
    private GamepadButton viperHighPosition, viperGroundPosition;
    private GamepadButton manualViperReset;

    @Override
    public void initialize() {
        // Initialize subsystems
        drive = new DriveSubsystem(hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);
        viper = new ViperSubsystem(hardwareMap);

        // Initialize controllers
        driverController = new GamepadEx(gamepad1);
        operatorController = new GamepadEx(gamepad2);

        // Configure buttons
        setupGamepadButtons();

        // Configure button bindings
        configureButtonBindings();

        // Set default commands
        setDefaultCommands();

        // Register subsystems
        register(drive, intake, outtake, viper);
    }

    private void setupGamepadButtons() {
        intakeClawOpen = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_LEFT);
        intakeClawClose = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_RIGHT);
        intakeWristToggle = new GamepadButton(operatorController, GamepadKeys.Button.LEFT_BUMPER);
        outtakeWristToggle = new GamepadButton(operatorController, GamepadKeys.Button.RIGHT_BUMPER);
        pickupPosition = new GamepadButton(operatorController, GamepadKeys.Button.A);
        // Note: Since touchpad is accessed directly through gamepad2.touchpad, we'll handle it in periodic
        // This button binding will be managed differently
        viperHighPosition = new GamepadButton(operatorController, GamepadKeys.Button.BACK);
        viperGroundPosition = new GamepadButton(operatorController, GamepadKeys.Button.START);
        manualViperReset = new GamepadButton(driverController, GamepadKeys.Button.LEFT_BUMPER);
    }

    private void configureButtonBindings() {
        intakeClawOpen.whenPressed(new InstantCommand(() ->
                intake.setClawPosition(positions_motor.NIntakeClawOpen)));

        intakeClawClose.whenPressed(new InstantCommand(() ->
                intake.setClawPosition(positions_motor.NIntakeClawClose)));

        intakeWristToggle.toggleWhenPressed(
                new InstantCommand(() -> intake.setWristPivotPosition(positions_motor.NIntakeWristPivotHorizontal)),
                new InstantCommand(() -> intake.setWristPivotPosition(positions_motor.NIntakeWristPivotVertical))
        );

        outtakeWristToggle.toggleWhenPressed(
                new InstantCommand(() -> outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar)),
                new InstantCommand(() -> outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotVertical))
        );

        pickupPosition.whenPressed(new PickupCommand(intake, outtake));
        // Handle touchpad in periodic
        viperHighPosition.whenPressed(new TeleOp.commands.ViperHighPositionCommand(viper, outtake));
        viperGroundPosition.whenPressed(new ViperGroundPositionCommand(viper, outtake));
        manualViperReset.whenPressed(new ManualViperResetCommand(viper));
    }

    private void setDefaultCommands() {
        drive.setDefaultCommand(
                new RunCommand(() -> {
                    if (!drive.isAutoActive()) {
                        drive.setTeleOpMovement(
                                -driverController.getLeftY(),
                                -driverController.getLeftX(),
                                -driverController.getRightX()
                        );
                    }
                }, drive)
        );

        // Add telemetry update command
        schedule(new RunCommand(() -> {
            telemetry.addData("Auto Active", drive.isAutoActive());
            telemetry.addData("Viper Position", viper.getCurrentPosition());
            telemetry.addData("Viper Target", viper.getTargetPosition());
            telemetry.update();
        }));
    }

    @Override
    public void run() {
        super.run();

        // Handle touchpad input
        boolean currentTouchpad = gamepad2.touchpad;
        if (currentTouchpad && !lastTouchpad) {
            schedule(new TransferCommand(intake, outtake, viper));
        }
        lastTouchpad = currentTouchpad;

        // Set motor zero power behavior
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "FL")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "FR")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "BL")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "BR")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}