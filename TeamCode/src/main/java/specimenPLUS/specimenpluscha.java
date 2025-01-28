package specimenPLUS;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;


import specimenPLUS.specimenPLUS_PATHS;

public class specimenpluscha {
    public static PathChain specimenPlus;

    public static void initializePaths(Follower follower) {
        specimenPLUS_PATHS paths = new specimenPLUS_PATHS(follower);
        specimenPlus = paths.paths();
    }
}