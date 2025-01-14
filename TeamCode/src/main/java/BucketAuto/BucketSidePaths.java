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
    private static final Pose scorePose = new Pose(23.7, 134.9, Math.toRadians(180));
    private static final Pose pickUpBlock1Pos = new Pose(25.4, 120.96, Math.toRadians(180));
    private static final Pose pickUpBlock2Pos = new Pose(25.4, 131.2, Math.toRadians(180));
    private static final Pose pickUpBlock3Pos = new Pose(29.5, 127.2, Math.toRadians(225));
    private static final Pose park = new Pose(62.6, 98.2, Math.toRadians(270));

    // Paths
    public static Path scorePreload;
    public static Path pickUp1Path;
    public static Path pickUp2Path;
    public static Path pickUp3Path;
    public static Path score1Path;
    public static Path score2Path;
    public static Path score3Path;
    public static Path parkPath;

    public BucketSidePaths(Follower follower) {
        BucketSidePaths.follower = follower;
    }

    public static PathChain paths() {
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

        // Return as a PathChain
        return follower.pathBuilder()
                .addPath(scorePreload)
                .addPath(pickUp1Path)
                .addPath(score1Path)
                .addPath(pickUp2Path)
                .addPath(score2Path)
                .addPath(pickUp3Path)
                .addPath(score3Path)
                .addPath(parkPath)
                .build();
    }
}