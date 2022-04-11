package com.example.shippingbotserver.view;


import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TextEditor {

    public String getText(String description) {
        String[] split;
        if (description.contains("\n")) {
            split = description.split("\n");
        } else {
            split = description.split(" ");
        }
        return Arrays.stream(split).skip(1).collect(Collectors.joining(" "));
    }

    public String getTitle(String description) {
        String[] split;
        if (description.contains("\n")) {
            split = description.split("\n");
        } else {
            split = description.split(" ");
        }
        return trimOfVeryLargeTitle(split[0]);
    }


    public int sizeOfString(int len) {
        double DEFAULT_SIZE_OF_STRING = 28;
        if (len != DEFAULT_SIZE_OF_STRING) {
            int percent = (int) Math.abs((((DEFAULT_SIZE_OF_STRING - len) / len) * 100));
            return (int) Math.abs((DEFAULT_SIZE_OF_STRING / 100) * (100 + percent));
        }
        return (int) DEFAULT_SIZE_OF_STRING;
    }

    public int sizeOfFont(String text) {
        double sizeStd = 28.0;
        double size = text.length();
        double MAX_SIZE_TEXT = 450.0;
        if (text.length() > MAX_SIZE_TEXT) {
            int percent = (int) (((size - MAX_SIZE_TEXT) / MAX_SIZE_TEXT) * 100);
            sizeStd = Math.abs((sizeStd / 100.0) * (100.0 - percent));
        }
        return (int) sizeStd;
    }

    public void splitToN(List<String> lst) {
        String[] sentences;
        for (int i = 0; i < lst.size(); i++) {
            if (containN(lst.get(i))) {
                sentences = lst.get(i).split("\n");
                lst.remove(i);
                for (String sentence : sentences) {
                    lst.add(i++, sentence);
                }
            }
        }
    }

    private String trimOfVeryLargeTitle(String title) {
        int MAX_SIZE_TITLE = 17;
        if (title.length() > MAX_SIZE_TITLE) {
            return title.substring(0, MAX_SIZE_TITLE) + "...";
        }
        return title;
    }

    private boolean containN(String s) {
        return s.split("\n").length > 1;
    }

    public String textEditor(String input, int lenDefault) {
        String sentences = stringEditor(input.trim(), lenDefault);
        StringBuilder str = new StringBuilder();
        return sentences;
    }

    private String[] trimToLen(String s, int len) {
        String[] str;
        String s1 = new String(s);
        for (int j = len; j < s1.length(); j += len) {
            s1 = charAdd(s1, '\n', j);
        }
        str = s1.split("\n");
        for (int i = 1; i < str.length; i++) {
            if (str[i - 1].length() + str[i].length() <= len) {
                str[i - 1] += str[i];
                str = reAlloc(str, i);
                i--;
            }
        }
        return str;
    }

    private String moveN(String s) {
        if (s.charAt(s.length() - 1) == '\n') {
            s = s.substring(0, s.length() - 1);
        }
        char[] arr = s.toCharArray();
        int pos;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '\n' && arr[i + 1] != ' ' && arr[i - 1] != ' ') {
                pos = i;
                while (true) {
                    --i;
                    if (arr[i] == ' ') {
                        arr[i] = '\n';
                        arr[pos] = 127;
                        i = pos;
                        break;
                    } else if (i == 0) {
                        i = pos;
                        break;
                    }
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : arr) {
            if (c != 127) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private String[] reAlloc(String[] s, int pos) {
        String[] res = new String[s.length - 1];
        int j = 0;
        for (int i = 0; i < s.length; i++) {
            if (i != pos) {
                res[j] = s[i];
                j++;
            }
        }
        return res;
    }

    private String stringEditor(String input, int lenDefault) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strings = trimToLen(input, lenDefault);

        for (String obj : strings) {
            stringBuilder.append(obj).append('\n');
        }
        return moveN(stringBuilder.toString());
    }

    private String charAdd(String s, char ch, int pos) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pos >= s.length()) {
            stringBuilder.append(s);
            stringBuilder.append(ch);
            return stringBuilder.toString();
        }
        stringBuilder.append(s, 0, pos);
        stringBuilder.append(ch);
        stringBuilder.append(s, pos, s.length());
        return stringBuilder.toString();
    }
}
