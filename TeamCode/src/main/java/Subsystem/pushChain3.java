package Subsystem;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class pushChain3 {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(9.4, 62.8, Math.toRadians(0));
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
    public static PathChain lineFirstUp;
    public static Path blockPushToScorePath;
    public static Path pushScoreToPickUpPath;
    public static Path pickUpToScore1;
    public static Path scoreToPickUp1;
    public static Path pickUpToScore2;
    public static Path scoreToPickUp2;
    public static Path pickUpToScore3;
    public static Path scoreToPickUp3;
    public static Path park;

    public pushChain3(Follower follower) {
        pushChain3.follower = follower;  // Fixed constructor name and assignment
    }

    public static PathChain paths() {
        // Preload path
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), preload.getHeading());

        preloadBackPath = new Path(new BezierLine(new Point(preload), new Point(preloadToBackPos)));
        preloadBackPath.setLinearHeadingInterpolation(preload.getHeading(), preloadToBackPos.getHeading());

// Line up path chain
        lineFirstUp = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(preloadToBackPos), new Point(8.2, 37.8), new Point(blueLineUp)))
                .setLinearHeadingInterpolation(preloadToBackPos.getHeading(), blueLineUp.getHeading())
                .addPath(new BezierCurve(new Point(blueLineUp), new Point(93.44, 24.96), new Point(pushBlock1)))
                .setLinearHeadingInterpolation(blueLineUp.getHeading(), pushBlock1.getHeading())
                .addPath(new BezierCurve(new Point(pushBlock1), new Point(97.92, 29.3), new Point(71.04, 11.1), new Point(pushBlock2)))
                .setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2.getHeading())
//                .addPath(new BezierCurve(new Point(pushBlock2), new Point(121, 9.7), new Point(pushBlock3)))
//                .setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading())
                .build();

        blockPushToScorePath = new Path(new BezierCurve(new Point(pushBlock2), new Point(12.4, 77.7), new Point(scoreBlock)));
        blockPushToScorePath.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlock.getHeading());

        pushScoreToPickUpPath = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2), new Point(pickUp)));
        pushScoreToPickUpPath.setLinearHeadingInterpolation(scoreBlock.getHeading(), pickUp.getHeading());

// Scoring cycles
        pickUpToScore1 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8), new Point(scoreBlock)));
        pickUpToScore1.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlock.getHeading());

        scoreToPickUp1 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2), new Point(pickUp)));
        scoreToPickUp1.setLinearHeadingInterpolation(scoreBlock.getHeading(), pickUp.getHeading());

        pickUpToScore2 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8), new Point(scoreBlock)));
        pickUpToScore2.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlock.getHeading());

        scoreToPickUp2 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2), new Point(pickUp)));
        scoreToPickUp2.setLinearHeadingInterpolation(scoreBlock.getHeading(), pickUp.getHeading());

        pickUpToScore3 = new Path(new BezierCurve(new Point(pickUp), new Point(16.5, 75.8), new Point(scoreBlock)));
        pickUpToScore3.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlock.getHeading());

        scoreToPickUp3 = new Path(new BezierCurve(new Point(scoreBlock), new Point(16.9, 76.2), new Point(pickUp)));
        scoreToPickUp3.setLinearHeadingInterpolation(scoreBlock.getHeading(), pickUp.getHeading());

// Park path
        park = new Path(new BezierLine(new Point(pickUp), new Point(parkPos)));
        park.setLinearHeadingInterpolation(pickUp.getHeading(), parkPos.getHeading());

        // Park path
        park = new Path(new BezierLine(new Point(pickUp), new Point(parkPos)));
        park.setConstantHeadingInterpolation(pickUp.getHeading());

        // Return as a PathChain
        return follower.pathBuilder()
                .addPath(scorePreload)
                .addPath(preloadBackPath)
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