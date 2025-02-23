package Subsystem;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class Push3Specimen {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(8.4, 62.8, Math.toRadians(0));
    private static final Pose preload = new Pose(42, 76.2, Math.toRadians(0));
    private static final Pose preloadBefore = new Pose(35, 76.2, Math.toRadians(0));

    private static final Pose preloadToBackPos = new Pose(25, 76.2, Math.toRadians(0));

    // Score poses updated with before positions
    private static final Pose scoreBlock1 = new Pose(40, 78.2, Math.toRadians(0));
    private static final Pose scoreBlockBefore1 = new Pose(35.5, 78.2, Math.toRadians(0));
    private static final Pose scoreBlock2 = new Pose(40, 77.5, Math.toRadians(0));
    private static final Pose scoreBlockBefore2 = new Pose(35.5, 77.5, Math.toRadians(0));
    private static final Pose scoreBlock3 = new Pose(40, 75.8, Math.toRadians(0));
    private static final Pose scoreBlockBefore3 = new Pose(35.5, 75.8, Math.toRadians(0));

    private static final Pose scoreBlock4 = new Pose(40, 74, Math.toRadians(0));
    private static final Pose scoreBlockBefore4 = new Pose(35.5, 74, Math.toRadians(0));

    private static final Pose blueLineUp = new Pose(60, 34.8, Math.toRadians(0));
    private static final Pose pushBlock1 = new Pose(16, 28, Math.toRadians(0));

    private static final Pose pushBlock2Up = new Pose(57.5, 21.6, Math.toRadians(0));
    private static final Pose pushBlock2 = new Pose(13.5, 21.6, Math.toRadians(0));

    private static final Pose pushBlock3Up = new Pose(55, 13.8, Math.toRadians(-5));
    private static final Pose pushBlock3 = new Pose(6, 13.8, Math.toRadians(-5));

    private static final Pose pushBlock3Direct = new Pose(5.8, 13, Math.toRadians(0));

    private static final Pose pushBlock3Pick = new Pose(16.1, 35, Math.toRadians(0));
    private static final Pose pickUp = new Pose(8, 35, Math.toRadians(0));

    private static final Pose pickUpAlt = new Pose(4, 28.75, Math.toRadians(0));

    private static final Pose pickUpAltBefore = new Pose(12, 28.75, Math.toRadians(0));

    private static final Pose pickUpAlt2 = new Pose(11, 25.75, Math.toRadians(0));

    private static final Pose pickUpAlt3 = new Pose(11, 25.75, Math.toRadians(0));


    private static final Pose parkPickUp = new Pose(22.8, 42.5, Math.toRadians(0));
    private static final Pose parkPos = new Pose(13.5, 31.4, Math.toRadians(0));

    // Updated path declarations
    public static Path scorePreload;

    public static Path pushBlock3ToPickUp;

    public static Path preloadBeforePath;

    public static Path preloadBeforePathPRE;
    public static Path preloadFinalPath;
    public static Path preloadBackPath;
    public static Path preloadToBlueLineUp;
    public static Path blueLineUpToPushBlock1;
    public static Path pushBlock1ToPushBlock2;
    public static Path pushBlock2ToPushBlock3;

    // Updated scoring paths with before positions

    public static Path directScore1;
    public static Path pushToScoreBefore1;
    public static Path scoreBefore1ToScore1;
    public static Path score1ToPickUp;
    public static Path score1ToPickUpBefore;
    public static Path pickUpToScoreBefore2;

    public static Path directScore2;
    public static Path scoreBefore2ToScore2;
    public static Path score2ToPickUp;
    public static Path score2ToPickUpBefore;
    public static Path pickUpToScoreBefore3;
    public static Path scoreBefore3ToScore3;
    public static Path score3ToPickUp;
    public static Path directScore3;
    public static Path score3ToPickUpBefore;

    public static Path pickUpToScoreBefore4;
    public static Path directScore4;
    public static Path scoreBefore4ToScore4;
    public static Path score4ToPickUp;


    public static Path pushBlockToDirect;



    public static Path park;

    public static Path blueLineDirect;

    public static Path pushBlock1ToPushBlock2Up;
    public static Path pushBlock2UpToPushBlock2;
    public static Path pushBlock2ToPushBlock3Up;
    public static Path pushBlock3UpToPushBlock3;



    public Push3Specimen(Follower follower) {
        Push3Specimen.follower = follower;
    }

    public static PathChain paths() {

        blueLineDirect = new Path(new BezierCurve(new Point(preload), new Point(21.8, 74), new Point(13.2, 25),new Point(blueLineUp)));
        blueLineDirect.setLinearHeadingInterpolation(preload.getHeading(), blueLineUp.getHeading());

        blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(61.6, 24.4), new Point(pushBlock1)));
        blueLineUpToPushBlock1.setLinearHeadingInterpolation(blueLineUp.getHeading(), pushBlock1.getHeading());
        blueLineUpToPushBlock1.setPathEndVelocityConstraint(1.0);

        pushBlock1ToPushBlock2Up = new Path(new BezierCurve(new Point(pushBlock1), new Point(57.5, 27.8), new Point(pushBlock2Up)));
        pushBlock1ToPushBlock2Up.setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2Up.getHeading());

        pushBlock2UpToPushBlock2 = new Path(new BezierLine(new Point(pushBlock2Up), new Point(pushBlock2)));
        pushBlock2UpToPushBlock2.setLinearHeadingInterpolation(pushBlock2Up.getHeading(), pushBlock2.getHeading());


        pushBlock2ToPushBlock3Up = new Path(new BezierCurve(new Point(pushBlock2), new Point(55, 17.4), new Point(pushBlock3Up)));
        pushBlock2ToPushBlock3Up.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3Up.getHeading());

        pushBlock3UpToPushBlock3 = new Path(new BezierLine(new Point(pushBlock3Up), new Point(pushBlock3)));
        pushBlock3UpToPushBlock3.setLinearHeadingInterpolation(pushBlock3Up.getHeading(), pushBlock3.getHeading());



        pushToScoreBefore1 = new Path(new BezierCurve(new Point(follower.getXOffset(), follower.getYOffset()), new Point(26.6, 33.1), new Point(7.5, 77), new Point(scoreBlockBefore1)));
        pushToScoreBefore1.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlockBefore1.getHeading());

        preloadBeforePathPRE = new Path(new BezierLine(new Point(startPose), new Point(preloadToBackPos)));
        preloadBeforePathPRE.setLinearHeadingInterpolation(startPose.getHeading(), preloadBefore.getHeading());

        preloadBeforePath = new Path(new BezierLine(new Point(preloadToBackPos), new Point(preloadBefore)));
        preloadBeforePath.setLinearHeadingInterpolation(startPose.getHeading(), preloadBefore.getHeading());



        // Preload paths
        scorePreload = new Path(new BezierLine(new Point(preloadBefore), new Point(preload)));
        scorePreload.setLinearHeadingInterpolation(preloadBefore.getHeading(), preload.getHeading());



        preloadBackPath = new Path(new BezierLine(new Point(preload), new Point(preloadToBackPos)));
        preloadBackPath.setLinearHeadingInterpolation(preload.getHeading(), preloadToBackPos.getHeading());

        // Push block paths



        pushBlock1ToPushBlock2 = new Path(new BezierCurve(new Point(pushBlock1), new Point(100, 12), new Point(pushBlock2)));
        pushBlock1ToPushBlock2.setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2.getHeading());

        pushBlock2ToPushBlock3 = new Path(new BezierCurve(new Point(pushBlock2), new Point(100, 9.2), new Point(pushBlock3)));
        pushBlock2ToPushBlock3.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading());

        pushBlockToDirect = new Path(new BezierCurve(new Point(pushBlock3), new Point(100, 9.2), new Point(pushBlock3Direct)));
        pushBlockToDirect.setLinearHeadingInterpolation(pushBlock3.getHeading(), pushBlock3Direct.getHeading());

        // Score 1 paths


        //push to pick up
        pushBlock3ToPickUp = new Path(new BezierCurve(new Point(pushBlock3), new Point(27.2, 24), new Point(pushBlock3Pick)));
        pushBlock3ToPickUp.setLinearHeadingInterpolation(pushBlock3.getHeading(), pushBlock3Pick.getHeading());




//        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3Pick), new Point(12.4, 83), new Point(scoreBlockBefore1)));
//        pushToScoreBefore1.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlockBefore1.getHeading());

        scoreBefore1ToScore1 = new Path(new BezierLine(new Point(scoreBlockBefore1), new Point(scoreBlock1)));
        scoreBefore1ToScore1.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());



        score1ToPickUpBefore = new Path(new BezierCurve(new Point(scoreBlock1), new Point(17.9, 69.2),new Point(pickUpAltBefore)));
        score1ToPickUpBefore.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUpAlt.getHeading());


        score1ToPickUp = new Path(new BezierLine(new Point(pickUpAltBefore), new Point(pickUpAlt)));
        score1ToPickUp.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUpAlt.getHeading());

        directScore1 = new Path(new BezierCurve(new Point(pushBlock3), new Point(scoreBlock1)));
        directScore1.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

        // Score 2 paths
        pickUpToScoreBefore2 = new Path(new BezierLine(new Point(follower.getXOffset(), follower.getYOffset()), new Point(scoreBlockBefore2)));
        pickUpToScoreBefore2.setLinearHeadingInterpolation(pickUpAlt.getHeading(), scoreBlockBefore2.getHeading());



        scoreBefore2ToScore2 = new Path(new BezierLine(new Point(scoreBlockBefore2), new Point(scoreBlock2)));
        scoreBefore2ToScore2.setLinearHeadingInterpolation(scoreBlockBefore2.getHeading(), scoreBlock2.getHeading());

        directScore2 = new Path(new BezierLine(new Point(pickUpAlt), new Point(scoreBlock2)));
        directScore2.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

        score2ToPickUpBefore = new Path(new BezierCurve(new Point(scoreBlock2), new Point(17.9, 69.2),new Point(pickUpAltBefore)));
        score2ToPickUpBefore.setLinearHeadingInterpolation(scoreBlock2.getHeading(), pickUpAlt.getHeading());

        score2ToPickUp = new Path(new BezierLine(new Point(pickUpAltBefore), new Point(pickUpAlt)));
        score2ToPickUp.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUpAlt.getHeading());

        // Score 3 paths
        pickUpToScoreBefore3 = new Path(new BezierLine(new Point(follower.getXOffset(), follower.getYOffset()),  new Point(scoreBlockBefore3)));
        pickUpToScoreBefore3.setLinearHeadingInterpolation(pickUpAlt2.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore3ToScore3 = new Path(new BezierLine(new Point(scoreBlockBefore3), new Point(scoreBlock3)));
        scoreBefore3ToScore3.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        directScore3 = new Path(new BezierLine(new Point(pickUpAlt), new Point(scoreBlock3)));
        directScore3.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

        score3ToPickUpBefore = new Path(new BezierCurve(new Point(scoreBlock3), new Point(17.9, 69.2),new Point(pickUpAltBefore)));
        score3ToPickUpBefore.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUpAlt.getHeading());

        score3ToPickUp = new Path(new BezierLine(new Point(pickUpAltBefore), new Point(pickUpAlt)));
        score3ToPickUp.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUpAlt.getHeading());

        pickUpToScoreBefore4 = new Path(new BezierLine(new Point(follower.getXOffset(), follower.getYOffset()), new Point(scoreBlockBefore4)));
        pickUpToScoreBefore4.setLinearHeadingInterpolation(pickUpAlt3.getHeading(), scoreBlockBefore4.getHeading());

        scoreBefore4ToScore4 = new Path(new BezierLine(new Point(scoreBlockBefore4), new Point(scoreBlock4)));
        scoreBefore4ToScore4.setLinearHeadingInterpolation(scoreBlockBefore4.getHeading(), scoreBlock4.getHeading());

        score4ToPickUp = new Path(new BezierCurve(new Point(scoreBlock4), new Point(22.5, 64.9), new Point(parkPickUp)));
        score4ToPickUp.setLinearHeadingInterpolation(scoreBlock4.getHeading(), parkPickUp.getHeading());

        directScore4 = new Path(new BezierLine(new Point(pickUpAlt), new Point(scoreBlock4)));
        directScore4.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

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
                .addPath(score1ToPickUpBefore)
                .addPath(pickUpToScoreBefore2)
                .addPath(scoreBefore2ToScore2)
                .addPath(score2ToPickUp)
                .addPath(score2ToPickUpBefore)
                .addPath(pickUpToScoreBefore3)
                .addPath(scoreBefore3ToScore3)
                .addPath(score3ToPickUp)
                .addPath(score3ToPickUpBefore)
                .addPath(pickUpToScoreBefore4)
                .addPath(scoreBefore4ToScore4)
                .addPath(score4ToPickUp)
                .addPath(pushBlock3ToPickUp)
                .addPath(park)
                .addPath(pushBlockToDirect)
                .addPath(pushBlock1ToPushBlock2Up)
                .addPath(pushBlock2UpToPushBlock2)
                .addPath(pushBlock2ToPushBlock3Up)
                .addPath(pushBlock3UpToPushBlock3)
                .addPath(directScore1)
                .addPath(directScore2)
                .addPath(directScore3)
                .addPath(directScore4)


                .build();
    }
}