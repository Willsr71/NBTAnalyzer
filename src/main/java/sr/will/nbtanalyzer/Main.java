package sr.will.nbtanalyzer;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: NBTAnalyzer <file>");
        }

        new NBTAnalyzer(args[0]);
    }
}
