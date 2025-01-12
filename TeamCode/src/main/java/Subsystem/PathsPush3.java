package Subsystem;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;

public class PathsPush3 {
    public static PathChain Push3Specimen;

    public static void initializePaths(Follower follower) {
        Push3Specimen paths = new Push3Specimen(follower);
        Push3Specimen = paths.paths();
    }
}