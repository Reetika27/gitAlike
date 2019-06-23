package git;

import java.io.IOException;

public class GitMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        SimpleGit simpleGit = new SimpleGit();

       simpleGit.commit("src/hello1.txt");
        //simpleGit.callDiff("src/hello1.txt");
    }

}
