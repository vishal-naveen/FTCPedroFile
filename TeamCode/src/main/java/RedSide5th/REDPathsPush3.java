package RedSide5th;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;

public class REDPathsPush3 {
    private static PathChain pathChain;

    public static PathChain initializePaths(Follower follower) {
        REDPush3Specimen paths = new REDPush3Specimen(follower);
        pathChain = paths.paths();
        return pathChain;
    }
}