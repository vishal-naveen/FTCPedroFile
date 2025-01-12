package Subsystem;

import com.pedropathing.pathgen.PathChain;
import com.pedropathing.follower.Follower;

public class PathsPush3Chain {
    public static PathChain pushChain3;

    public static void initializePaths(Follower follower) {
        pushChain3 paths = new pushChain3(follower);
        pushChain3 = paths.paths();
    }
}