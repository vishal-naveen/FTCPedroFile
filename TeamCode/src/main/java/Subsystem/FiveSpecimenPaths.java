package Subsystem;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class FiveSpecimenPaths {
        private static Follower follower;

        // Poses
        private static final Pose startPose = new Pose(9.4, 62.8, Math.toRadians(0));
        private static final Pose preload = new Pose(36.5, 72.96, Math.toRadians(0));
        private static final Pose scoreBlock = new Pose(36.5, 72.96, Math.toRadians(0));
        private static final Pose blueLineUp = new Pose(54.9, 34.56, Math.toRadians(0));
        private static final Pose pushBlock1 = new Pose(10.5, 26.7, Math.toRadians(0));
        private static final Pose pushBlock2 = new Pose(13.9, 18, Math.toRadians(0));
        private static final Pose pushBlock2Top = new Pose(72.2, 18.8, Math.toRadians(0));
        private static final Pose pushBlock3 = new Pose(14.3, 17, Math.toRadians(0));
        private static final Pose pushBlock3Top = new Pose(69.12, 17, Math.toRadians(0));
        private static final Pose pickUp = new Pose(10.5, 26.7, Math.toRadians(0));

        // Paths and PathChains
        public static Path scorePreload;
        public static PathChain lineFirstUp;
        public static Path pickUpToScore1;
        public static Path scoreToPickUp;
        public static Path pickUpToScore2;
        public static Path scoreToBlock2Top;
        public static Path block2TopToPickUp;
        public static Path pickUpToScore3;
        public static Path scoreToBlock3Top;
        public static Path block3TopToPickUp;
        public static Path pickUpToScore4;
        public static Path park;
        public static Path lineFirstUpToBlueLineUp;
        public static Path blueLineUpToPushBlock1;

        public FiveSpecimenPaths(Follower follower) {
                FiveSpecimenPaths.follower = follower;
        }

        public static PathChain paths() {
                // Preload path
                scorePreload = new Path(new BezierLine(new Point(startPose), new Point(preload)));
                scorePreload.setConstantHeadingInterpolation(startPose.getHeading());

                lineFirstUp = follower.pathBuilder()
                        .addPath(new BezierCurve(new Point(preload), new Point(17.8, 34.8), new Point(blueLineUp)))
                        .setConstantHeadingInterpolation(preload.getHeading())
                        .addPath(new BezierCurve(new Point(blueLineUp), new Point(83.3, 24.8), new Point(pushBlock1)))
                        .setConstantHeadingInterpolation(blueLineUp.getHeading())
                        .build();

                pickUpToScore1 = new Path(new BezierLine(new Point(pushBlock1), new Point(scoreBlock)));
                pickUpToScore1.setConstantHeadingInterpolation(pickUp.getHeading());

                scoreToPickUp = new Path(new BezierLine(new Point(scoreBlock), new Point(pickUp)));
                scoreToPickUp.setConstantHeadingInterpolation(scoreBlock.getHeading());

                pickUpToScore2 = new Path(new BezierLine(new Point(pickUp), new Point(scoreBlock)));
                pickUpToScore2.setConstantHeadingInterpolation(pickUp.getHeading());

                scoreToBlock2Top = new Path(new BezierCurve(new Point(scoreBlock),new Point(15.4,35),new Point(pushBlock2Top)));
                scoreToBlock2Top.setConstantHeadingInterpolation(pickUp.getHeading());

                block2TopToPickUp = new Path(new BezierLine(new Point(pushBlock2Top), new Point(pushBlock2)));
                block2TopToPickUp.setConstantHeadingInterpolation(pushBlock2Top.getHeading());

                pickUpToScore3 = new Path(new BezierLine(new Point(pushBlock2), new Point(scoreBlock)));
                pickUpToScore3.setConstantHeadingInterpolation(pushBlock2.getHeading());

                scoreToBlock3Top = new Path(new BezierCurve(new Point(scoreBlock),new Point(13.3, 28), new Point(pushBlock3Top)));
                scoreToBlock3Top.setConstantHeadingInterpolation(scoreBlock.getHeading());

                block3TopToPickUp = new Path(new BezierLine(new Point(pushBlock3Top), new Point(pushBlock3)));
                block3TopToPickUp.setConstantHeadingInterpolation(pushBlock3Top.getHeading());

                pickUpToScore4 = new Path(new BezierLine(new Point(pushBlock3), new Point(scoreBlock)));
                pickUpToScore4.setConstantHeadingInterpolation(pushBlock2.getHeading());

                park = new Path(new BezierLine(new Point(scoreBlock), new Point(pickUp)));
                park.setConstantHeadingInterpolation(pushBlock2.getHeading());

                lineFirstUpToBlueLineUp = new Path(new BezierCurve(new Point(preload), new Point(17.8, 34.8), new Point(blueLineUp)));
                lineFirstUpToBlueLineUp.setConstantHeadingInterpolation(preload.getHeading());

                blueLineUpToPushBlock1 = new Path(new BezierCurve(new Point(blueLineUp), new Point(83.3, 24.8), new Point(pushBlock1)));
                blueLineUpToPushBlock1.setConstantHeadingInterpolation(blueLineUp.getHeading());

                // Return as a PathChain
                return follower.pathBuilder()
                        .addPath(scorePreload)
                        .addPath(lineFirstUpToBlueLineUp)
                        .addPath(blueLineUpToPushBlock1)
                        .addPath(pickUpToScore1)
                        .addPath(scoreToPickUp)
                        .addPath(pickUpToScore2)
                        .addPath(scoreToBlock2Top)
                        .addPath(block2TopToPickUp)
                        .addPath(pickUpToScore3)
                        .addPath(scoreToBlock3Top)
                        .addPath(block3TopToPickUp)
                        .addPath(pickUpToScore4)
                        .addPath(park)
                        .build();
        }
}