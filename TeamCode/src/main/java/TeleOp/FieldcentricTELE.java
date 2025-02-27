package TeleOp;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import IntakeSubsystem.BucketSideAutoSubsystem;
import Positions.Commands;
import Positions.RobotPose;
import Positions.positions_motor;
import Subsystem.OuttakeSubsystem;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name="FieldcentricTELE_Command")
public class FieldcentricTELE extends CommandOpMode {
    // Subsystems
    private Follower follower;
    private OuttakeSubsystem outtakeSubsystem;
    private BucketSideAutoSubsystem bucketSubsystem;

    // Path Variables
    private Path pickUpToScoreBefore2;
    private Path scoreBefore2ToScore2;
    private Path score2ToPickUp;
    private final Pose pickUp = new Pose(12, 28.75, Math.toRadians(0));
    private final Pose scoreBefore2 = new Pose(32.5, 76.7, Math.toRadians(0));
    private final Pose score2 = new Pose(40, 76.7, Math.toRadians(0));

    // Hardware
    private DcMotor viperMotor;
    private Servo IntakeArmLeft, IntakeArmRight;
    private Servo NintakeWrist, NintakeWristPivot, NintakeClaw;
    private Servo OuttakeArmLeft, OuttakeArmRight;
    private Servo OuttakeWrist, OuttakeWristPivot, OuttakeClaw;

    // Controllers
    private GamepadEx driver;
    private GamepadEx operator;

    // State variables
    private boolean isAutoSequenceActive = false;
    private double power = 1;
    private boolean isOutakeHorizontal = false;
    private boolean isWristHorizontal = false;

    @Override
    public void initialize() {
        // Initialize hardware
        initializeHardware();

        // Set up paths
        initializePaths();

        // Set up controllers
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        // Set up subsystems
        outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, follower);
        bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        // Schedule continuous commands
        schedule(new RunCommand(follower::update));
        schedule(new RunCommand(() -> {
            if (!isAutoSequenceActive) {
                ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightFront")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "leftRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                ((DcMotorEx) hardwareMap.get(DcMotorEx.class, "rightRear")).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }));

        // Set up default drive command
        schedule(new RunCommand(() -> {
            if (!isAutoSequenceActive) {
                follower.setTeleOpMovementVectors(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x,
                        false
                );
            }
        }));

        // Button bindings - Driver
        configureDriverBindings();

        // Button bindings - Operator
        configureOperatorBindings();

        // Telemetry
        schedule(new RunCommand(telemetry::update));
    }

    private void initializeHardware() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(RobotPose.stopPose);
        follower.setMaxPower(0.9);

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        IntakeArmLeft = hardwareMap.get(Servo.class, "IntakeArmLeft");
        IntakeArmRight = hardwareMap.get(Servo.class, "IntakeArmRight");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");
        OuttakeArmLeft = hardwareMap.get(Servo.class, "OuttakeArmLeft");
        OuttakeArmRight = hardwareMap.get(Servo.class, "OuttakeArmRight");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");
    }
//a
    private void initializePaths() {
        pickUpToScoreBefore2 = new Path(new BezierLine(
                new Point(pickUp),
                new Point(scoreBefore2)
        ));
        pickUpToScoreBefore2.setConstantHeadingInterpolation(Math.toRadians(0));

        scoreBefore2ToScore2 = new Path(new BezierLine(
                new Point(scoreBefore2),
                new Point(score2)
        ));
        scoreBefore2ToScore2.setConstantHeadingInterpolation(Math.toRadians(0));

        score2ToPickUp = new Path(new BezierLine(
                new Point(score2),
                new Point(pickUp)
        ));
        score2ToPickUp.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    private void configureDriverBindings() {
        // B button to start auto sequence
        driver.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(new InstantCommand(() -> {
                    if (!isAutoSequenceActive) {
                        startAutoSequence();
                        follower.setCurrentPoseWithOffset(new Pose(4, 28.75, Math.toRadians(0)));
                    }
                }));

        // A button to reset heading
        driver.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new InstantCommand(() ->
                        follower.setCurrentPoseWithOffset(new Pose(4, 28.75, Math.toRadians(0)))
                ));

        // Y button for up intake
        driver.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new InstantCommand(() -> {
                    IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
                    IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                }));

        // Down on D-pad to extend intake
        driver.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new InstantCommand(() -> {
                    IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
                    IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
                }));

        // Stop motor on touchpad
//        driver.getGamepadButton(GamepadKeys.Button.TOUCHPAD)
//                .whenPressed(new InstantCommand(() -> {
//                    viperMotor.setPower(0);
//                    // Cancel ground timer if it's active
//                }));

        // Y button for manual viper movement
        driver.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new InstantCommand(() -> {
                    viperMotor.setPower(-0.3);
                    // Cancel ground timer if it's active
                }));
    }

    private void configureOperatorBindings() {
        // Left bumper for wrist pivot toggle
        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(new InstantCommand(() -> {
                    isWristHorizontal = !isWristHorizontal;
                    NintakeWristPivot.setPosition(isWristHorizontal ?
                            positions_motor.NIntakeWristPivotHorizontal :
                            positions_motor.NIntakeWristPivotVertical);
                }));

        // Right bumper for outtake wrist pivot toggle
        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(new InstantCommand(() -> {
                    isOutakeHorizontal = !isOutakeHorizontal;
                    OuttakeWristPivot.setPosition(isOutakeHorizontal ?
                            positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR :
                            positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                }));

        // D-pad left for intake claw open
        operator.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(new InstantCommand(() ->
                        NintakeClaw.setPosition(positions_motor.NIntakeClawOpen)
                ));

        // D-pad right for intake claw close
        operator.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(new InstantCommand(() ->
                        NintakeClaw.setPosition(positions_motor.NIntakeClawClose)
                ));

        // B button for pickup sequence
        operator.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(createPickupCommand());

        // A button for outtake to pickup position
        operator.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                }));

        // X button for outtake flick position
        operator.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_FLICK);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_FLICK);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_FLICK);
                }));

        // Down on D-pad for transfer position
        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                }));

        // Y button for transfer wait position
        operator.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER_WAIT);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER_WAIT);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                }));

        // Touchpad for transfer sequence
//        operator.getGamepadButton(GamepadKeys.Button.TOUCHPAD)
//                .whenPressed(createTransferCommand());

        // Start button for viper down sequence
        operator.getGamepadButton(GamepadKeys.Button.START)
                .whenPressed(createViperDownCommand());

        // Back button for high bucket position
        operator.getGamepadButton(GamepadKeys.Button.BACK)
                .whenPressed(new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_HIGHBASKET);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                }));

        // D-pad up for intake pickup
        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(new InstantCommand(() ->
                        NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore)
                ));

        // Left trigger for open outtake claw
        schedule(new RunCommand(() -> {
            if (gamepad2.left_trigger > 0.25) {
                OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
            }
        }));

        // Right trigger for close outtake claw
        schedule(new RunCommand(() -> {
            if (gamepad2.right_trigger > 0.25) {
                OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE);
                Commands.closeClaw(outtakeSubsystem).raceWith(Commands.sleep(100));
            }
        }));

        // Left/right stick for intake arm positions
        schedule(new RunCommand(() -> {
            if (gamepad2.left_stick_y > 0.5) {
                IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
                IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
            }
            if (gamepad2.left_stick_y < -0.5) {
                IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_EXTEND_FULL);
                IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_EXTEND_FULL);
                NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
            }
            if (gamepad2.right_stick_y > 0.5) {
                NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
            }
            if (gamepad2.right_stick_y < -0.5) {
                NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
            }
        }));
    }

    private SequentialCommandGroup createPickupCommand() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                }),
                new WaitCommand(250),
                new InstantCommand(() -> {
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                })
        );
    }

    private SequentialCommandGroup createTransferCommand() {
        return new SequentialCommandGroup(
                // Phase 1: Initial positioning
                new InstantCommand(() -> {
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_OPEN);
                    IntakeArmLeft.setPosition(positions_motor.STATE_INTAKELEFTARM_CLOSE);
                    IntakeArmRight.setPosition(positions_motor.STATE_INTAKERIGHTARM_CLOSE);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                    NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                }),
                new WaitCommand(150),

                // Phase 2: Move outtake to transfer position
                new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                }),
                new WaitCommand(200),

                // Phase 3: Close outtake claw
                new InstantCommand(() -> OuttakeClaw.setPosition(positions_motor.STATE_OUTTAKECLAW_CLOSE)),
                new WaitCommand(150),

                // Phase 4: Open intake claw
                new InstantCommand(() -> NintakeClaw.setPosition(positions_motor.NIntakeClawOpen)),
                new WaitCommand(10),

                // Phase 5: Move to scoring position
                new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                }),
                new WaitCommand(100),

                // Phase 6: Reset intake
                new InstantCommand(() -> {
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                })
        );
    }

    private SequentialCommandGroup createViperDownCommand() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_TRANSFER_WAIT);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_TRANSFER_WAIT);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_TRANSFER);
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                }),
                new WaitCommand(750),
                new InstantCommand(() -> {
                    viperMotor.setTargetPosition((int)positions_motor.VIPER_GROUND);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                })
        );
    }

    private void startAutoSequence() {
        isAutoSequenceActive = true;

        SequentialCommandGroup autoCommand = new SequentialCommandGroup(
                // First cycle
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.flick(outtakeSubsystem))
                        .andThen(new RunCommand(() -> {
                            follower.startTeleopDrive();
                            follower.setTeleOpMovementVectors(0.3, 0, 0, false);
                        }).withTimeout(300))
                        .andThen(Commands.openClaw(outtakeSubsystem))
                        .andThen(Commands.pickUpPOS(outtakeSubsystem))
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(2000)),

                // Second cycle
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.flick(outtakeSubsystem))
                        .andThen(new RunCommand(() -> {
                            follower.startTeleopDrive();
                            follower.setTeleOpMovementVectors(0.3, 0, 0, false);
                        }).withTimeout(300))
                        .andThen(Commands.openClaw(outtakeSubsystem))
                        .andThen(Commands.pickUpPOS(outtakeSubsystem))
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(2000)),

                // Third cycle
                Commands.closeClawThenScore(outtakeSubsystem)
                        .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                        .andThen(Commands.flick(outtakeSubsystem))
                        .andThen(new RunCommand(() -> {
                            follower.startTeleopDrive();
                            follower.setTeleOpMovementVectors(0.3, 0, 0, false);
                        }).withTimeout(300))
                        .andThen(Commands.openClaw(outtakeSubsystem))
                        .andThen(Commands.pickUpPOS(outtakeSubsystem))
                        .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(2000))
                        .andThen(new InstantCommand(() -> {
                            isAutoSequenceActive = false;
                            follower.startTeleopDrive();
                        }))
        );

        schedule(autoCommand);

        // Check for joystick input to cancel
        schedule(new RunCommand(() -> {
            boolean hasJoystickInput = Math.abs(gamepad1.left_stick_x) > 0.1 ||
                    Math.abs(gamepad1.left_stick_y) > 0.1 ||
                    Math.abs(gamepad1.right_stick_x) > 0.1;

            if (isAutoSequenceActive && hasJoystickInput) {
                cancelAutoSequence();
            }
        }));
    }

    private void cancelAutoSequence() {
        CommandScheduler.getInstance().cancelAll();
        isAutoSequenceActive = false;
        follower.breakFollowing();
        follower.setTeleOpMovementVectors(0, 0, 0, false);
        follower.startTeleopDrive();

        // Reinitialize
        initialize();
    }

    @Override
    public void runOpMode() {
        initialize();
        waitForStart();
        follower.startTeleopDrive();

        while (opModeIsActive() && !isStopRequested()) {
            run();

            // Handle viper motor state
            updateViperMotorState();

            // Add telemetry
            telemetry.addData("viper power", viperMotor.getPower());
            telemetry.addData("viper target", viperMotor.getTargetPosition());
            telemetry.addData("viper pos", viperMotor.getCurrentPosition());
            telemetry.addData("Auto Active", isAutoSequenceActive);
        }
    }

    private void updateViperMotorState() {
        if (viperMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = viperMotor.getCurrentPosition();
            int targetPos = viperMotor.getTargetPosition();

            // Simplified position control
            if (Math.abs(currentPos - targetPos) <= 20) {
                viperMotor.setPower(0.1); // Holding power
            } else {
                viperMotor.setPower(1);
            }
        }
    }
}