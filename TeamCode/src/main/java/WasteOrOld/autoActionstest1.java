//package WasteOrOld;
//
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.pathgen.BezierCurve;
//import com.pedropathing.pathgen.BezierLine;
//import com.pedropathing.pathgen.Path;
//import com.pedropathing.pathgen.PathChain;
//import com.pedropathing.pathgen.Point;
//import com.pedropathing.util.Constants;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import Subsystem.OuttakeWristSubsystem;
//import pedroPathing.constants.FConstants;
//import pedroPathing.constants.LConstants;
//
//@Autonomous(name = "WasteOrOld.autoActionstest1", group = "Examples")
//public class autoActionstest1 extends OpMode {
//
//    private Follower follower;
//    private Timer pathTimer, opmodeTimer;
//    private Servo outtakeWrist;
//    private OuttakeWristSubsystem outtakeWristSubsystem;
//    private int pathState;
//
//    // Poses
//    private final Pose startPose = new Pose(0.5, 73, Math.toRadians(0));
//    private final Pose preload = new Pose(21, 85, Math.toRadians(0));
//    private final Pose scoreBlock = new Pose(27, 85, Math.toRadians(0));
//    private final Pose blueLineUp = new Pose(58.5, 46, Math.toRadians(0));
//    private final Pose pushBlock1 = new Pose(10, 35, Math.toRadians(0));
//    private final Pose pushBlock2 = new Pose(10, 30, Math.toRadians(0));
//    private final Pose pickUp = new Pose(4, 42, Math.toRadians(0));
//
//    // Paths
//    private Path scorePreload, pickUpToScore, scoreToPickUp;
//    private PathChain lineFirstUp;
//
//    @Override
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//
//        // Hardware Initialization
//        outtakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
//        outtakeWristSubsystem = new OuttakeWristSubsystem(hardwareMap);
//        pathState = 0;
//
//        // Setting constants and initializing follower
//        Constants.setConstants(FConstants.class, LConstants.class);
//        follower = new Follower(hardwareMap);
//        follower.setStartingPose(startPose);
//
//        // Building paths
//        buildPaths();
//    }
//
//    public void buildPaths() {
//        // Preload path
//        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
//        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());
//
//        // Line first up path chain
//        lineFirstUp = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(preload), new Point(1, 38), new Point(blueLineUp)))
//                .setConstantHeadingInterpolation(preload.getHeading())
//                .addPath(new BezierCurve(new Point(blueLineUp), new Point(71.6, 35), new Point(pushBlock1)))
//                .setConstantHeadingInterpolation(blueLineUp.getHeading())
//                .addPath(new BezierCurve(new Point(pushBlock1), new Point(71.6, 31), new Point(pushBlock2)))
//                .setConstantHeadingInterpolation(pushBlock1.getHeading())
//                .build();
//
//        // Pickup to score path
//        pickUpToScore = new Path(new BezierLine(new Point(pickUp), new Point(scoreBlock)));
//        pickUpToScore.setConstantHeadingInterpolation(pickUp.getHeading());
//
//        // Score to pickup path
//        scoreToPickUp = new Path(new BezierLine(new Point(scoreBlock), new Point(pickUp)));
//        scoreToPickUp.setConstantHeadingInterpolation(scoreBlock.getHeading());
//    }
//
//    public void autonomousPathUpdate() {
//        switch (pathState) {
//            case 0:
//                follower.followPath(scorePreload);
//                outtakeWrist.setPosition(0.5);
//                outtakeWristSubsystem.score().schedule();
//                setPathState(1);
//                break;
//            case 1:
//                if (!follower.isBusy()) {
//                    follower.followPath(lineFirstUp, true);
//                    outtakeWrist.setPosition(0.2);
//                    outtakeWristSubsystem.pickup().schedule();
//                    setPathState(2);
//                }
//                break;
//            case 2:
//                if (!follower.isBusy()) {
//                    follower.followPath(scoreToPickUp);
//                    setPathState(3);
//                }
//                break;
//            case 3:
//                if (!follower.isBusy()) {
//                    follower.followPath(pickUpToScore);
//                    outtakeWrist.setPosition(0);
//                    setPathState(4);
//                }
//                break;
//            case 4:
//                if (!follower.isBusy()) {
//                    setPathState(-1); // End of autonomous
//                }
//                break;
//        }
//    }
//
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    @Override
//    public void loop() {
//        // Update follower and autonomous path
//        follower.update();
//        autonomousPathUpdate();
//
//        // Telemetry feedback
//        telemetry.addData("Path State", pathState);
//        telemetry.addData("X", follower.getPose().getX());
//        telemetry.addData("Y", follower.getPose().getY());
//        telemetry.addData("Heading", follower.getPose().getHeading());
//        telemetry.update();
//    }
//
//    @Override
//    public void init_loop() {}
//
//    @Override
//    public void start() {
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    @Override
//    public void stop() {}
//}
//
