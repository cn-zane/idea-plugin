package com.zane.pligin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;

public class CamelCase extends AnAction {

    private static final String UNDERLINE_STRING = "_";

    public static final char UNDERLINE_CHAR = '_';

    /**
     * 执行操作
     *
     * @param e 事件
     * @author cn_zane@outlook.com
     * @date 2024/01/25
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前编辑器
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor != null) {
            // 获取选中的文本
            String selectedText = editor.getSelectionModel().getSelectedText();

            if (selectedText != null) {
                final StringBuilder sb = new StringBuilder(selectedText.length());
                String[] lines = selectedText.split("\\r?\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (line.contains(UNDERLINE_STRING)) {
                        if (Character.isUpperCase(line.charAt(0))) {
                            // 小驼峰
                            line = toCamelCase(line);
                        } else {
                            // 大驼峰
                            line = toPascalCase(line);
                        }
                    } else {
                        if (Character.isLowerCase(line.charAt(0))) {
                            // 小写下划线
                            line = toSymbolCase(line, UNDERLINE_CHAR);
                        } else {
                            // 大写下划线
                            line = toSymbolCase(line, UNDERLINE_CHAR).toUpperCase();
                        }
                    }
                    sb.append(line);
                    if (i < lines.length - 1) {
                        sb.append("\n");
                    }
                }

                final String text = sb.toString();

                // 在写操作中执行替换操作
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> editor.getDocument().replaceString(
                        editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd(),
                        text
                ));

                // 在这里可以执行你的操作，如弹出对话框等
//                Messages.showMessageDialog(selectedText, "Selected Text", Messages.getInformationIcon());
            }
        }
    }


    /**
     * 小驼峰命名
     *
     * @param name 名字
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/01/24
     */
    public static String toCamelCase(CharSequence name) {
        if (null == name) {
            return null;
        }
        String name2 = name.toString();
        if (name2.contains(UNDERLINE_STRING)) {
            final StringBuilder sb = new StringBuilder(name2.length());
            boolean upperCase = false;
            for (int i = 0; i < name2.length(); i++) {
                char c = name2.charAt(i);
                if (c == UNDERLINE_CHAR) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    /**
     * 帕斯卡命名
     *
     * @param input 输入
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/01/24
     */
    public static String toPascalCase(CharSequence input) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNextChar = true;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '_') {
                capitalizeNextChar = true;
            } else if (Character.isLetterOrDigit(currentChar)) {
                if (capitalizeNextChar) {
                    sb.append(Character.toUpperCase(currentChar));
                    capitalizeNextChar = false;
                } else {
                    sb.append(Character.toLowerCase(currentChar));
                }
            } else {
                sb.append(currentChar);
                capitalizeNextChar = true;
            }
        }
        return sb.toString();
    }

    /**
     * 小写下划线
     *
     * @param str    str
     * @param symbol 象征
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/01/24
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }
        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
            if (Character.isUpperCase(c)) {
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
                if (null != preChar && Character.isUpperCase(preChar)) {
                    sb.append(c);
                } else if (null != nextChar && Character.isUpperCase(nextChar)) {
                    if (null != preChar && symbol != preChar) {
                        sb.append(symbol);
                    }
                    sb.append(c);
                } else {
                    if (null != preChar && symbol != preChar && !Character.isWhitespace(preChar)) {
                        sb.append(symbol);
                    }
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                if (sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() - 1)) && symbol != c) {
                    sb.append(symbol);
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
