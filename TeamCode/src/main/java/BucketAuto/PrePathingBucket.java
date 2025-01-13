package BucketAuto;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;
import BucketAuto.BucketSidePaths;

public class PrePathingBucket {
    public static PathChain bucketSideAuto;
    public static PathChain fiveSpecimanAuto;

    public static void initializePaths(Follower follower) {
        // Initialize bucket side paths
        BucketSidePaths bucketPaths = new BucketSidePaths(follower);
        bucketSideAuto = bucketPaths.paths();

    }
}