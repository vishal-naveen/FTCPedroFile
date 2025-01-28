package Subsystem;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class Push3Specimen {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(8.4, 62.8, Math.toRadians(0));
    private static final Pose preload = new Pose(42, 76.2, Math.toRadians(0));
    private static final Pose preloadBefore = new Pose(36, 76.2, Math.toRadians(0));
    private static final Pose preloadToBackPos = new Pose(23.3, 68.1, Math.toRadians(0));

    // Score poses updated with before positions
    private static final Pose scoreBlock1 = new Pose(41.25, 77.2, Math.toRadians(0));
    private static final Pose scoreBlockBefore1 = new Pose(36, 77.2, Math.toRadians(0));
    private static final Pose scoreBlock2 = new Pose(41.25, 76.8, Math.toRadians(0));
    private static final Pose scoreBlockBefore2 = new Pose(37, 76.8, Math.toRadians(0));
    private static final Pose scoreBlock3 = new Pose(41.25, 76.4, Math.toRadians(0));
    private static final Pose scoreBlockBefore3 = new Pose(36, 76.4, Math.toRadians(0));

    private static final Pose scoreBlock4 = new Pose(41.25, 76, Math.toRadians(0));
    private static final Pose scoreBlockBefore4 = new Pose(36, 76, Math.toRadians(0));

    private static final Pose blueLineUp = new Pose(60, 32.5, Math.toRadians(0));
    private static final Pose pushBlock1 = new Pose(15, 33, Math.toRadians(0));
    private static final Pose pushBlock2 = new Pose(13.5, 26, Math.toRadians(0));
    private static final Pose pushBlock3 = new Pose(10.3, 13, Math.toRadians(0));

    private static final Pose pushBlock3Direct = new Pose(6, 13, Math.toRadians(0));

    private static final Pose pushBlock3Pick = new Pose(16.1, 35, Math.toRadians(0));
    private static final Pose pickUp = new Pose(13.5, 35, Math.toRadians(0));

    private static final Pose pickUpAlt = new Pose(13, 25.9, Math.toRadians(0));

    private static final Pose pickUpAlt2 = new Pose(14.2, 35, Math.toRadians(0));

    private static final Pose parkPickUp = new Pose(22.8, 42.5, Math.toRadians(50));
    private static final Pose parkPos = new Pose(13.5, 31.4, Math.toRadians(0));

    // Updated path declarations
    public static Path scorePreload;

    public static Path pushBlock3ToPickUp;

    public static Path preloadBeforePath;
    public static Path preloadFinalPath;
    public static Path preloadBackPath;
    public static Path preloadToBlueLineUp;
    public static Path blueLineUpToPushBlock1;
    public static Path pushBlock1ToPushBlock2;
    public static Path pushBlock2ToPushBlock3;

    // Updated scoring paths with before positions
    public static Path pushToScoreBefore1;
    public static Path scoreBefore1ToScore1;
    public static Path score1ToPickUp;
    public static Path pickUpToScoreBefore2;
    public static Path scoreBefore2ToScore2;
    public static Path score2ToPickUp;
    public static Path pickUpToScoreBefore3;
    public static Path scoreBefore3ToScore3;
    public static Path score3ToPickUp;

    public static Path pickUpToScoreBefore4;
    public static Path scoreBefore4ToScore4;
    public static Path score4ToPickUp;

    public static Path pushBlockToDirect;



    public static Path park;

    public static Path blueLineDirect;

    public Push3Specimen(Follower follower) {
        Push3Specimen.follower = follower;
    }

    public static PathChain paths() {

        preloadBeforePath = new Path(new BezierLine(new Point(startPose), new Point(preloadBefore)));
        preloadBeforePath.setLinearHeadingInterpolation(preloadBefore.getHeading(), preload.getHeading());

        // Preload paths
        scorePreload = new Path(new BezierLine(new Point(preloadBefore), new Point(preload)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), preloadBefore.getHeading());



        preloadBackPath = new Path(new BezierLine(new Point(preload), new Point(preloadToBackPos)));
        preloadBackPath.setLinearHeadingInterpolation(preload.getHeading(), preloadToBackPos.getHeading());

        // Push block paths
        preloadToBlueLineUp = new Path(new BezierCurve(new Point(preloadToBackPos), new Point(7.2, 38.9), new Point(blueLineUp)));
        preloadToBlueLineUp.setLinearHeadingInterpolation(preloadToBackPos.getHeading(), blueLineUp.getHeading());

        blueLineDirect = new Path(new BezierCurve(new Point(preload), new Point(2.2, 36.5), new Point(blueLineUp)));
        blueLineDirect.setLinearHeadingInterpolation(preload.getHeading(), blueLineUp.getHeading());

        blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(99.84, 10.9), new Point(pushBlock1)));
        blueLineUpToPushBlock1.setLinearHeadingInterpolation(blueLineUp.getHeading(), pushBlock1.getHeading());

        pushBlock1ToPushBlock2 = new Path(new BezierCurve(new Point(pushBlock1), new Point(100, 12), new Point(pushBlock2)));
        pushBlock1ToPushBlock2.setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2.getHeading());

        pushBlock2ToPushBlock3 = new Path(new BezierCurve(new Point(pushBlock2), new Point(100, 9.2), new Point(pushBlock3)));
        pushBlock2ToPushBlock3.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading());

        pushBlockToDirect = new Path(new BezierCurve(new Point(pushBlock3), new Point(100, 9.2), new Point(pushBlock3Direct)));
        pushBlockToDirect.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading());

        // Score 1 paths


        //push to pick up
        pushBlock3ToPickUp = new Path(new BezierCurve(new Point(pushBlock3), new Point(27.2, 24), new Point(pushBlock3Pick)));
        pushBlock3ToPickUp.setLinearHeadingInterpolation(pushBlock3.getHeading(), pickUp.getHeading());

        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3Direct), new Point(26.6, 33.1), new Point(7.5, 77), new Point(scoreBlockBefore1)));
        pushToScoreBefore1.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlockBefore1.getHeading());


//        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3Pick), new Point(12.4, 83), new Point(scoreBlockBefore1)));
//        pushToScoreBefore1.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlockBefore1.getHeading());

        scoreBefore1ToScore1 = new Path(new BezierLine(new Point(scoreBlockBefore1), new Point(scoreBlock1)));
        scoreBefore1ToScore1.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

        score1ToPickUp = new Path(new BezierCurve(new Point(scoreBlock1), new Point(16  , 71.9), new Point(27, 28.7),new Point(pickUpAlt)));
        score1ToPickUp.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUp.getHeading());

        // Score 2 paths
        pickUpToScoreBefore2 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(16.5, 81), new Point(scoreBlockBefore2)));
        pickUpToScoreBefore2.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore2.getHeading());

        scoreBefore2ToScore2 = new Path(new BezierLine(new Point(scoreBlockBefore2), new Point(scoreBlock2)));
        scoreBefore2ToScore2.setLinearHeadingInterpolation(scoreBlockBefore2.getHeading(), scoreBlock2.getHeading());

        score2ToPickUp = new Path(new BezierCurve(new Point(scoreBlock2), new Point(16  , 71.9), new Point(27, 28.7), new Point(pickUpAlt)));
        score2ToPickUp.setLinearHeadingInterpolation(scoreBlock2.getHeading(), pickUp.getHeading());

        // Score 3 paths
        pickUpToScoreBefore3 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(16.5, 81), new Point(scoreBlockBefore3)));
        pickUpToScoreBefore3.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore3ToScore3 = new Path(new BezierLine(new Point(scoreBlockBefore3), new Point(scoreBlock3)));
        scoreBefore3ToScore3.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        score3ToPickUp = new Path(new BezierCurve(new Point(scoreBlock3), new Point(16  , 71.9), new Point(27, 28.7), new Point(pickUpAlt)));
        score3ToPickUp.setLinearHeadingInterpolation(scoreBlock3.getHeading(), pickUp.getHeading());

        pickUpToScoreBefore4 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(16.5, 81), new Point(scoreBlockBefore4)));
        pickUpToScoreBefore4.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore4ToScore4 = new Path(new BezierLine(new Point(scoreBlockBefore4), new Point(scoreBlock4)));
        scoreBefore4ToScore4.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        score4ToPickUp = new Path(new BezierCurve(new Point(scoreBlock4), new Point(22.5, 64.9), new Point(parkPickUp)));
        score4ToPickUp.setLinearHeadingInterpolation(scoreBlock3.getHeading(), parkPickUp.getHeading());

        // Park path
        park = new Path(new BezierLine(new Point(pickUp), new Point(parkPos)));
        park.setLinearHeadingInterpolation(pickUp.getHeading(), parkPos.getHeading());

        // Return as a PathChain with updated sequence
        return follower.pathBuilder()
                .addPath(scorePreload)
                .addPath(preloadBeforePath)
                .addPath(preloadBackPath)
                .addPath(preloadToBlueLineUp)
                .addPath(blueLineUpToPushBlock1)
                .addPath(pushBlock1ToPushBlock2)
                .addPath(pushBlock2ToPushBlock3)
                .addPath(pushToScoreBefore1)
                .addPath(scoreBefore1ToScore1)
                .addPath(score1ToPickUp)
                .addPath(pickUpToScoreBefore2)
                .addPath(scoreBefore2ToScore2)
                .addPath(score2ToPickUp)
                .addPath(pickUpToScoreBefore3)
                .addPath(scoreBefore3ToScore3)
                .addPath(score3ToPickUp)
                .addPath(pickUpToScoreBefore4)
                .addPath(scoreBefore4ToScore4)
                .addPath(score4ToPickUp)
                .addPath(pushBlock3ToPickUp)
                .addPath(park)
                .addPath(pushBlockToDirect)
                .build();
    }
}