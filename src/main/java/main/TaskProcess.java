package main;

import entities.FileString;

import java.io.*;
import java.util.*;

import static util.Constants.*;

public class TaskProcess {
    private BufferedWriterWrap bufferedWriter;
    private List<FileString> arrayListA = new ArrayList<>();
    private List<FileString> arrayListB = new ArrayList<>();
    private List<FileString> linkedListA = new LinkedList<>();
    private List<FileString> linkedListB = new LinkedList<>();
    private Map<Integer, List<String>> hashMapA = new HashMap<>();
    private Map<Integer, List<String>> hashMapB = new HashMap<>();

    public TaskProcess(String inputfilename1, String inputfilename2, String outputfilename) {
        if ((bufferedWriter = new BufferedWriterWrap(outputfilename)) == null) {
            return;
        }
        bufferedWriter.write("Reading file...\n");
        if (!readFile(inputfilename1, true) || !readFile(inputfilename2, false)) {
            return;
        }
        bufferedWriter.write("Successfully read info:\nFile A:\n");
        for (FileString fs : arrayListA) {
            bufferedWriter.write(fs.toString());
        }
        bufferedWriter.write("\nFile B:\n");
        for (FileString fs : arrayListB) {
            bufferedWriter.write(fs.toString());
        }
        bufferedWriter.write("\nArrayList result:\n");
        processDataArrayList();
        linkedListA.sort(Comparator.comparingInt(FileString::getId));
        linkedListB.sort(Comparator.comparingInt(FileString::getId));
        bufferedWriter.write("\nLinkedList result:\n");
        processDataLinkedList();
        bufferedWriter.write("\nHashMap result:\n");
        processDataMap();
        bufferedWriter.close();
    }

    private void processDataArrayList() {
        for (int i = 0; i < arrayListA.size(); i++) {
            FileString fs = arrayListA.get(i);
            if (arrayListB.contains(fs) && arrayListA.indexOf(fs) == i) {
                for (int j = i; j < arrayListA.size(); j++) {
                    for (FileString aFileBAL : arrayListB) {
                        if (fs.equals(arrayListA.get(j)) && fs.equals(aFileBAL)) {
                            bufferedWriter.write(fs.getId() + " " + arrayListA.get(j).getValue() + " " + aFileBAL.getValue() + '\n');
                        }
                    }
                }
            }
        }
    }

    private void processDataLinkedList() {
        ListIterator<FileString> itA = linkedListA.listIterator();
        ListIterator<FileString> itB = linkedListB.listIterator();
        while (itA.hasNext()) {
            FileString fsA = itA.next();
            while (itB.hasNext()) {
                FileString fsB = itB.next();
                if (fsA.equals(fsB)) {
                    bufferedWriter.write(fsA.getId() + " " + fsA.getValue() + " " + fsB.getValue() + '\n');
                } else {
                    if (fsA.getId() < fsB.getId()) {
                        itB.previous();
                        if (fsA.equals(itA.next())) {
                            while (itB.hasPrevious() && itB.previous().equals(fsA)) {
//                                System.out.println();
                            }
                            if (itB.hasPrevious()) {
                                itB.next();
                            }
                        }
                        fsA = itA.previous();
                        itA.next();
                    }
                }
            }
        }
    }

    private void processDataMap() {
        for (Integer iA : hashMapA.keySet()) {
            if (hashMapB.containsKey(iA)) {
                for (String sA : hashMapA.get(iA)) {
                    for (String sB : hashMapB.get(iA)) {
                        bufferedWriter.write(iA + " " + sA + " " + sB + '\n');
                    }
                }
            }
        }
    }

    private boolean readFile(String filename, boolean isFirst) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader((filename)))) {
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                String[] stringParts = temp.split(",");
                Integer id;
                if (stringParts.length != STRING_PARTS_NUM || (id = parseId(stringParts[ID_PART])) == null) {
                    bufferedWriter.write("[ERROR] Failed to parse line: " + temp + "\n");
                    continue;
                }
                FileString newFileString = new FileString(id, stringParts[VALUE_PART]);
                if (isFirst) {
                    arrayListA.add(newFileString);
                    linkedListA.add(newFileString);
                    if (!hashMapA.containsKey(id)) {
                        hashMapA.put(id, new ArrayList<>());
                    }
                    hashMapA.get(id).add(stringParts[VALUE_PART]);
                } else {
                    arrayListB.add(newFileString);
                    linkedListB.add(newFileString);
                    if (!hashMapB.containsKey(id)) {
                        hashMapB.put(id, new ArrayList<>());
                    }
                    hashMapB.get(id).add(stringParts[VALUE_PART]);
                }
            }
            return true;
        } catch (IOException e) {
            bufferedWriter.write("[ERROR] IO error with input file operation.");
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
        private BufferedWriter bufferedWriter;
        public BufferedWriterWrap(String filename) {
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(filename));
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to open/create output file");
            }
        }

        public void write(String msg) {
            try {
                bufferedWriter.write(msg);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed writing to output file");
            }
        }

        public void close() {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed closing output file");
                }
            }
        }
    }
}
