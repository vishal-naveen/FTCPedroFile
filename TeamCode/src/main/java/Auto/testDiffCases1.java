package Auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

/**
 * This is an example auto that showcases movement and control of two servos autonomously.
 * It is a 0+4 (Specimen + Sample) bucket auto. It scores a neutral preload and then pickups 3 samples from the ground and scores them before parking.
 * There are examples of different ways to build paths.
 * A path progression method has been created and can advance based on time, position, or other factors.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 11/28/2024
 */

@Autonomous(name = "testDiffCases1", group = "Auto Testing")
public class testDiffCases1 extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private Servo NintakeWrist = null;

    private DcMotor viperMotor, leftFront, leftRear, rightFront, rightRear = null;



    private Servo NintakeArm = null;
    private Servo NintakeWristPivot = null;
    private Servo NintakeClaw = null;

    private Servo OuttakeArm = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;
    private Servo OuttakeClaw = null;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState;

    /* Create and Define Poses + Paths
     * Poses are built with three constructors: x, y, and heading (in Radians).
     * Pedro uses 0 - 144 for x and y, with 0, 0 being on the bottom left.
     * (For Into the Deep, this would be Blue Observation Zone (0,0) to Red Observation Zone (144,144).)
     * Even though Pedro uses a different coordinate system than RR, you can convert any roadrunner pose by adding +72 both the x and y.
     * This visualizer is very easy to use to find and create paths/pathchains/poses: <https://pedro-path-generator.vercel.app/>
     * Lets assume our robot is 18 by 18 inches
     * Lets assume the Robot is facing the human player and we want to score in the bucket */

    /** Start Pose of our robot */
    /** Start Pose of our robot */
    private final Pose startPose = new Pose(0.5, 73, Math.toRadians(0));

    /** Scoring Pose of our robot */

    private final Pose preload = new Pose(27, 78.5, Math.toRadians(0));

    private final Pose scoreBlock = new Pose(27, 85, Math.toRadians(0));
    private final Pose blueLineUp = new Pose(58.5, 46, Math.toRadians(0));
    private final Pose pushBlock1 = new Pose(10, 35, Math.toRadians(0));
    private final Pose pushBlock2 = new Pose(10, 26, Math.toRadians(0));

    private final Pose pushBlock3 = new Pose(10, 30, Math.toRadians(0));


    private final Pose pickUp = new Pose(4, 42, Math.toRadians(0));


    /** Park Control Pose for our robot, this is used to manipulate the bezier curve that we will create for the parking.
     * The Robot will not go to this pose, it is used a control point for our bezier curve. */

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private Path scorePreload, pickUpToScore, scoreToPickUp, firstPickUp;
    private PathChain lineFirstUp, block1, block2, block3;

    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {
        // Preload path
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());

        lineFirstUp = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(preload), new Point(1, 40.6), new Point(blueLineUp)))
                .setConstantHeadingInterpolation(preload.getHeading())
                .addPath(new BezierCurve(new Point(blueLineUp), new Point(79.9, 32.5), new Point(pushBlock1)))
                .setConstantHeadingInterpolation(blueLineUp.getHeading())
                .addPath(new BezierCurve(new Point(pushBlock1), new Point(110, 28.5), new Point(pushBlock2)))
                .setConstantHeadingInterpolation(pushBlock1.getHeading())
                .build();

        firstPickUp = new Path(new BezierLine(new Point(pushBlock2), new Point(pickUp)));
        firstPickUp.setConstantHeadingInterpolation(pushBlock2.getHeading());

        pickUpToScore = new Path(new BezierLine(new Point(pickUp), new Point(scoreBlock)));
        pickUpToScore.setConstantHeadingInterpolation(pickUp.getHeading());

        scoreToPickUp = new Path(new BezierLine(new Point(scoreBlock), new Point(pickUp)));
        scoreToPickUp.setConstantHeadingInterpolation(scoreBlock.getHeading());
    }

    public void pickUpFull()
    {
        viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
        OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
    }

    public void scoreFull()
    {
        viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBAR);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        OuttakeArm.setPosition(positions_motor.OuttakeArmHighBar);
        OuttakeWrist.setPosition(positions_motor.OuttakeWristHighBar);
        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
    }

    public void clawOpen()
    {
        OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
    }

    public void clawClose()
    {
        OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
    }




    public void pause(double time) {
        ElapsedTime myTimer = new ElapsedTime();
        myTimer.reset();
        while (myTimer.seconds() < time) {
            telemetry.addData("Elapsed Time", myTimer.seconds());
            telemetry.update();

            follower.holdPoint(follower.getPose());

        }
    }


    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                scoreFull();
                setPathState(1);
                break;
            case 1:
                follower.followPath(scorePreload);
                setPathState(2);
                break;
            case 2:
                clawOpen();
                setPathState(3);
                break;

            case 3:  // Keep original timing case
                if (!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 1) {
                        if(pathTimer.getElapsedTimeSeconds()>2.1) {
                            follower.followPath(lineFirstUp, true);
                            if(pathTimer.getElapsedTimeSeconds() > 3.5) {
                                pickUpFull();
                                setPathState(4);
                                break;
                            }
                        }
                    }
                }
                break;

            case 4:
                follower.followPath(firstPickUp);
                setPathState(5);
                break;
            case 5:
                clawClose();
                setPathState(6);
                break;
            case 6:
                scoreFull();
                setPathState(7);
                break;

            case 7:
                follower.followPath(pickUpToScore);
                setPathState(8);
                break;
            case 8:
                clawOpen();
                setPathState(9);
                break;

            case 9:
                follower.followPath(scoreToPickUp);
                setPathState(10);
                break;
            case 10:
                pickUpFull();
                setPathState(11);
                break;
            case 11:
                clawClose();
                setPathState(12);
                break;
            case 12:
                scoreFull();
                setPathState(13);
                break;

            case 13:
                follower.followPath(pickUpToScore);
                setPathState(14);
                break;
            case 14:
                clawOpen();
                setPathState(15);
                break;

            case 15:
                follower.followPath(scoreToPickUp);
                setPathState(16);
                break;
            case 16:
                pickUpFull();
                setPathState(17);
                break;
            case 17:
                clawClose();
                setPathState(18);
                break;
            case 18:
                scoreFull();
                setPathState(19);
                break;

            case 19:
                follower.followPath(pickUpToScore);
                setPathState(20);
                break;
            case 20:
                clawOpen();
                setPathState(21);
                break;

            case 21:
                follower.followPath(scoreToPickUp);
                setPathState(22);
                break;
            case 22:
                pickUpFull();
                setPathState(23);
                break;
            case 23:
                clawClose();
                setPathState(24);
                break;
            case 24:
                scoreFull();
                setPathState(25);
                break;

            case 25:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }

    /** These change the states of the paths and actions
     * It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();


        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        leftFront = hardwareMap.get(DcMotorEx.class, "FL");
        leftRear = hardwareMap.get(DcMotorEx.class, "BL");
        rightFront = hardwareMap.get(DcMotorEx.class, "FR");
        rightRear = hardwareMap.get(DcMotorEx.class, "BR");

        NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        OuttakeArm.setPosition(positions_motor.OuttakeArmPedroAuto);
        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
    }
}

