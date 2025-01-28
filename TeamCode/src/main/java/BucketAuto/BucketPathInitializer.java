package BucketAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;

public class BucketPathInitializer {
    public static PathChain BucketSidePaths;

    public static void initializePaths(Follower follower) {
        BucketSidePaths paths = new BucketSidePaths(follower);
        BucketSidePaths = paths.paths();
    }
}