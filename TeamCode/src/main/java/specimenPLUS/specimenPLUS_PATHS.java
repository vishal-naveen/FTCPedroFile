package specimenPLUS;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class specimenPLUS_PATHS {
    private static Follower follower;

    // Poses
    private static final Pose startPose = new Pose(8.4, 86.9, Math.toRadians(0));
    private static final Pose scoreBlock1 = new Pose(41.25, 77, Math.toRadians(0));
    private static final Pose scoreBlockBefore1 = new Pose(36, 77, Math.toRadians(0));
    private static final Pose scoreBlock2 = new Pose(41.25, 80.175, Math.toRadians(0));
    private static final Pose scoreBlockBefore2 = new Pose(37, 80.175, Math.toRadians(0));
    private static final Pose scoreBlock3 = new Pose(41.25, 80.075, Math.toRadians(0));
    private static final Pose scoreBlockBefore3 = new Pose(36, 80.075, Math.toRadians(0));
    private static final Pose scoreBlock4 = new Pose(41.25, 80, Math.toRadians(0));
    private static final Pose scoreBlockBefore4 = new Pose(36, 80, Math.toRadians(0));
    private static final Pose scoreBlock5 = new Pose(41, 79.95, Math.toRadians(0));
    private static final Pose scoreBlockBefore5 = new Pose(36, 79.95, Math.toRadians(0));

    // Updated poses for the new paths
    private static final Pose blueLineUp = new Pose(55, 31.4, Math.toRadians(0));
    private static final Pose pushBlock1 = new Pose(13, 25.6, Math.toRadians(0));
    private static final Pose pushBlock2 = new Pose(13, 18.75, Math.toRadians(0));
    private static final Pose pushBlock3 = new Pose(14, 11, Math.toRadians(-7));
    private static final Pose pushBlock2Up = new Pose(52, 18.75, Math.toRadians(0));
    private static final Pose pushBlock3Up = new Pose(52, 11, Math.toRadians(-7));
    private static final Pose pushBlock3Direct = new Pose(4.8, 13, Math.toRadians(0));
    private static final Pose pushBlock3Pick = new Pose(16.1, 35, Math.toRadians(0));
    //a
    private static final Pose pickUp = new Pose(10.1, 35, Math.toRadians(0));
    private static final Pose pickUpAlt = new Pose(10.15, 25.75, Math.toRadians(0));
    private static final Pose pickUpAlt2 = new Pose(9.8, 25.85, Math.toRadians(0));

    private static final Pose pickUpAlt3 = new Pose(9, 25.95, Math.toRadians(0));

    private static final Pose pickUpAlt4 = new Pose(7.9, 25.95, Math.toRadians(0));

    private static final Pose parkPickUp = new Pose(22.8, 42.5, Math.toRadians(50));
    private static final Pose parkPos = new Pose(13.5, 31.4, Math.toRadians(0));

    // Path declarations
    public static Path pushBlock3ToPickUp;
    public static Path blueLineUpToPushBlock1;
    public static Path pushBlock1ToPushBlock2;
    public static Path pushBlock2ToPushBlock3;
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
    public static Path pickUpToScoreBefore5;
    public static Path scoreBefore5ToScore5;
    public static Path score5ToPickUp;
    public static Path pushBlockToDirect;
    public static Path park;
    public static Path blueLineDirect;
    public static Path pushBlock1ToPushBlock2Up;
    public static Path pushBlock2UpToPushBlock2;
    public static Path pushBlock2ToPushBlock3Up;
    public static Path pushBlock3UpToPushBlock3;

    public specimenPLUS_PATHS(Follower follower) {
        specimenPLUS_PATHS.follower = follower;
    }

    public static PathChain paths() {
        // Keeping blueLineDirect the same as it was
        blueLineDirect = new Path(new BezierCurve(new Point(startPose),  new Point(5, 28.7), new Point(blueLineUp)));
        blueLineDirect.setLinearHeadingInterpolation(startPose.getHeading(), blueLineUp.getHeading());

        blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(55, 24.4), new Point(pushBlock1)));
        blueLineUpToPushBlock1.setLinearHeadingInterpolation(blueLineUp.getHeading(), pushBlock1.getHeading());
        blueLineUpToPushBlock1.setPathEndVelocityConstraint(1.0);

        // Add missing push block paths
        pushBlock1ToPushBlock2 = new Path(new BezierCurve(new Point(pushBlock1), new Point(100, 12), new Point(pushBlock2)));
        pushBlock1ToPushBlock2.setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2.getHeading());

        pushBlock2ToPushBlock3 = new Path(new BezierCurve(new Point(pushBlock2), new Point(100, 9.2), new Point(pushBlock3)));
        pushBlock2ToPushBlock3.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading());

        pushBlock3ToPickUp = new Path(new BezierCurve(new Point(pushBlock3), new Point(27.2, 24), new Point(pushBlock3Pick)));
        pushBlock3ToPickUp.setLinearHeadingInterpolation(pushBlock3.getHeading(), pushBlock3Pick.getHeading());

        pushBlock1ToPushBlock2Up = new Path(new BezierCurve(new Point(pushBlock1), new Point(52, 27.8), new Point(pushBlock2Up)));
        pushBlock1ToPushBlock2Up.setLinearHeadingInterpolation(pushBlock1.getHeading(), pushBlock2Up.getHeading());

        pushBlock2UpToPushBlock2 = new Path(new BezierLine(new Point(pushBlock2Up), new Point(pushBlock2)));
        pushBlock2UpToPushBlock2.setLinearHeadingInterpolation(pushBlock2Up.getHeading(), pushBlock2.getHeading());
        pushBlock2UpToPushBlock2.setPathEndVelocityConstraint(1.0);

        pushBlock2ToPushBlock3Up = new Path(new BezierCurve(new Point(pushBlock2), new Point(52, 17.4), new Point(pushBlock3Up)));
        pushBlock2ToPushBlock3Up.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3Up.getHeading());

        pushBlock3UpToPushBlock3 = new Path(new BezierLine(new Point(pushBlock3Up), new Point(pushBlock3)));
        pushBlock3UpToPushBlock3.setLinearHeadingInterpolation(pushBlock3Up.getHeading(), pushBlock3.getHeading());
        pushBlock3UpToPushBlock3.setPathEndVelocityConstraint(1.0);

        // Rest of the paths...
        pushBlockToDirect = new Path(new BezierCurve(new Point(pushBlock3), new Point(100, 9.2), new Point(pushBlock3Direct)));
        pushBlockToDirect.setLinearHeadingInterpolation(pushBlock2.getHeading(), pushBlock3.getHeading());

        pushToScoreBefore1 = new Path(new BezierCurve(new Point(pushBlock3), new Point(26.6, 33.1), new Point(7.5, 77), new Point(scoreBlockBefore1)));
        pushToScoreBefore1.setLinearHeadingInterpolation(pushBlock3.getHeading(), scoreBlockBefore1.getHeading());

        scoreBefore1ToScore1 = new Path(new BezierLine(new Point(scoreBlockBefore1), new Point(scoreBlock1)));
        scoreBefore1ToScore1.setLinearHeadingInterpolation(scoreBlockBefore1.getHeading(), scoreBlock1.getHeading());

        score1ToPickUp = new Path(new BezierCurve(new Point(scoreBlock1), new Point(17.9, 69.2), new Point(pickUpAlt)));
        score1ToPickUp.setLinearHeadingInterpolation(scoreBlock1.getHeading(), pickUp.getHeading());

        pickUpToScoreBefore2 = new Path(new BezierLine(new Point(pickUpAlt), new Point(scoreBlockBefore2)));
        pickUpToScoreBefore2.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore2.getHeading());

        scoreBefore2ToScore2 = new Path(new BezierLine(new Point(scoreBlockBefore2), new Point(scoreBlock2)));
        scoreBefore2ToScore2.setLinearHeadingInterpolation(scoreBlockBefore2.getHeading(), scoreBlock2.getHeading());

        score2ToPickUp = new Path(new BezierCurve(new Point(scoreBlock2), new Point(17.9, 69.2), new Point(pickUpAlt2)));
        score2ToPickUp.setLinearHeadingInterpolation(scoreBlock2.getHeading(), pickUp.getHeading());

        pickUpToScoreBefore3 = new Path(new BezierLine(new Point(pickUpAlt2), new Point(scoreBlockBefore3)));
        pickUpToScoreBefore3.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore3ToScore3 = new Path(new BezierLine(new Point(scoreBlockBefore3), new Point(scoreBlock3)));
        scoreBefore3ToScore3.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        score3ToPickUp = new Path(new BezierCurve(new Point(scoreBlock3), new Point(17.9, 69.2), new Point(pickUpAlt3)));
        score3ToPickUp.setLinearHeadingInterpolation(scoreBlock3.getHeading(), pickUp.getHeading());

        pickUpToScoreBefore4 = new Path(new BezierLine(new Point(pickUpAlt3), new Point(scoreBlockBefore4)));
        pickUpToScoreBefore4.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore4ToScore4 = new Path(new BezierLine(new Point(scoreBlockBefore4), new Point(scoreBlock4)));
        scoreBefore4ToScore4.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        score4ToPickUp = new Path(new BezierCurve(new Point(scoreBlock4), new Point(22.5, 64.9), new Point(pickUpAlt4)));
        score4ToPickUp.setLinearHeadingInterpolation(scoreBlock3.getHeading(), scoreBlock3.getHeading());

        pickUpToScoreBefore5 = new Path(new BezierLine(new Point(pickUpAlt4), new Point(scoreBlockBefore5)));
        pickUpToScoreBefore5.setLinearHeadingInterpolation(pickUp.getHeading(), scoreBlockBefore3.getHeading());

        scoreBefore5ToScore5 = new Path(new BezierCurve(new Point(scoreBlockBefore5), new Point(15.1, 71.1),new Point(scoreBlock5)));
        scoreBefore5ToScore5.setLinearHeadingInterpolation(scoreBlockBefore3.getHeading(), scoreBlock3.getHeading());

        score5ToPickUp = new Path(new BezierCurve(new Point(scoreBlock5), new Point(22.5, 64.9), new Point(parkPickUp)));
        score5ToPickUp.setLinearHeadingInterpolation(scoreBlock3.getHeading(), parkPickUp.getHeading());

        park = new Path(new BezierLine(new Point(pickUp), new Point(parkPos)));
        park.setLinearHeadingInterpolation(pickUp.getHeading(), parkPos.getHeading());

        return follower.pathBuilder()
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
                .addPath(pickUpToScoreBefore5)
                .addPath(scoreBefore5ToScore5)
                .addPath(score5ToPickUp)
                .addPath(pushBlock3ToPickUp)
                .addPath(park)
                .addPath(pushBlockToDirect)
                .addPath(blueLineDirect)
                .addPath(pushBlock1ToPushBlock2Up)
                .addPath(pushBlock2UpToPushBlock2)
                .addPath(pushBlock2ToPushBlock3Up)
                .addPath(pushBlock3UpToPushBlock3)
                .build();
    }
}