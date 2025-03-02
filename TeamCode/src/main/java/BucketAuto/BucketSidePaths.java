package BucketAuto;

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

public class BucketSidePaths {
    static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(9, 111, Math.toRadians(180));
    private static final Pose scorePose = new Pose(16.5, 129, Math.toRadians(140));
    private static final Pose pickUpBlock1Pos = new Pose(25, 123.5, Math.toRadians(170));
    private static final Pose pickUpBlock2Pos = new Pose(25, 133.3, Math.toRadians(170));

    private static final Pose pickUpBlock3BEFORE = new Pose(44.8, 115.6, Math.toRadians(265));
    private static final Pose pickUpBlock3Pos = new Pose(44.8, 121, Math.toRadians(265));
    private static final Pose park = new Pose(62.6, 95.2, Math.toRadians(270));

    private static final Pose pandoraPoint = new Pose(11.093, 96, Math.toRadians(90));


    // Paths
    public static Path waitPre;
    public static Path scorePreload;
    public static Path pickUp1Path;
    public static Path pickUp2Path;
    public static Path pickUp3Path;
    public static Path score1Path;
    public static Path score2Path;
    public static Path score3Path;
    public static Path parkPath;

    public static Path pickUpPath3PRE;
    public static Path pickUpPath3Grab;

    public static Path pandoraPick;
    public static Path pandoraScore;

    public BucketSidePaths(Follower follower) {
        BucketSidePaths.follower = follower;
    }

    public static PathChain paths() {

        waitPre = new Path(new BezierLine(new Point(startPose),  new Point(pickUpBlock1Pos)));
        waitPre.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
        // Preload path to score
        scorePreload = new Path(new BezierLine(new Point(pickUpBlock1Pos), new Point(scorePose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());


        pandoraPick = new Path(new BezierLine(new Point(scorePose), new Point(pandoraPoint)));
        pandoraPick.setLinearHeadingInterpolation(scorePose.getHeading(), pandoraPoint.getHeading());


        pandoraScore = new Path(new BezierLine(new Point(pandoraPoint), new Point(scorePose)));
        pandoraScore.setLinearHeadingInterpolation(pandoraPoint.getHeading(), scorePose.getHeading());

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

        pickUpPath3PRE = new Path(new BezierLine(new Point(scorePose),  new Point(pickUpBlock3BEFORE)));
        pickUpPath3PRE.setLinearHeadingInterpolation(scorePose.getHeading(), pickUpBlock3Pos.getHeading());

        pickUp3Path = new Path(new BezierLine(new Point(pickUpBlock3BEFORE),  new Point(pickUpBlock3Pos)));
        pickUp3Path.setConstantHeadingInterpolation(pickUpBlock3BEFORE.getHeading());

        pickUpPath3Grab = new Path(new BezierLine(new Point(pickUpBlock3Pos),  new Point(pickUpBlock3BEFORE)));
        pickUpPath3Grab.setConstantHeadingInterpolation(pickUpBlock3BEFORE.getHeading());

        // Score third block
        score3Path = new Path(new BezierLine(new Point(pickUpBlock3BEFORE), new Point(scorePose)));
        score3Path.setLinearHeadingInterpolation(pickUpBlock3Pos.getHeading(), scorePose.getHeading());

        // Park path
        parkPath = new Path(new BezierCurve(new Point(scorePose), new Point(94.6, 119.5), new Point(park)));
        parkPath.setLinearHeadingInterpolation(scorePose.getHeading(), park.getHeading());

        // Return as a PathChain
        return follower.pathBuilder()
                .addPath(waitPre)
                .addPath(scorePreload)
                .addPath(pickUp1Path)
                .addPath(score1Path)
                .addPath(pickUp2Path)
                .addPath(score2Path)
                .addPath(pickUp3Path)
                .addPath(score3Path)
                .addPath(parkPath)
                .addPath(pickUpPath3PRE)
                .addPath(pickUpPath3Grab)
                .build();
    }
}