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

@Autonomous(name = "bucketTest1", group = "Auto Testing")
public class bucketTest1 extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;


    private DcMotor viperMotor, leftFront, leftRear, rightFront, rightRear = null;


    private Servo NintakeWrist = null;
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
    private final Pose startPose = new Pose(9, 111, Math.toRadians(180));

    /** Scoring Pose of our robot */

    private final Pose scorePose = new Pose(23.7, 134.9, Math.toRadians(180));

    private final Pose pickUpBlock1Pos = new Pose(25.4, 120.96, Math.toRadians(180));
    private final Pose pickUpBlock2Pos = new Pose(25.4, 131.2, Math.toRadians(180));
    private final Pose pickUpBlock3Pos = new Pose(29.5, 127.2, Math.toRadians(225));

    private final Pose park = new Pose(62.6, 98.2, Math.toRadians(90));



    /** Park Control Pose for our robot, this is used to manipulate the bezier curve that we will create for the parking.
     * The Robot will not go to this pose, it is used a control point for our bezier curve. */

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private Path scorePreload, pickUp1Path, pickUp2Path, pickUp3Path, score1Path, score2Path, score3Path, parkPath;

    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {
        // Preload path to score
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

        // Path to first pickup
        pickUp1Path = new Path(new BezierLine(new Point(scorePose), new Point(pickUpBlock1Pos)));
        pickUp1Path.setLinearHeadingInterpolation(scorePose.getHeading(), pickUpBlock1Pos.getHeading());

        // Score first block
        score1Path = new Path(new BezierLine(new Point(pickUpBlock1Pos), new Point(scorePose)));
        score1Path.setLinearHeadingInterpolation(pickUpBlock1Pos.getHeading(), scorePose.getHeading());

        // Path to second pickup
        pickUp2Path = new Path(new BezierLine(new Point(scorePose), new Point(pickUpBlock2Pos)));
        pickUp2Path.setLinearHeadingInterpolation(scorePose.getHeading(), pickUpBlock2Pos.getHeading());

        // Score second block
        score2Path = new Path(new BezierLine(new Point(pickUpBlock2Pos), new Point(scorePose)));
        score2Path.setLinearHeadingInterpolation(pickUpBlock2Pos.getHeading(), scorePose.getHeading());

        // Path to third pickup
        pickUp3Path = new Path(new BezierLine(new Point(scorePose), new Point(pickUpBlock3Pos)));
        pickUp3Path.setLinearHeadingInterpolation(scorePose.getHeading(), pickUpBlock3Pos.getHeading());

        // Score third block
        score3Path = new Path(new BezierLine(new Point(pickUpBlock3Pos), new Point(scorePose)));
        score3Path.setLinearHeadingInterpolation(pickUpBlock3Pos.getHeading(), scorePose.getHeading());

        // Park path
        parkPath = new Path(new BezierCurve(new Point(scorePose), new Point(94.6, 119.5), new Point(park)));
        parkPath.setLinearHeadingInterpolation(scorePose.getHeading(), park.getHeading());
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
                follower.followPath(scorePreload);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(pickUp1Path);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(score1Path);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(pickUp2Path);
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    follower.followPath(score2Path);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(pickUp3Path);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(score3Path);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    follower.followPath(parkPath);
                    setPathState(8);
                }
                break;
            case 8:
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

