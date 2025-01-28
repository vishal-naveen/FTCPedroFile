package specimenAutoRenew;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;

public class specimenTweenRENEW {
    public static PathChain specimenPlus;

    public static void initializePaths(Follower follower) {
        specimenRENEWPATHS paths = new specimenRENEWPATHS(follower);
        specimenPlus = paths.paths();
    }
}