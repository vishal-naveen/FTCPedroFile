package Subsystem;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class Push3Specimen {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(9.4, 62.8, Math.toRadians(0));

    /** Scoring Pose of our robot */

    private static final Pose preload = new Pose(36.5, 72.96, Math.toRadians(0));

    private static final Pose preloadToBackPos = new Pose(23.3, 68.1, Math.toRadians(0));


    private static final Pose scoreBlock = new Pose(36.3, 69.12, Math.toRadians(0));
    private static final Pose blueLineUp = new Pose(55.1, 34, Math.toRadians(0));
    private static final Pose pushBlock1 = new Pose(11.4, 25.9, Math.toRadians(0));
    private static final Pose pushBlock2 = new Pose(13.1, 18.3, Math.toRadians(0));

    private static final Pose pushBlock3 = new Pose(12.6, 12.4, Math.toRadians(0));


    private static final Pose pickUp = new Pose(13.5, 31.4, Math.toRadians(0));

    private static final Pose parkPos = new Pose(13.5, 31.4, Math.toRadians(0));

    // Paths and PathChains
    public static Path scorePreload;
    public static Path preloadBackPath;
    public static Path preloadToBlueLineUp;
    public static Path blueLineUpToPushBlock1;
    public static Path pushBlock1ToPushBlock2;
    public static Path pushBlock2ToPushBlock3;
    public static Path blockPushToScorePath;
    public static Path pushScoreToPickUpPath;
    public static Path pickUpToScore1;
    public static Path scoreToPickUp1;
    public static Path pickUpToScore2;
    public static Path scoreToPickUp2;
    public static Path pickUpToScore3;
    public static Path scoreToPickUp3;
    public static Path park;


    public Push3Specimen(Follower follower) {
        Push3Specimen.follower = follower;
    }

    public static PathChain paths() {
        // Preload path
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());

        preloadBackPath = new Path(new BezierLine(new Point(preload), new Point(preloadToBackPos)));
        preloadBackPath.setConstantHeadingInterpolation(preload.getHeading());

        preloadToBlueLineUp = new Path(new BezierLine(new Point(preloadToBackPos), new Point(blueLineUp)));
        preloadToBlueLineUp.setConstantHeadingInterpolation(preload.getHeading());

        blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(88.8, 26.3), new Point(pushBlock1)));
        blueLineUpToPushBlock1.setConstantHeadingInterpolation(blueLineUp.getHeading());

        pushBlock1ToPushBlock2 = new Path(new BezierCurve(new Point(pushBlock1), new Point(122.3, 17.92), new Point(pushBlock2)));
        pushBlock1ToPushBlock2.setConstantHeadingInterpolation(pushBlock1.getHeading());

        pushBlock2ToPushBlock3 = new Path(new BezierCurve(new Point(pushBlock2), new Point(121, 9.7), new Point(pushBlock3)));
        pushBlock2ToPushBlock3.setConstantHeadingInterpolation(pushBlock2.getHeading());

        blockPushToScorePath = new Path(new BezierCurve(new Point(pushBlock3), new Point(12.4, 77.7),new Point(scoreBlock)));
        blockPushToScorePath.setConstantHeadingInterpolation(pushBlock2.getHeading());

        pushScoreToPickUpPath = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2),new Point(pickUp)));
        pushScoreToPickUpPath.setConstantHeadingInterpolation(pushBlock2.getHeading());

        pickUpToScore1 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8),new Point(scoreBlock)));
        pickUpToScore1.setConstantHeadingInterpolation(pushBlock2.getHeading());

        scoreToPickUp1 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2),new Point(pickUp)));
        scoreToPickUp1.setConstantHeadingInterpolation(pushBlock2.getHeading());

        pickUpToScore2 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8),new Point(scoreBlock)));
        pickUpToScore2.setConstantHeadingInterpolation(pushBlock2.getHeading());

        scoreToPickUp2 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2),new Point(pickUp)));
        scoreToPickUp2.setConstantHeadingInterpolation(pushBlock2.getHeading());

        pickUpToScore3 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8),new Point(scoreBlock)));
        pickUpToScore3.setConstantHeadingInterpolation(pushBlock2.getHeading());

        scoreToPickUp3 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2),new Point(pickUp)));
        scoreToPickUp3.setConstantHeadingInterpolation(pushBlock2.getHeading());


        // Return as a PathChain
        return follower.pathBuilder()
                .addPath(scorePreload)
                .addPath(preloadBackPath)
                .addPath(preloadToBlueLineUp)
                .addPath(blueLineUpToPushBlock1)
                .addPath(pushBlock1ToPushBlock2)
                .addPath(pushBlock2ToPushBlock3)
                .addPath(blockPushToScorePath)
                .addPath(pushScoreToPickUpPath)
                .addPath(pickUpToScore1)
                .addPath(scoreToPickUp1)
                .addPath(pickUpToScore2)
                .addPath(scoreToPickUp2)
                .addPath(pickUpToScore3)
                .addPath(scoreToPickUp3)
                .addPath(park)
                .build();
    }
}