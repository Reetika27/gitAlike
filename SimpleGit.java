package git;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleGit {
    //Change gitdir to location where you have git directory
    String gitDir = "D:\\Temp\\git\\";
    File commandFile = new File(gitDir + "command.txt");
    private static int version = 0;

    public void commit(String fileName) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            File gitfile = new File(gitDir + file.getName());
            if (gitfile.exists()) {
                File latestVersionFile = getVersion(getLatestVersionNumber(), file.getName());
                getVersionInfo(file, latestVersionFile);
            } else {
                copyFile(file);
                FileWriter writer = new FileWriter(commandFile);
                writer.close();
            }
        }
        else {
            System.err.println("Specified file does not exist : " + fileName);
        }
    }

    public void callDiff(String fileName) throws IOException, InterruptedException {
        File file = new File(fileName);
        File gitfile = new File(gitDir + file.getName());
         if(gitfile.exists())
         {
             File latestVersionFile = getVersion(getLatestVersionNumber(), file.getName());
             diff(latestVersionFile,file);
         }
    }

    private void copyFile(File file) throws IOException {
        File newFile = new File(gitDir + file.getName());
        FileReader reader = new FileReader(file);
        FileWriter writer = new FileWriter(newFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        try {
            Stream<String> lines = bufferedReader.lines();
            lines.forEach(line -> {
                try {
                    System.out.println(line);
                    bufferedWriter.write(line + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        System.out.println("Copied file");
    }

    public void diff(File previous, File current) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
       Process p = runtime.exec("FC " + gitDir+ previous.getName() + " " + "D:/workspace/SampleGit/src/" +current.getName());
       p.waitFor();
       BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private void getVersionInfo(File file, File gitFile) throws IOException {
        List<String> prevVersion = null;
        List<String> currVersion = null;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        BufferedReader bufferedReader1 = new BufferedReader(new FileReader(gitFile));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(commandFile, true));

        currVersion = bufferedReader.lines().collect(Collectors.toList());
        prevVersion = bufferedReader1.lines().collect(Collectors.toList());

        Iterator<String> prevVersionIterator = prevVersion.iterator();
        Iterator<String> currVersionIterator = currVersion.iterator();
        Map<String, Integer> prevVersionMap = new HashMap<>();
        Map<String, Integer> currentVersionMap = new HashMap<>();
        int lineNoPrev = 0;
        int lineNoCurrent = 0;
        while (prevVersionIterator.hasNext()) {
            prevVersionMap.put(prevVersionIterator.next(), lineNoPrev);
            lineNoPrev += 1;
        }
        while (currVersionIterator.hasNext()) {
            currentVersionMap.put(currVersionIterator.next(), lineNoCurrent);
            if (prevVersionMap.containsKey(currVersion.get(lineNoCurrent)) && currentVersionMap.containsKey(prevVersion.get(lineNoCurrent))) {
                System.out.println("Already present");
            } else if (prevVersion.size() > currVersion.size()) {
                System.out.println("Deleted a line");
                bufferedWriter.write("Delete|" + prevVersion.get(lineNoCurrent) + "\r\n");

            } else if (prevVersion.size() < currVersion.size()) {
                System.out.println("Appended a line");
                bufferedWriter.write("Append|" + currVersion.get(lineNoCurrent) + "\r\n");
            }
            lineNoCurrent += 1;
        }
        if(prevVersion.size() > currVersion.size())
        {
            System.out.println("Deleted a line");
            bufferedWriter.write("Delete|" + prevVersion.get(lineNoCurrent) + "\r\n");
        }
        bufferedReader.close();
        bufferedReader1.close();
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public File getVersion(int versionNumber, String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(commandFile));
        String line = "";
        File versionFile = new File(gitDir + "versionFile.txt");
        File file = new File(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(versionFile));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(gitDir + file.getName()));
        List<String> v0FileContents = bufferedReader.lines().collect(Collectors.toList());
        for (int i = 0; i < versionNumber; i++) {
            line = reader.readLine();
            String contents[] = line.split("\\|");
            if (contents[0].equals("Append")) {
                v0FileContents.add(contents[1]);
            } else if (contents[0].equals("Delete")) {
                v0FileContents.remove(contents[1]);
            }
        }
        ListIterator<String> listIterator = v0FileContents.listIterator();
        while (listIterator.hasNext()) {
            bufferedWriter.write(listIterator.next() + "\r\n");
        }
        reader.close();
        bufferedReader.close();
        bufferedWriter.flush();
        bufferedWriter.close();

        return versionFile;
    }

    private int getLatestVersionNumber() throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(commandFile));
            List<String> commandFileContent = reader.lines().collect(Collectors.toList());
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commandFileContent.size();
        }
        catch (IOException ex) {
            return 0;
        }
    }
}
