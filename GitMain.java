package git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GitMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        SimpleGit simpleGit = new SimpleGit();

        if (args.length == 2) {
            String d;
            switch (args[0]) {
                case "commit":
                    simpleGit.commit(args[1]);
                    break;

                case "diff":
                    simpleGit.callDiff(args[1]);
                    break;

                case "version":
                    System.out.println("Usage : java GitMain version fileName versionNumber");
                    break;

                default:
                    System.out.println("Unknown command : " + args[0]);
                    printUsage();
            }
        }
        else if(args.length == 3)
        {
            if(args[0].equals("version"))
            {
                File newFile = simpleGit.getVersion(Integer.parseInt(args[1]),args[2]);
                BufferedReader reader = new BufferedReader( new FileReader(newFile));
                List<String> versionContent = reader.lines().collect(Collectors.toList());
                Iterator it = versionContent.iterator();
                while(it.hasNext())
                {
                    System.out.println(it.next());
                }
            }
        }
        else {
            System.out.println("Missing or unknown arguments");
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage : java GitMain commit|diff fileName\n" +
                                   "\t\tjava GitMain version versionNumber fileName");
    }
}
