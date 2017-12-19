package main;

import entities.FileString;

import java.io.*;
import java.util.*;

import static util.Constants.*;

public class TaskProcess {
    private BufferedWriterWrap bw;
    private List<FileString> fileAAL = new ArrayList<>();
    private List<FileString> fileBAL = new ArrayList<>();
    private List<FileString> fileALL = new LinkedList<>();
    private List<FileString> fileBLL = new LinkedList<>();
    private Map<Integer, List<String>> fileAMap = new HashMap<>();
    private Map<Integer, List<String>> fileBMap = new HashMap<>();

    public TaskProcess(String inputfilename1, String inputfilename2, String outputfilename) {
        if ((bw = new BufferedWriterWrap(outputfilename)) == null) {
            return;
        }
        bw.write("Reading file...\n");
        if (!readFile(inputfilename1, true) || !readFile(inputfilename2, false)) {
            return;
        }
        bw.write("Successfully read info:\nFile A:\n");
        for (FileString fs : fileAAL) {
            bw.write(fs.toString());
        }
        bw.write("\nFile B:\n");
        for (FileString fs : fileBAL) {
            bw.write(fs.toString());
        }
        bw.write("\nArrayList result:\n");
        processDataArrayList();
        fileALL.sort(Comparator.comparingInt(FileString::getId));
        fileBLL.sort(Comparator.comparingInt(FileString::getId));
        bw.write("\nLinkedList result:\n");
        processDataLinkedList();
        bw.write("\nHashMap result:\n");
        processDataMap();
        bw.close();
    }

    private void processDataArrayList() {
        for (int i = 0; i < fileAAL.size(); i++) {
            FileString fs = fileAAL.get(i);
            if (fileBAL.contains(fs) && fileAAL.indexOf(fs) == i) {
                for (int j = i; j < fileAAL.size(); j++) {
                    for (FileString aFileBAL : fileBAL) {
                        if (fs.equals(fileAAL.get(j)) && fs.equals(aFileBAL)) {
                            bw.write(fs.getId() + " " + fileAAL.get(j).getValue() + " " + aFileBAL.getValue() + '\n');
                        }
                    }
                }
            }
        }
    }

    private void processDataLinkedList() {
        for (FileString fsA : fileALL) {
            for (FileString fsB : fileBLL) {
                if (fsA.equals(fsB)) {
                    bw.write(fsA.getId() + " " + fsA.getValue() + " " + fsB.getValue() + '\n');
                } else if (fsB.getId() > fsA.getId()) {
                    break;
                }
            }
        }
    }

    private void processDataMap() {
        for (Integer iA : fileAMap.keySet()) {
            if (fileBMap.containsKey(iA)) {
                for (String sA : fileAMap.get(iA)) {
                    for (String sB : fileBMap.get(iA)) {
                        bw.write(iA + " " + sA + " " + sB + '\n');
                    }
                }
            }
        }
    }

    private boolean readFile(String filename, boolean isFirst) {
        try (BufferedReader r = new BufferedReader(new FileReader((filename)))) {
            String temp;
            while ((temp = r.readLine()) != null) {
                String[] stringParts = temp.split(",");
                Integer id = parseId(stringParts[ID_PART]);
                if (stringParts.length != STRING_PARTS_NUM || id == null) {
                    bw.write("[ERROR] Failed to parse line: " + temp + "\n");
                    continue;
                }

                FileString fs = new FileString(id, stringParts[VALUE_PART]);
                if (isFirst) {
                    fileAAL.add(fs);
                    fileALL.add(fs);
                    if (!fileAMap.containsKey(id)) {
                        fileAMap.put(id, new ArrayList<>());
                    }
                    fileAMap.get(id).add(stringParts[VALUE_PART]);
                } else {
                    fileBAL.add(fs);
                    fileBLL.add(fs);
                    if (!fileBMap.containsKey(id)) {
                        fileBMap.put(id, new ArrayList<>());
                    }
                    fileBMap.get(id).add(stringParts[VALUE_PART]);
                }
            }
            return true;
        } catch (IOException e) {
            bw.write("[ERROR] IO error with input file operation.");
            return false;
        }
    }

    private Integer parseId(String str) {
        Integer res = null;
        try {
            res = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // nothing to do here
        }
        return res;
    }

    class BufferedWriterWrap {
        private BufferedWriter bw;
        public BufferedWriterWrap(String filename) {
            try {
                bw = new BufferedWriter(new FileWriter(filename));
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to open/create output file");
            }
        }

        public void write(String msg) {
            try {
                bw.write(msg);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed writing to output file");
            }
        }

        public void close() {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed closing output file");
                }
            }
        }
    }
}
