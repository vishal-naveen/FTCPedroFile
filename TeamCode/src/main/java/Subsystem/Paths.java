package Subsystem;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;

public class Paths {
    public static PathChain fiveSpecimanAuto;

    public static void initializePaths(Follower follower) {
        FiveSpecimenPaths paths = new FiveSpecimenPaths(follower);
        fiveSpecimanAuto = paths.paths();
    }
}