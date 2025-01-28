package specimenAutoRenew;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class specimenRENEWPATHS {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(0.8, 72, Math.toRadians(0));
    private static final Pose preload = new Pose(28.9, 82.16, Math.toRadians(0));
    private static final Pose preloadBefore = new Pose(18, 82.16, Math.toRadians(0));
    private static final Pose preloadToBackPos = new Pose(15.7, 77.3, Math.toRadians(0));

    // Score poses updated with before positions
    private static final Pose scoreBlock1 = new Pose(33.9, 86.4, Math.toRadians(0));
    private static final Pose scoreBlockBefore1 = new Pose(28.4, 86.4, Math.toRadians(0));
    private static final Pose scoreBlock2 = new Pose(33.9, 86, Math.toRadians(0));
    private static final Pose scoreBlockBefore2 = new Pose(29.4, 86, Math.toRadians(0));
    private static final Pose scoreBlock3 = new Pose(33.9, 85.6, Math.toRadians(0));
    private static final Pose scoreBlockBefore3 = new Pose(28.4, 85.6, Math.toRadians(0));

    private static final Pose scoreBlock4 = new Pose(33.9, 85.2, Math.toRadians(0));
    private static final Pose scoreBlockBefore4 = new Pose(28.4, 85.2, Math.toRadians(0));


    private static final Pose blueLineUp = new Pose(49, 42, Math.toRadians(0));
    private static final Pose pushBlock1 = new Pose(8.3, 42, Math.toRadians(0));
    private static final Pose pushBlock2 = new Pose(8.7, 33, Math.toRadians(0));
    private static final Pose pushBlock3UP = new Pose(49.7, 19.1, Math.toRadians(0));
    private static final Pose pushBlock3 = new Pose(2.2, 19.1, Math.toRadians(0));



    private static final Pose pickUp = new Pose(6.9, 44.2, Math.toRadians(0));
    private static final Pose pickUpAlt = new Pose(5.7, 44.2, Math.toRadians(0));
    private static final Pose parkPickUp = new Pose(15.2, 51.7, Math.toRadians(50));
    private static final Pose parkPos = new Pose(5.9, 40.6, Math.toRadians(0));
    private static final Pose pushBlock3Pick = new Pose(8.5, 44.2, Math.toRadians(0));

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



    public static Path park;

    public static Path blueLineDirect;

    public static Path blockUP3ToPush;
    public static Path pushBlock2ToPushBlock3UP;


    public specimenRENEWPATHS(Follower follower) {
        specimenRENEWPATHS.follower = follower;
    }

    public static PathChain paths() {
        preloadBeforePath = new Path(new BezierLine(new Point(startPose), new Point(preloadBefore)));
        preloadBeforePath.setConstantHeadingInterpolation(preloadBefore.getHeading());

        scorePreload = new Path(new BezierLine(new Point(preloadBefore), new Point(preload)));
        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());

        preloadBackPath = new Path(new BezierLine(new Point(preload), new Point(preloadToBackPos)));
        preloadBackPath.setConstantHeadingInterpolation(preload.getHeading());

        preloadToBlueLineUp = new Path(new BezierCurve(new Point(preloadToBackPos), new Point(20, 48.1), new Point(blueLineUp)));
        preloadToBlueLineUp.setConstantHeadingInterpolation(preloadToBackPos.getHeading());

        blueLineDirect = new Path(new BezierCurve(new Point(startPose), new Point(20, 42), new Point(blueLineUp)));
        blueLineDirect.setConstantHeadingInterpolation(startPose.getHeading());

        blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(60, 20.1), new Point(pushBlock1)));
        blueLineUpToPushBlock1.setConstantHeadingInterpolation(blueLineUp.getHeading());

        pushBlock1ToPushBlock2 = new Path(new BezierCurve(new Point(pushBlock1), new Point(85, 21.2), new Point(pushBlock2)));
        pushBlock1ToPushBlock2.setConstantHeadingInterpolation(pushBlock1.getHeading());

        pushBlock2ToPushBlock3UP = new Path(new BezierCurve(new Point(pushBlock2), new Point(60.5, 29.3), new Point(pushBlock3UP)));
        pushBlock2ToPushBlock3UP.setConstantHeadingInterpolation(pushBlock2.getHeading());

        blockUP3ToPush = new Path(new BezierCurve(new Point(pushBlock3UP), new Point(85, 18.4), new Point(pushBlock3)));
        blockUP3ToPush.setConstantHeadingInterpolation(pushBlock2.getHeading());

//        pushBlock3ToPickUp = new Path(new BezierCurve(new Point(pushBlock3), new Point(19.6, 33.2), new Point(pushBlock3Pick)));
//        pushBlock3ToPickUp.setConstantHeadingInterpolation(pushBlock3.getHeading());

        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3), new Point(4.8, 92.2), new Point(scoreBlockBefore1)));
        pushToScoreBefore1.setConstantHeadingInterpolation(pushBlock3.getHeading());

//        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3Pick), new Point(4.8, 92.2), new Point(scoreBlockBefore1)));
//        pushToScoreBefore1.setConstantHeadingInterpolation(pushBlock3.getHeading());

        scoreBefore1ToScore1 = new Path(new BezierLine(new Point(scoreBlockBefore1), new Point(scoreBlock1)));
        scoreBefore1ToScore1.setConstantHeadingInterpolation(scoreBlockBefore1.getHeading());

        score1ToPickUp = new Path(new BezierCurve(new Point(scoreBlock1), new Point(9.1, 67.5), new Point(pickUpAlt)));
        score1ToPickUp.setConstantHeadingInterpolation(scoreBlock1.getHeading());

        pickUpToScoreBefore2 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(8.9, 90.2), new Point(scoreBlockBefore2)));
        pickUpToScoreBefore2.setConstantHeadingInterpolation(pickUp.getHeading());

        scoreBefore2ToScore2 = new Path(new BezierLine(new Point(scoreBlockBefore2), new Point(scoreBlock2)));
        scoreBefore2ToScore2.setConstantHeadingInterpolation(scoreBlockBefore2.getHeading());

        score2ToPickUp = new Path(new BezierCurve(new Point(scoreBlock2), new Point(9.1, 67.5), new Point(pickUpAlt)));
        score2ToPickUp.setConstantHeadingInterpolation(scoreBlock2.getHeading());

        pickUpToScoreBefore3 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(8.9, 90.2), new Point(scoreBlockBefore3)));
        pickUpToScoreBefore3.setConstantHeadingInterpolation(pickUp.getHeading());

        scoreBefore3ToScore3 = new Path(new BezierLine(new Point(scoreBlockBefore3), new Point(scoreBlock3)));
        scoreBefore3ToScore3.setConstantHeadingInterpolation(scoreBlockBefore3.getHeading());

        score3ToPickUp = new Path(new BezierCurve(new Point(scoreBlock3), new Point(9.1, 67.5), new Point(pickUpAlt)));
        score3ToPickUp.setConstantHeadingInterpolation(scoreBlock3.getHeading());

        pickUpToScoreBefore4 = new Path(new BezierCurve(new Point(pickUpAlt), new Point(8.9, 90.2), new Point(scoreBlockBefore4)));
        pickUpToScoreBefore4.setConstantHeadingInterpolation(pickUp.getHeading());

        scoreBefore4ToScore4 = new Path(new BezierLine(new Point(scoreBlockBefore4), new Point(scoreBlock4)));
        scoreBefore4ToScore4.setConstantHeadingInterpolation(scoreBlockBefore4.getHeading());

        score4ToPickUp = new Path(new BezierCurve(new Point(scoreBlock4), new Point(14.9, 74.1), new Point(parkPickUp)));
        score4ToPickUp.setConstantHeadingInterpolation(scoreBlock4.getHeading());

        park = new Path(new BezierLine(new Point(pickUp), new Point(parkPos)));
        park.setConstantHeadingInterpolation(pickUp.getHeading());

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
                .addPath(park)
                .addPath(pushBlock2ToPushBlock3UP)
                .addPath(blockUP3ToPush)
                .build();
    }
}