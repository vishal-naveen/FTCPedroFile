//package TeleOp.Subsystem;
//
//import com.arcrobotics.ftclib.command.CommandOpMode;
//import com.arcrobotics.ftclib.command.InstantCommand;
//import com.arcrobotics.ftclib.command.RunCommand;
//import com.arcrobotics.ftclib.command.button.GamepadButton;
//import com.arcrobotics.ftclib.gamepad.GamepadEx;
//import com.arcrobotics.ftclib.gamepad.GamepadKeys;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
//import TeleOp.Subsystem.Commands.*;
//import Positions.positions_motor;
//
//@TeleOp(name="FieldcentricTELE")
//public class FieldcentricTELE extends CommandOpMode {
//    // Subsystems
//    private DriveSubsystem drive;
//    private IntakeSubsystem intake;
//    private OuttakeSubsystemTeleOp outtake;
//    private ViperSubsystem viper;
//
//    // Controllers
//    private GamepadEx driverController, operatorController;
//
//    // Gamepad Buttons
//    private GamepadButton intakeClawOpen, intakeClawClose;
//    private GamepadButton intakeWristToggle, outtakeWristToggle;
//    private GamepadButton pickupPosition;
//    private GamepadButton viperHighPosition, viperGroundPosition;
//    private GamepadButton manualViperReset, resetHeading;
//
//    @Override
//    public void initialize() {
//        // Initialize subsystems
//        drive = new DriveSubsystem(hardwareMap);
//        intake = new IntakeSubsystem(hardwareMap);
//        outtake = new OuttakeSubsystemTeleOp(hardwareMap);
//        viper = new ViperSubsystem(hardwareMap);
//
//        // Initialize controllers
//        driverController = new GamepadEx(gamepad1);
//        operatorController = new GamepadEx(gamepad2);
//
//        // Configure buttons
//        setupGamepadButtons();
//
//        // Configure button bindings
//        configureButtonBindings();
//
//        // Set default commands
//        setDefaultCommands();
//
//        // Register subsystems
//        register(drive, intake, outtake, viper);
//    }
//
//    private void setupGamepadButtons() {
//        // Driver controls
//        resetHeading = new GamepadButton(driverController, GamepadKeys.Button.A);
//        manualViperReset = new GamepadButton(driverController, GamepadKeys.Button.LEFT_BUMPER);
//
//        // Operator controls
//        intakeClawOpen = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_LEFT);
//        intakeClawClose = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_RIGHT);
//        intakeWristToggle = new GamepadButton(operatorController, GamepadKeys.Button.LEFT_BUMPER);
//        outtakeWristToggle = new GamepadButton(operatorController, GamepadKeys.Button.RIGHT_BUMPER);
//        pickupPosition = new GamepadButton(operatorController, GamepadKeys.Button.A);
//        viperHighPosition = new GamepadButton(operatorController, GamepadKeys.Button.BACK);
//        viperGroundPosition = new GamepadButton(operatorController, GamepadKeys.Button.START);
//    }
//
//    private void configureButtonBindings() {
//        // Intake controls
//        intakeClawOpen.whenPressed(new InstantCommand(() ->
//                intake.setClawPosition(positions_motor.NIntakeClawOpen)));
//
//        intakeClawClose.whenPressed(new InstantCommand(() ->
//                intake.setClawPosition(positions_motor.NIntakeClawClose)));
//
//        // Outtake controls
//        outtakeWristToggle.toggleWhenPressed(
//                new InstantCommand(() -> outtake.setToState(OuttakeSubsystemTeleOp.OuttakeState.SCORE)),
//                new InstantCommand(() -> outtake.pickUpPOS())
//        );
//
//        // Position commands
//        pickupPosition.whenPressed(new PickupCommand(intake, outtake));
//        resetHeading.whenPressed(new InstantCommand(drive::resetHeading));
//        viperHighPosition.whenPressed(new ViperHighPositionCommand(viper, outtake));
//        viperGroundPosition.whenPressed(new ViperGroundPositionCommand(viper, outtake));
//        manualViperReset.whenPressed(new ManualViperResetCommand(viper));
//    }
//
//    private void setDefaultCommands() {
//        // Set drive default command
//        drive.setDefaultCommand(
//                new RunCommand(() -> {
//                    drive.setTeleOpMovement(
//                            -driverController.getLeftY(),
//                            -driverController.getLeftX(),
//                            -driverController.getRightX()
//                    );
//                }, drive)
//        );
//
//        // Add telemetry update command
//        schedule(new RunCommand(() -> {
//            telemetry.addData("Auto Active", drive.isAutoActive());
//            telemetry.addData("Viper Position", viper.getCurrentPosition());
//            telemetry.addData("Viper Target", viper.getTargetPosition());
//            telemetry.update();
//        }));
//    }
//
//    @Override
//    public void runOpMode() {
//        initialize();
//
//        waitForStart();
//
//        while (!isStopRequested() && opModeIsActive()) {
//            // Set motor zero power behavior
//            ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//            // Handle touchpad input for transfer command
//            boolean currentTouchpad = gamepad2.touchpad;
//            if (currentTouchpad && !lastTouchpad) {
//                schedule(new TransferCommand(intake, outtake, viper));
//            }
//            lastTouchpad = currentTouchpad;
//
//            run();
//        }
//
//        reset();
//    }
//
//    private boolean lastTouchpad = false;
//}