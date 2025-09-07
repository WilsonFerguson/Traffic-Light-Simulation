package library.core;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.*;
import java.awt.Color;

public class PFunctions implements PConstants {
    public void println(Object... args) {
        Helper.println(args);
    }

    public void print(Object... args) {
        Helper.print(args);
    }

    public void printArray(Object[] array) {
        Helper.printArray(array);
    }

    public void printArray(String[] array) {
        Helper.printArray(array);
    }

    public void printArray(int[] array) {
        Helper.printArray(array);
    }

    public void printArray(float[] array) {
        Helper.printArray(array);
    }

    public void printArray(long[] array) {
        Helper.printArray(array);
    }

    public void printArray(double[] array) {
        Helper.printArray(array);
    }

    public void printArray(char[] array) {
        Helper.printArray(array);
    }

    public String input(String message) {
        return Helper.input(message);
    }

    public int inputInt(String message) {
        return Helper.inputInt(message);
    }

    public int inputInt(String message, boolean repeat) {
        return Helper.inputInt(message, repeat);
    }

    public boolean inputYesNo(String message) {
        return Helper.inputYesNo(message);
    }

    public boolean inputYesNo(String message, boolean repeat) {
        return Helper.inputYesNo(message, repeat);
    }

    public boolean inputBoolean(String message) {
        return Helper.inputBoolean(message);
    }

    public boolean inputBoolean(String message, boolean repeat) {
        return Helper.inputBoolean(message, repeat);
    }

    /**
     * Prints a given message + " (option 1, option 2, etc.): " and returns whatever
     * option the user enters. Throws an error if user types anything else.
     * 
     * @param message
     * @param options which options the user can choose from (any number of options)
     * 
     * @return String the option the user chose
     */
    public static String inputOption(String message, String... options) {
        return Helper.inputOption(message, options);
    }

    /**
     * Prints a given message + " (option 1, option 2, etc.): " and returns whatever
     * option the user enters. If set to repeat, it will continuously ask the user
     * for an integer until they give one.
     * 
     * @param message
     * @param options which options the user can choose from (any number of options)
     * 
     * @return String the option the user chose
     */
    public static String inputOption(String message, boolean repeat, String... options) {
        return Helper.inputOption(message, repeat, options);
    }

    public String[] loadStrings(String fileName) {
        return Helper.loadStrings(fileName);
    }

    /**
     * Opens a file with the default program. This can either be a directory (in
     * which case it will open the file explorer) or a file (in which case it will
     * open the default program for that file type
     * 
     * @param fileName
     */

    public void openInBrowser(String url) {
        Helper.openInBrowser(url);
    }

    public boolean isValidURL(String url) {
        return Helper.isValidURL(url);
    }

    public String getClipboardContents() {
        return Helper.getClipboardContents();
    }

    public void copyToClipboard(String text) {
        Helper.copyToClipboard(text);
    }

    public LinkedHashMap<String, String> loadProperties(String filename) {
        String[] lines = loadStrings(filename);
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.length() == 0 || line.startsWith("#") || !line.contains("="))
                continue;

            String[] split = line.split("=");
            int lastHash = split[1].lastIndexOf("#");
            if (lastHash != -1) {
                split[1] = split[1].substring(0, lastHash);
            }
            split[0] = split[0].trim();
            split[1] = split[1].trim();

            properties.put(split[0], split[1]);
        }

        return properties;
    }

    public void saveProperties(LinkedHashMap<String, String> properties, String filename) {
        ArrayList<String> lines = new ArrayList<>();
        for (String key : properties.keySet()) {
            lines.add(key + "=" + properties.get(key));
        }

        saveStrings(lines, filename);
    }

    public void append(byte[] array, byte value) {
        byte[] newArray = new byte[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(char[] array, char value) {
        char[] newArray = new char[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    // Array functions
    public void append(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(float[] array, float value) {
        float[] newArray = new float[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(String[] array, String value) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void append(Object[] array, Object value) {
        Object[] newArray = new Object[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[array.length] = value;
        array = newArray;
    }

    public void arrayCopy(Object src, int srcPos, Object dest, int destPos,
            int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public void arrayCopy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
    }

    public void arrayCopy(Object src, Object dest) {
        System.arraycopy(src, 0, dest, 0, Array.getLength(src));
    }

    public boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] c = new boolean[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public char[] concat(char[] a, char[] b) {
        char[] c = new char[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public int[] concat(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public float[] concat(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public Object[] concat(Object[] a, Object[] b) {
        Object[] c = new Object[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public boolean[] expand(boolean[] list) {
        boolean temp[] = new boolean[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public boolean[] expand(boolean[] list, int newSize) {
        boolean temp[] = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public byte[] expand(byte[] list) {
        byte temp[] = new byte[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public byte[] expand(byte[] list, int newSize) {
        byte temp[] = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public char[] expand(char[] list) {
        char temp[] = new char[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public char[] expand(char[] list, int newSize) {
        char temp[] = new char[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public int[] expand(int[] list) {
        int temp[] = new int[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public int[] expand(int[] list, int newSize) {
        int temp[] = new int[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public long[] expand(long[] list) {
        long temp[] = new long[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public long[] expand(long[] list, int newSize) {
        long temp[] = new long[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public float[] expand(float[] list) {
        float temp[] = new float[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public float[] expand(float[] list, int newSize) {
        float temp[] = new float[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public double[] expand(double[] list) {
        double temp[] = new double[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public double[] expand(double[] list, int newSize) {
        double temp[] = new double[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public String[] expand(String[] list) {
        String temp[] = new String[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public String[] expand(String[] list, int newSize) {
        String temp[] = new String[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public Object[] expand(Object[] list) {
        Object temp[] = new Object[list.length + 1];
        System.arraycopy(list, 0, temp, 0, list.length);
        return temp;
    }

    public Object[] expand(Object[] list, int newSize) {
        Object temp[] = new Object[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(list.length, newSize));
        return temp;
    }

    public boolean[] shorten(boolean[] list) {
        boolean outgoing[] = new boolean[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public byte[] shorten(byte[] list) {
        byte outgoing[] = new byte[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public char[] shorten(char[] list) {
        char outgoing[] = new char[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public int[] shorten(int[] list) {
        int outgoing[] = new int[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public long[] shorten(long[] list) {
        long outgoing[] = new long[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public float[] shorten(float[] list) {
        float outgoing[] = new float[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public double[] shorten(double[] list) {
        double outgoing[] = new double[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public String[] shorten(String[] list) {
        String outgoing[] = new String[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public Object[] shorten(Object[] list) {
        Object outgoing[] = new Object[list.length - 1];
        System.arraycopy(list, 0, outgoing, 0, list.length - 1);
        return outgoing;
    }

    public boolean[] splice(boolean[] list, boolean value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public byte[] splice(byte[] list, byte value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public char[] splice(char[] list, char value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public int[] splice(int[] list, int value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public long[] splice(long[] list, long value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public float[] splice(float[] list, float value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public double[] splice(double[] list, double value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public String[] splice(String[] list, String value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public Object[] splice(Object[] list, Object value, int index) {
        list = expand(list);
        System.arraycopy(list, index, list, index + 1, list.length - index - 1);
        list[index] = value;
        return list;
    }

    public boolean[] subset(boolean[] list, int start) {
        return subset(list, start, list.length);
    }

    public byte[] subset(byte[] list, int start) {
        return subset(list, start, list.length);
    }

    public char[] subset(char[] list, int start) {
        return subset(list, start, list.length);
    }

    public int[] subset(int[] list, int start) {
        return subset(list, start, list.length);
    }

    public long[] subset(long[] list, int start) {
        return subset(list, start, list.length);
    }

    public float[] subset(float[] list, int start) {
        return subset(list, start, list.length);
    }

    public double[] subset(double[] list, int start) {
        return subset(list, start, list.length);
    }

    public String[] subset(String[] list, int start) {
        return subset(list, start, list.length);
    }

    public Object[] subset(Object[] list, int start) {
        return subset(list, start, list.length);
    }

    public boolean[] subset(boolean[] list, int start, int count) {
        boolean outgoing[] = new boolean[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public byte[] subset(byte[] list, int start, int count) {
        byte outgoing[] = new byte[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public char[] subset(char[] list, int start, int count) {
        char outgoing[] = new char[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public int[] subset(int[] list, int start, int count) {
        int outgoing[] = new int[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public long[] subset(long[] list, int start, int count) {
        long outgoing[] = new long[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public float[] subset(float[] list, int start, int count) {
        float outgoing[] = new float[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public double[] subset(double[] list, int start, int count) {
        double outgoing[] = new double[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public String[] subset(String[] list, int start, int count) {
        String outgoing[] = new String[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public Object[] subset(Object[] list, int start, int count) {
        Object outgoing[] = new Object[count];
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public boolean[] reverse(boolean[] list) {
        boolean outgoing[] = new boolean[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public byte[] reverse(byte[] list) {
        byte outgoing[] = new byte[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public char[] reverse(char[] list) {
        char outgoing[] = new char[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public int[] reverse(int[] list) {
        int outgoing[] = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public long[] reverse(long[] list) {
        long outgoing[] = new long[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public float[] reverse(float[] list) {
        float outgoing[] = new float[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public double[] reverse(double[] list) {
        double outgoing[] = new double[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public String[] reverse(String[] list) {
        String outgoing[] = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public Object[] reverse(Object[] list) {
        Object outgoing[] = new Object[list.length];
        for (int i = 0; i < list.length; i++) {
            outgoing[i] = list[list.length - i - 1];
        }
        return outgoing;
    }

    public String reverse(String what) {
        StringBuilder sb = new StringBuilder(what);
        return sb.reverse().toString();
    }

    public byte[] sort(byte[] list) {
        byte outgoing[] = new byte[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public char[] sort(char[] list) {
        char outgoing[] = new char[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public int[] sort(int[] list) {
        int outgoing[] = new int[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public long[] sort(long[] list) {
        long outgoing[] = new long[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public float[] sort(float[] list) {
        float outgoing[] = new float[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public double[] sort(double[] list) {
        double outgoing[] = new double[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public String[] sort(String[] list) {
        String outgoing[] = new String[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public Object[] sort(Object[] list) {
        Object outgoing[] = new Object[list.length];
        System.arraycopy(list, 0, outgoing, 0, list.length);
        Arrays.sort(outgoing);
        return outgoing;
    }

    public int indexOf(byte[] list, byte item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(char[] list, char item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(int[] list, int item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(long[] list, long item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(float[] list, float item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(double[] list, double item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(Object[] list, Object item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    // Conversion Functions
    public String binary(int n) {
        return Integer.toBinaryString(n);
    }

    public String binary(byte n) {
        return Integer.toBinaryString(n);
    }

    public String binary(char n) {
        return Integer.toBinaryString(n);
    }

    public String binary(color c) {
        return Integer.toBinaryString(c.getRGB());
    }

    public boolean bool(int n) {
        return n != 0;
    }

    public boolean bool(String s) {
        return s.equals("true");
    }

    public byte parseByte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public byte parseByte(int n) {
        return (byte) n;
    }

    public byte parseByte(char n) {
        return (byte) n;
    }

    public byte parseByte(double n) {
        return (byte) n;
    }

    public byte parseByte(String s) {
        return Byte.parseByte(s);
    }

    public byte parseByte(Object o) {
        return Byte.parseByte(o.toString());
    }

    public char parseChar(int n) {
        return (char) n;
    }

    public char parseChar(byte n) {
        return (char) n;
    }

    public char parseChar(double n) {
        return (char) n;
    }

    public char parseChar(String s) {
        return s.charAt(0);
    }

    public char parseChar(Object o) {
        return o.toString().charAt(0);
    }

    public float parseFloat(boolean b) {
        return b ? 1 : 0;
    }

    public float parseFloat(byte n) {
        return n;
    }

    public float parseFloat(char n) {
        return n;
    }

    public float parseFloat(int n) {
        return n;
    }

    public float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public float parseFloat(Object o) {
        return Float.parseFloat(o.toString());
    }

    public double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    public double parseDouble(Object o) {
        return Double.parseDouble(o.toString());
    }

    public double parseDouble(boolean b) {
        return b ? 1 : 0;
    }

    public double parseDouble(byte n) {
        return n;
    }

    public double parseDouble(char n) {
        return n;
    }

    public double parseDouble(int n) {
        return n;
    }

    public String hex(byte n) {
        return Integer.toHexString(n);
    }

    public String hex(char n) {
        return Integer.toHexString(n);
    }

    public String hex(int n) {
        return Integer.toHexString(n);
    }

    public String hex(color c) {
        return Integer.toHexString(c.getRGB());
    }

    public int parseInt(boolean b) {
        return b ? 1 : 0;
    }

    public int parseInt(byte n) {
        return n;
    }

    public int parseInt(char n) {
        return n;
    }

    public int parseInt(double n) {
        return (int) n;
    }

    public int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public int parseInt(Object o) {
        return Integer.parseInt(o.toString());
    }

    public String str(boolean b) {
        return Boolean.toString(b);
    }

    public String str(byte n) {
        return Byte.toString(n);
    }

    public String str(char n) {
        return Character.toString(n);
    }

    public String str(int n) {
        return Integer.toString(n);
    }

    public String str(double n) {
        return Float.toString((float) n);
    }

    public String str(color c) {
        return c.toString();
    }

    public String str(Object o) {
        return o.toString();
    }

    public int unbinary(String s) {
        return Integer.parseInt(s, 2);
    }

    public int unhex(String s) {
        return Integer.parseInt(s, 16);
    }

    // String Functions
    public String join(String[] list, String separator) {
        String result = "";
        for (int i = 0; i < list.length; i++) {
            result += list[i];
            if (i < list.length - 1) {
                result += separator;
            }
        }
        return result;
    }

    // match all/match

    // nf
    public String nf(int num) {
        return str(num);
    }

    public String nf(double num) {
        return str(num);
    }

    public String[] nf(int[] nums) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = str(nums[i]);
        }
        return outgoing;
    }

    public String[] nf(float[] nums) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = str(nums[i]);
        }
        return outgoing;
    }

    public String nf(int num, int digits) {
        return String.format("%0" + digits + "d", num);
    }

    public String nf(double num, int digits) {
        return String.format("%0" + digits + "f", num);
    }

    public String[] nf(int[] nums, int digits) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = String.format("%0" + digits + "d", nums[i]);
        }
        return outgoing;
    }

    public String[] nf(float[] nums, int digits) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            outgoing[i] = String.format("%0" + digits + "f", nums[i]);
        }
        return outgoing;
    }

    public String nf(int num, int left, int right) {
        String first = left > 0 ? String.format("%0" + left + "d", num) : str(num);
        String second = String.format("%0" + right + "d", (int) (num % 1 * Math.pow(10, right)));
        return first + "." + second;
    }

    public String nf(double num, int left, int right) {
        String first = left > 0 ? String.format("%0" + left + "d", (int) num) : str((int) num);
        String second = String.format("%0" + right + "d", (int) (num % 1 * Math.pow(10, right)));
        return first + "." + second;
    }

    public String[] nf(int[] nums, int left, int right) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            String first = left > 0 ? String.format("%0" + left + "d", nums[i]) : str(nums[i]);
            String second = String.format("%0" + right + "d", (int) (nums[i] % 1 * Math.pow(10, right)));
            outgoing[i] = first + "." + second;
        }
        return outgoing;
    }

    public String[] nf(float[] nums, int left, int right) {
        String[] outgoing = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            String first = left > 0 ? String.format("%0" + left + "d", (int) nums[i]) : str((int) nums[i]);
            String second = String.format("%0" + right + "d", (int) (nums[i] % 1 * Math.pow(10, right)));
            outgoing[i] = first + "." + second;
        }
        return outgoing;
    }

    // nfc
    public String nfc(int num) {
        String output = str(num);

        int length = output.length() - 1;
        if (output.charAt(0) == '-') {
            length--;
        }
        int commas = length / 3;
        for (int i = 0; i < commas; i++) {
            output = output.substring(0, output.length() - (i + 1) * 3 - i) + ","
                    + output.substring(output.length() - (i + 1) * 3 - i);
        }
        return output;
    }

    public String nfc(double num) {
        String first = nfc((int) num);
        String second = str(num).substring(str(num).indexOf("."));
        return first + second;
    }

    public String[] nfc(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i]);
        }
        return output;
    }

    public String[] nfc(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i]);
        }
        return output;
    }

    public String nfc(double num, int digits) {
        String first = nfc((int) num);
        String second = nf(num, 0, digits);
        second = second.substring(second.indexOf("."));
        return first + second;
    }

    public String[] nfc(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i], digits);
        }
        return output;
    }

    public String[] nfc(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfc(num[i], digits);
        }
        return output;
    }

    // nfp
    public String nfp(int num) {
        return num > 0 ? "+" + nf(num) : nf(num);
    }

    public String nfp(double num) {
        return num > 0 ? "+" + nf(num) : nf(num);
    }

    public String[] nfp(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i]);
        }
        return output;
    }

    public String[] nfp(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i]);
        }
        return output;
    }

    public String nfp(int num, int digits) {
        return num > 0 ? "+" + nf(num, digits) : nf(num, digits);
    }

    public String nfp(double num, int digits) {
        return num > 0 ? "+" + nf(num, digits) : nf(num, digits);
    }

    public String[] nfp(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], digits);
        }
        return output;
    }

    public String[] nfp(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], digits);
        }
        return output;
    }

    public String nfp(int num, int left, int right) {
        return num > 0 ? "+" + nf(num, left, right) : nf(num, left, right);
    }

    public String nfp(double num, int left, int right) {
        return num > 0 ? "+" + nf(num, left, right) : nf(num, left, right);
    }

    public String[] nfp(int[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], left, right);
        }
        return output;
    }

    public String[] nfp(float[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfp(num[i], left, right);
        }
        return output;
    }

    // nfs
    public String nfs(int num) {
        return num > 0 ? " " + nf(num) : nf(num);
    }

    public String nfs(double num) {
        return num > 0 ? " " + nf(num) : nf(num);
    }

    public String[] nfs(int[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i]);
        }
        return output;
    }

    public String[] nfs(float[] num) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i]);
        }
        return output;
    }

    public String nfs(int num, int digits) {
        return num > 0 ? " " + nf(num, digits) : nf(num, digits);
    }

    public String nfs(double num, int digits) {
        return num > 0 ? " " + nf(num, digits) : nf(num, digits);
    }

    public String[] nfs(int[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], digits);
        }
        return output;
    }

    public String[] nfs(float[] num, int digits) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], digits);
        }
        return output;
    }

    public String nfs(int num, int left, int right) {
        return num > 0 ? " " + nf(num, left, right) : nf(num, left, right);
    }

    public String nfs(double num, int left, int right) {
        return num > 0 ? " " + nf(num, left, right) : nf(num, left, right);
    }

    public String[] nfs(int[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], left, right);
        }
        return output;
    }

    public String[] nfs(float[] num, int left, int right) {
        String[] output = new String[num.length];
        for (int i = 0; i < num.length; i++) {
            output[i] = nfs(num[i], left, right);
        }
        return output;
    }

    public String[] split(String str, char separator) {
        return str.split(Character.toString(separator));
    }

    public String[] split(String str, String separator) {
        return str.split(separator);
    }

    public String trim(String str) {
        return str.trim();
    }

    // Time and Date Functions
    public int day() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public int hour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int minute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int month() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public int second() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public int year() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    // Math Functions
    public int abs(int n) {
        return Math.abs(n);
    }

    public float abs(double n) {
        return MathHelper.abs(n);
    }

    public int ceil(double n) {
        return (int) Math.ceil(n);
    }

    public int floor(double n) {
        return (int) Math.floor(n);
    }

    public float constrain(double value, double min, double max) {
        return MathHelper.constrain(value, min, max);
    }

    public int constrain(int value, int min, int max) {
        return MathHelper.constrain(value, min, max);
    }

    public float dist(double x1, double y1, double x2, double y2) {
        return PVector.dist(x1, y1, x2, y2);
    }

    public float dist(PVector p1, PVector p2) {
        return PVector.dist(p1, p2);
    }

    public float distSq(double x1, double y1, double x2, double y2) {
        return PVector.distSq(x1, y1, x2, y2);
    }

    public float distSq(PVector p1, PVector p2) {
        return PVector.distSq(p1, p2);
    }

    public float exp(double n) {
        return (float) Math.exp(n);
    }

    public float lerp(double start, double stop, double amt) {
        return Lerp.lerp(start, stop, amt);
    }

    public float lerpSmooth(double start, double stop, double amt) {
        return Lerp.lerpSmooth(start, stop, amt);
    }

    public float lerpOvershoot(double start, double stop, double amt) {
        return Lerp.lerpOvershoot(start, stop, amt);
    }

    public float ln(double n) {
        return (float) Math.log(n);
    }

    public float log(double n) {
        return (float) Math.log10(n);
    }

    public float mag(double a, double b) {
        return (float) Math.sqrt(a * a + b * b);
    }

    public float mag(double a, double b, double c) {
        return (float) Math.sqrt(a * a + b * b + c * c);
    }

    public float map(double value, double min1, double max1, double min2, double max2) {
        return MathHelper.map(value, min1, max1, min2, max2);
    }

    public float max(double... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("max() requires at least one value");
        }

        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return (float) max;
    }

    public int max(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("max() requires at least one value");
        }

        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    public float min(double... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("min() requires at least one value");
        }

        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return (float) min;
    }

    public int min(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("min() requires at least one value");
        }

        int min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public float norm(double value, double min, double max) {
        return MathHelper.norm(value, min, max);
    }

    public float pow(double n, double e) {
        return MathHelper.pow(n, e);
    }

    public int round(double n) {
        return (int) Math.round(n);
    }

    public float round(double n, int place) {
        return (float) (Math.round(n * Math.pow(10, place)) / Math.pow(10, place));
    }

    public float sq(double n) {
        return (float) (n * n);
    }

    public float sqrt(double n) {
        return MathHelper.sqrt(n);
    }

    // Trigonometry Functions
    public float acos(double n) {
        return MathHelper.acos(n);
    }

    public float asin(double n) {
        return MathHelper.asin(n);
    }

    public float atan(double n) {
        return MathHelper.atan(n);
    }

    public float atan2(double y, double x) {
        return MathHelper.atan2(y, x);
    }

    public float cos(double angle) {
        return MathHelper.cos(angle);
    }

    public float sin(double angle) {
        return MathHelper.sin(angle);
    }

    public float tan(double angle) {
        return MathHelper.tan(angle);
    }

    public float csc(double angle) {
        return 1 / MathHelper.sin(angle);
    }

    public float sec(double angle) {
        return 1 / MathHelper.cos(angle);
    }

    public float cot(double angle) {
        return 1 / MathHelper.tan(angle);
    }

    public float acsc(double n) {
        return 1 / asin(n);
    }

    public float asec(double n) {
        return 1 / acos(n);
    }

    public float acot(double n) {
        return 1 / atan(n);
    }

    public float degrees(double radians) {
        return (float) Math.toDegrees(radians);
    }

    public float radians(double degrees) {
        return (float) Math.toRadians(degrees);
    }

    // Random Functions
    public float random(double high) {
        return MathHelper.random(high);
    }

    public float random(double low, double high) {
        return MathHelper.random(low, high);
    }

    public float random() {
        return MathHelper.random();
    }

    public char random(char[] array) {
        return MathHelper.random(array);
    }

    public byte random(byte[] array) {
        return MathHelper.random(array);
    }

    public int random(int[] array) {
        return MathHelper.random(array);
    }

    public long random(long[] array) {
        return MathHelper.random(array);
    }

    public float random(float[] array) {
        return MathHelper.random(array);
    }

    public double random(double[] array) {
        return MathHelper.random(array);
    }

    public boolean random(boolean[] array) {
        return MathHelper.random(array);
    }

    public String random(String[] array) {
        return MathHelper.random(array);
    }

    public PVector random(PVector[] array) {
        return MathHelper.random(array);
    }

    public Object random(Object[] array) {
        return MathHelper.random(array);
    }

    public float randomGaussian() {
        return (float) (new Random().nextGaussian());
    }

    // Shuffle
    /**
     * Returns a randomized copy of the array.
     */
    public char[] shuffle(char[] array) {
        char[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public byte[] shuffle(byte[] array) {
        byte[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            byte temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public int[] shuffle(int[] array) {
        int[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public long[] shuffle(long[] array) {
        long[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            long temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public float[] shuffle(float[] array) {
        float[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            float temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public double[] shuffle(double[] array) {
        double[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            double temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public boolean[] shuffle(boolean[] array) {
        boolean[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            boolean temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public String[] shuffle(String[] array) {
        String[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public PVector[] shuffle(PVector[] array) {
        PVector[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            PVector temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    /**
     * Returns a randomized copy of the array.
     */
    public Object[] shuffle(Object[] array) {
        Object[] shuffled = array.clone();
        Random random = new Random();
        for (int i = shuffled.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Object temp = shuffled[index];
            shuffled[index] = shuffled[i];
            shuffled[i] = temp;
        }
        return shuffled;
    }

    // color
    public color color(Color color) {
        return new color(color);
    }

    public color color(color c, double a) {
        return new color(c, a);
    }

    public color color(double gray, double alpha) {
        return new color(gray, alpha);
    }

    public color color(double gray) {
        return color(gray, 255);
    }

    public color color(double red, double green, double blue, double alpha) {
        return new color(red, green, blue, alpha);
    }

    public color color(double red, double green, double blue) {
        return color(red, green, blue, 255);
    }

    public color color(String c) {
        return new color(c);
    }

    public int alpha(color color) {
        return color.a;
    }

    public int blue(color color) {
        return color.b;
    }

    public int green(color color) {
        return color.g;
    }

    public int red(color color) {
        return color.r;
    }

    public float brightness(color color) {
        return color.getBrightness();
    }

    public color lerpColor(color c1, color c2, double amt) {
        return color(
                Lerp.lerp(c1.getRed(), c2.getRed(), amt),
                Lerp.lerp(c1.getGreen(), c2.getGreen(), amt),
                Lerp.lerp(c1.getBlue(), c2.getBlue(), amt),
                Lerp.lerp(c1.getAlpha(), c2.getAlpha(), amt));
    }

    public float saturation(color color) {
        float[] hsb = color.RGBtoHSB();
        return hsb[1];
    }

    public float hue(color color) {
        float[] hsb = color.RGBtoHSB();
        return hsb[0];
    }

    public void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveStrings(String[] lines, String filename) {
        try {
            Files.write(Paths.get(filename), Arrays.asList(lines));
        } catch (IOException e) {
        }
    }

    public void saveStrings(java.util.List<String> lines, String filename) {
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException e) {
        }
    }

    /**
     * Runs a shader on the entire image. The Dispatch method of the shader should
     * be written for one pixel and it will be run for every pixel in the image in
     * parallel.
     * 
     * @param shader
     */
    public void shaderTexture(Shader shader) {
        int cores = Runtime.getRuntime().availableProcessors();
        // Create columns of chunks
        int threadWidth = PComponent.width / cores;
        int threadWidthRemainder = PComponent.width % cores;

        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            final int xStart = i * threadWidth;
            int w = threadWidth;
            if (i == cores - 1) {
                w += threadWidthRemainder;
            }
            final int xEnd = xStart + w;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = xStart; x < xEnd; x++) {
                        for (int y = 0; y < PComponent.height; y++) {
                            shader.Dispatch(new PVector(x, y));
                        }
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs a shader on an array. The Dispatch method of the shader should be
     * written for one element in the array.
     * 
     * @param shader
     * @param arrayLength
     */
    public void shaderArray(Shader shader, int arrayLength) {
        int cores = Runtime.getRuntime().availableProcessors();
        // Sections of the array
        int threadWidth = arrayLength / cores;
        int threadWidthRemainder = arrayLength % cores;

        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            final int xStart = i * threadWidth;
            int w = threadWidth;
            if (i == cores - 1) {
                w += threadWidthRemainder;
            }
            final int xEnd = xStart + w;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = xStart; x < xEnd; x++) {
                        shader.Dispatch(x);
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
