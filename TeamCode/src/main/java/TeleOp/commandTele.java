//package TeleOp;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.arcrobotics.ftclib.command.CommandOpMode;
//import com.arcrobotics.ftclib.command.InstantCommand;
//import com.arcrobotics.ftclib.command.RunCommand;
//import com.arcrobotics.ftclib.command.SequentialCommandGroup;
//import com.arcrobotics.ftclib.command.button.GamepadButton;
//import com.arcrobotics.ftclib.gamepad.GamepadEx;
//import com.arcrobotics.ftclib.gamepad.GamepadKeys;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.IMU;
//
//import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;
//
//@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="FieldcentricTELE")
//public class FieldcentricTELE extends CommandOpMode {
//    private Follower follower;
//    private DrivetrainSubsystem drivetrain;
//    private ElevatorSubsystem elevator;
//    private IntakeSubsystem intake;
//    private IMU imu;
//
//    private GamepadEx driverController, operatorController;
//    private GamepadButton resetHeading, clawOpen, clawClose, wristPivotToggle,
//            outtakePivotToggle, transfer, viperDown, manualLower,
//            pickupSequence;
//
//    private FtcDashboard dashboard;
//    private AnalogInput ultra;
//
//    @Override
//    public void initialize() {
//        // Initialize hardware
//        drivetrain = new DrivetrainSubsystem(hardwareMap);
//        elevator = new ElevatorSubsystem(hardwareMap);
//        intake = new IntakeSubsystem(hardwareMap, telemetry);
//        ultra = hardwareMap.analogInput.get("ultra");
//
//        // IMU setup
//        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
//                RevHubOrientationOnRobot.LogoFacingDirection.UP,
//                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
//        ));
//        imu = hardwareMap.get(IMU.class, "imu");
//        imu.initialize(parameters);
//
//        // Pedro Pathing setup
//        follower = new Follower(hardwareMap);
//        follower.setStartingPose(new Pose(0, 0, 0));
//
//        // Gamepad setup
//        driverController = new GamepadEx(gamepad1);
//        operatorController = new GamepadEx(gamepad2);
//
//        // Button bindings
//        resetHeading = new GamepadButton(driverController, GamepadKeys.Button.A);
//        manualLower = new GamepadButton(driverController, GamepadKeys.Button.Y);
//        clawOpen = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_LEFT);
//        clawClose = new GamepadButton(operatorController, GamepadKeys.Button.DPAD_RIGHT);
//        wristPivotToggle = new GamepadButton(operatorController, GamepadKeys.Button.LEFT_BUMPER);
//        outtakePivotToggle = new GamepadButton(operatorController, GamepadKeys.Button.RIGHT_BUMPER);
//        transfer = new GamepadButton(operatorController, GamepadKeys.Button.Touch);
//        viperDown = new GamepadButton(operatorController, GamepadKeys.Button.START);
//        pickupSequence = new GamepadButton(operatorController, GamepadKeys.Button.B);
//
//        // Command bindings
//        resetHeading.whenPressed(new InstantCommand(() ->
//                follower.setCurrentPoseWithOffset(new Pose(
//                        follower.getPose().getX(),
//                        follower.getPose().getY(),
//                        Math.toRadians(0)
//                ))
//        ));
//
//        clawOpen.whenPressed(new InstantCommand(() ->
//                intake.intakeToPosition(
//                        intake.getExtensionState(),
//                        intake.getArmState(),
//                        intake.getWristState(),
//                        IntakeSubsystem.ClawState.OPEN_CLAW
//                ), intake));
//
//        clawClose.whenPressed(new InstantCommand(() ->
//                intake.intakeToPosition(
//                        intake.getExtensionState(),
//                        intake.getArmState(),
//                        intake.getWristState(),
//                        IntakeSubsystem.ClawState.CLOSE_CLAW
//                ), intake));
//
//        wristPivotToggle.toggleWhenPressed(
//                new InstantCommand(() -> intake.setWristPivot(IntakeSubsystem.WristPivotState.HORIZONTAL), intake),
//                new InstantCommand(() -> intake.setWristPivot(IntakeSubsystem.WristPivotState.VERTICAL), intake)
//        );
//
//        outtakePivotToggle.toggleWhenPressed(
//                new InstantCommand(() -> elevator.setWristPivot(ElevatorSubsystem.WristPivotState.HIGH_BAR), elevator),
//                new InstantCommand(() -> elevator.setWristPivot(ElevatorSubsystem.WristPivotState.VERTICAL), elevator)
//        );
//
//        transfer.whenPressed(new SequentialCommandGroup(
//                new InstantCommand(() -> intake.prepareTransfer()),
//                new WaitCommand(500),
//                new InstantCommand(() -> {
//                    intake.completeTransfer();
//                    elevator.receiveTransfer();
//                }),
//                new WaitCommand(200),
//                new InstantCommand(() -> elevator.closeClaw()),
//                new WaitCommand(400),
//                new InstantCommand(() -> intake.openClaw()),
//                new WaitCommand(100),
//                new InstantCommand(() -> elevator.toHighBasket())
//        ));
//
//        viperDown.whenPressed(new SequentialCommandGroup(
//                new InstantCommand(() -> elevator.prepareForGround()),
//                new WaitCommand(750),
//                new InstantCommand(() -> elevator.toGround())
//        ));
//
//        pickupSequence.whenPressed(new SequentialCommandGroup(
//                new InstantCommand(() -> elevator.preparePickup()),
//                new WaitCommand(250),
//                new InstantCommand(() -> elevator.completePickup())
//        ));
//
//        // Default drive command
//        drivetrain.setDefaultCommand(new RunCommand(() -> {
//            follower.setTeleOpMovementVectors(
//                    -driverController.getLeftY(),
//                    -driverController.getLeftX(),
//                    -driverController.getRightX(),
//                    false
//            );
//            follower.update();
//        }, drivetrain));
//
//        register(drivetrain, elevator, intake);
//        schedule(new RunCommand(() -> {
//            double dist = 100 * (ultra.getVoltage()/3.3);
//            double distInches = dist/2.54;
//            telemetry.addData("sensor cm", dist);
//            telemetry.addData("sensor Inches ", distInches);
//            telemetry.update();
//        }));
//    }
//
//    @Override
//    public void runOpMode() {
//        initialize();
//        waitForStart();
//
//        follower.startTeleopDrive();
//
//        while (!isStopRequested() && opModeIsActive()) {
//            run();
//        }
//
//        reset();
//    }
//}
//
//class IntakeSubsystem extends SubsystemBase {
//    private final Servo armServo, wristServo, wristPivotServo, clawServo;
//    private Telemetry telemetry;
//
//    public enum ArmState {
//        TRANSFER(0.0), // Replace with actual positions
//        EXTENDED_FULL(0.0),
//        EXTENDED_BACK(0.0),
//        SPECIMEN_PICKUP(0.0);
//
//        private final double position;
//        ArmState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum WristState {
//        PICKUP(0.0),
//        TRANSFER(0.0),
//        PICKUP_BEFORE(0.0);
//
//        private final double position;
//        WristState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum WristPivotState {
//        HORIZONTAL(0.0),
//        VERTICAL(0.0),
//        TRANSFER(0.0);
//
//        private final double position;
//        WristPivotState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum ClawState {
//        OPEN_CLAW(0.0),
//        CLOSE_CLAW(0.0),
//        CLOSE_FULL(0.0);
//
//        private final double position;
//        ClawState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    private ArmState armState = ArmState.TRANSFER;
//    private WristState wristState = WristState.PICKUP;
//    private WristPivotState wristPivotState = WristPivotState.HORIZONTAL;
//    private ClawState clawState = ClawState.OPEN_CLAW;
//
//    public IntakeSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
//        this.telemetry = telemetry;
//        armServo = hardwareMap.get(Servo.class, "NintakeArm");
//        wristServo = hardwareMap.get(Servo.class, "NintakeWrist");
//        wristPivotServo = hardwareMap.get(Servo.class, "NintakeWristPivot");
//        clawServo = hardwareMap.get(Servo.class, "NintakeClaw");
//    }
//
//    public void intakeToPosition(ArmState arm, WristState wrist, WristPivotState pivot, ClawState claw) {
//        armState = arm;
//        wristState = wrist;
//        wristPivotState = pivot;
//        clawState = claw;
//
//        armServo.setPosition(arm.getPosition());
//        wristServo.setPosition(wrist.getPosition());
//        wristPivotServo.setPosition(pivot.getPosition());
//        clawServo.setPosition(claw.getPosition());
//    }
//
//    public void setWristPivot(WristPivotState state) {
//        wristPivotState = state;
//        wristPivotServo.setPosition(state.getPosition());
//    }
//
//    public void prepareTransfer() {
//        intakeToPosition(ArmState.TRANSFER, WristState.TRANSFER, wristPivotState, ClawState.OPEN_CLAW);
//    }
//
//    public void completeTransfer() {
//        intakeToPosition(ArmState.TRANSFER, wristState, WristPivotState.TRANSFER, ClawState.CLOSE_FULL);
//    }
//
//    public void openClaw() {
//        clawState = ClawState.OPEN_CLAW;
//        clawServo.setPosition(clawState.getPosition());
//    }
//
//    public ArmState getArmState() { return armState; }
//    public WristState getWristState() { return wristState; }
//    public WristPivotState getWristPivotState() { return wristPivotState; }
//    public ClawState getClawState() { return clawState; }
//}
//
//class ElevatorSubsystem extends SubsystemBase {
//    private final DcMotorEx viperMotor;
//    private final Servo armServo, wristServo, wristPivotServo, clawServo;
//    private final PIDFController elevatorController;
//    private Telemetry telemetry;
//    private double targetPosition;
//
//    public enum LiftState {
//        GROUND(0), // Replace with actual positions
//        HIGH_BASKET(0),
//        RETRACTED(0);
//
//        private final double position;
//        LiftState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum ArmState {
//        TRANSFER(0.0),
//        BUCKET(0.0),
//        PICKUP_SPECIMEN(0.0),
//        HIGH_BAR(0.0);
//
//        private final double position;
//        ArmState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum WristState {
//        TRANSFER(0.0),
//        BUCKET(0.0),
//        PICKUP_SPECIMEN(0.0),
//        HIGH_BAR(0.0);
//
//        private final double position;
//        WristState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum WristPivotState {
//        HIGH_BAR(0.0),
//        VERTICAL(0.0),
//        SPECIMEN_PICKUP(0.0);
//
//        private final double position;
//        WristPivotState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    public enum ClawState {
//        OPEN_CLAW(0.0),
//        CLOSE_CLAW(0.0);
//
//        private final double position;
//        ClawState(double position) { this.position = position; }
//        public double getPosition() { return position; }
//    }
//
//    private LiftState liftState = LiftState.RETRACTED;
//    private ArmState armState = ArmState.TRANSFER;
//    private WristState wristState = WristState.TRANSFER;
//    private WristPivotState wristPivotState = WristPivotState.VERTICAL;
//    private ClawState clawState = ClawState.OPEN_CLAW;
//
//    public ElevatorSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
//        this.telemetry = telemetry;
//        viperMotor = hardwareMap.get(DcMotorEx.class, "viper1motor");
//        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
//        viperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        viperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        armServo = hardwareMap.get(Servo.class, "OuttakeArm");
//        wristServo = hardwareMap.get(Servo.class, "OuttakeWrist");
//        wristPivotServo = hardwareMap.get(Servo.class, "OuttakeWristPivot");
//        clawServo = hardwareMap.get(Servo.class, "OuttakeClaw");
//
//        elevatorController = new PIDFController(0.016, 0, 0.00008, 0);
//        elevatorController.setTolerance(20);
//        targetPosition = 0;
//    }
//
//    @Override
//    public void periodic() {
//        viperMotor.setPower(elevatorController.calculate(getViperPosition(), targetPosition));
//        telemetry.addData("Viper Current Position", getViperPosition());
//        telemetry.addData("Viper Target Position", targetPosition);
//        telemetry.addData("Viper Power", viperMotor.getPower());
//    }
//
//    public void elevatorToPosition(LiftState state) {
//        liftState = state;
//        targetPosition = state.getPosition();
//    }
//
//    public void manipulatorToPosition(ArmState arm, WristState wrist, WristPivotState pivot, ClawState claw) {
//        armState = arm;
//        wristState = wrist;
//        wristPivotState = pivot;
//        clawState = claw;
//
//        armServo.setPosition(arm.getPosition());
//        wristServo.setPosition(wrist.getPosition());
//        wristPivotServo.setPosition(pivot.getPosition());
//        clawServo.setPosition(claw.getPosition());
//    }
//
//    public void setWristPivot(WristPivotState state) {
//        wristPivotState = state;
//        wristPivotServo.setPosition(state.getPosition());
//    }
//
//    public void preparePickup() {
//        manipulatorToPosition(ArmState.HIGH_BAR, wristState, wristPivotState, clawState);
//    }
//
//    public void completePickup() {
//        manipulatorToPosition(ArmState.HIGH_BAR, WristState.HIGH_BAR, WristPivotState.HIGH_BAR, clawState);
//    }
//
//    public void receiveTransfer() {
//        manipulatorToPosition(ArmState.TRANSFER, WristState.TRANSFER, WristPivotState.HIGH_BAR, clawState);
//    }
//
//    public void closeClaw() {
//        clawState = ClawState.CLOSE_CLAW;
//        clawServo.setPosition(clawState.getPosition());
//    }
//
//    public void toHighBasket() {
//        elevatorToPosition(LiftState.HIGH_BASKET);
//        manipulatorToPosition(ArmState.BUCKET, WristState.BUCKET, WristPivotState.HIGH_BAR, clawState);
//    }
//
//    public void prepareForGround() {
//        manipulatorToPosition(ArmState.TRANSFER, WristState.PICKUP_SPECIMEN, WristPivotState.HIGH_BAR, clawState);
//    }
//
//    public void toGround() {
//        elevatorToPosition(LiftState.GROUND);
//    }
//
//    public int getViperPosition() {
//        return viperMotor.getCurrentPosition();
//    }
//}