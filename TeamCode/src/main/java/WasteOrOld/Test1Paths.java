package WasteOrOld;// paths/WasteOrOld.Test1Paths.java

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.*;
import com.pedropathing.localization.Pose;

public class Test1Paths {
    private final static Pose startPose = new Pose(0.5, 73, Math.toRadians(0));
    private final static Pose preload = new Pose(21, 85, Math.toRadians(0));
    private final static Pose scoreBlock = new Pose(27, 85, Math.toRadians(0));
    private final static Pose blueLineUp = new Pose(58.5, 46, Math.toRadians(0));
    private final static Pose pushBlock1 = new Pose(10, 35, Math.toRadians(0));
    private final static Pose pushBlock2 = new Pose(10, 30, Math.toRadians(0));
    private final static Pose pushBlock3 = new Pose(10, 30, Math.toRadians(0));
    private final static Pose pickUp = new Pose(4, 42, Math.toRadians(0));

    public static Path getScorePreload(Follower follower) {
        Path scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
        scorePreload.setConstantHeadingInterpolation(startPose.getHeading());
        return scorePreload;
    }

    public static PathChain getLineFirstUp(Follower follower) {
        return follower.pathBuilder()
                .addPath(new BezierCurve(new Point(preload), new Point(1, 38), new Point(blueLineUp)))
                .setConstantHeadingInterpolation(preload.getHeading())
                .addPath(new BezierCurve(new Point(blueLineUp), new Point(71.6, 35), new Point(pushBlock1)))
                .setConstantHeadingInterpolation(blueLineUp.getHeading())
                .addPath(new BezierCurve(new Point(pushBlock1), new Point(71.6, 31), new Point(pushBlock2)))
                .setConstantHeadingInterpolation(pushBlock1.getHeading())
                .addPath(new BezierCurve(new Point(pushBlock2), new Point(71.6, 18), new Point(pushBlock3)))
                .setConstantHeadingInterpolation(pushBlock2.getHeading())
                .build();
    }

    public static Path getPickUpToScore(Follower follower) {
        Path pickUpToScore = new Path(new BezierLine(new Point(pickUp), new Point(scoreBlock)));
        pickUpToScore.setConstantHeadingInterpolation(pickUp.getHeading());
        return pickUpToScore;
    }

    public static Path getScoreToPickUp(Follower follower) {
        Path scoreToPickUp = new Path(new BezierLine(new Point(scoreBlock), new Point(pickUp)));
        scoreToPickUp.setConstantHeadingInterpolation(scoreBlock.getHeading());
        return scoreToPickUp;
    }
}