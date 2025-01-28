package specimenPLUS;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;

public class specimenpluscha {
    public static PathChain specimenPlus;

    public static void initializePaths(Follower follower) {
        specimenPLUS_PATHS paths = new specimenPLUS_PATHS(follower);
        specimenPlus = paths.paths();  // This needs to match your specimenPLUS_PATHS method name
    }
}