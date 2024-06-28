package com.zane.plugin;

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
                            if (line.equals(line.toLowerCase())) {
                                // 大驼峰
                                line = toPascalCase(line);
                            } else {
                                // 小写下划线
                                line = toSymbolCase(line, UNDERLINE_CHAR);
                            }
                        } else {
                            if (isFirstLetterUppercase(line)) {
                                // 单个单词帕斯卡
                                line = line.toUpperCase();
                            } else if (line.equals(line.toUpperCase())) {
                                // 小驼峰
                                line = line.toLowerCase();
                            } else {
                                // 大写下划线
                                line = toSymbolCase(line, UNDERLINE_CHAR).toUpperCase();
                            }
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
     * 检查字符串是否以大写字母开头，其余字符均为小写。
     *
     * @param str 待检查的字符串。
     * @return boolean
     * @author cn_zane@outlook.com
     * @date 2024/06/28
     */
    public static boolean isFirstLetterUppercase(String str) {
        // 获取字符串的第一个字符
        char firstChar = str.charAt(0);

        // 获取字符串除第一个字符外的剩余部分
        String restOfString = str.substring(1);

        // 检查第一个字符是否为大写，且剩余部分是否全部为小写
        return Character.isUpperCase(firstChar) && restOfString.equals(restOfString.toLowerCase());
    }


    /**
     * 将字符串转换为驼峰命名法。
     * 如果字符串包含下划线，则将下划线后的字符转换为大写，并移除下划线，以生成驼峰命名的字符串。
     * 如果字符串不包含下划线，则直接返回原字符串。
     *
     * @param name 待转换的字符串，可以是下划线分隔的字符串。
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/06/28
     */
    public static String toCamelCase(CharSequence name) {
        // 检查输入字符串是否为空，如果为空则直接返回null
        if (null == name) {
            return null;
        }
        // 将CharSequence转换为String类型，以便后续处理
        String name2 = name.toString();
        // 检查字符串是否包含下划线，如果不包含则直接返回原字符串
        if (name2.contains(UNDERLINE_STRING)) {
            // 使用StringBuilder来构建最终的驼峰命名字符串
            final StringBuilder sb = new StringBuilder(name2.length());
            // 标记是否应该将下一个字符转换为大写
            boolean upperCase = false;
            // 遍历字符串中的每个字符
            for (int i = 0; i < name2.length(); i++) {
                char c = name2.charAt(i);
                // 如果当前字符是下划线，则标记下一个字符应该转换为大写
                if (c == UNDERLINE_CHAR) {
                    upperCase = true;
                } else {
                    // 如果当前字符不是下划线，并且上一个字符是下划线，则将当前字符转换为大写，并取消大写标记
                    if (upperCase) {
                        sb.append(Character.toUpperCase(c));
                        upperCase = false;
                    } else {
                        // 如果当前字符不是下划线，并且上一个字符也不是下划线，则将当前字符转换为小写
                        sb.append(Character.toLowerCase(c));
                    }
                }
            }
            // 返回构建好的驼峰命名字符串
            return sb.toString();
        } else {
            // 如果字符串不包含下划线，则直接返回原字符串
            return name2;
        }
    }


    /**
     * 字符串转换为帕斯卡命名法。
     * 帕斯卡命名法规定，每个单词的首字母大写，单词之间没有下划线。
     * 本方法通过移除下划线并转换字母的大小写来实现转换。
     *
     * @param input 待转换的字符串，可以包含下划线和字母数字字符。
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/06/28
     */
    public static String toPascalCase(CharSequence input) {
        StringBuilder sb = new StringBuilder();
        // 标记下一个字符是否应该大写
        boolean capitalizeNextChar = true;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            // 遇到下划线，下一个字符需要大写
            if (currentChar == '_') {
                capitalizeNextChar = true;
            } else if (Character.isLetterOrDigit(currentChar)) {
                // 如果当前字符是字母或数字
                if (capitalizeNextChar) {
                    // 如果需要大写，则转换为大写，并设置标记为不需要再大写
                    sb.append(Character.toUpperCase(currentChar));
                    capitalizeNextChar = false;
                } else {
                    // 否则，转换为小写
                    sb.append(Character.toLowerCase(currentChar));
                }
            } else {
                // 如果当前字符不是字母或数字，直接追加，并设置标记为下一个字符需要大写
                sb.append(currentChar);
                capitalizeNextChar = true;
            }
        }
        return sb.toString();
    }


    /**
     * 将字符串转换为使用指定符号分隔的驼峰式命名。
     *
     * @param str    待转换的字符串
     * @param symbol 分隔符，用于分隔大写字母
     * @return {@link String }
     * @author cn_zane@outlook.com
     * @date 2024/06/28
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        // 如果输入字符串为null，直接返回null
        if (str == null) {
            return null;
        }
        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            // 获取当前字符的前一个字符，如果不存在则为null
            final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
            // 判断当前字符是否为大写字母
            if (Character.isUpperCase(c)) {
                // 获取当前字符的下一个字符，如果不存在则为null
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
                // 如果前一个字符和当前字符都是大写，直接添加当前字符
                if (null != preChar && Character.isUpperCase(preChar)) {
                    sb.append(c);
                }
                // 如果下一个字符是大写，且前一个字符不为当前符号，添加符号后再添加当前字符
                else if (null != nextChar && Character.isUpperCase(nextChar)) {
                    if (null != preChar && symbol != preChar) {
                        sb.append(symbol);
                    }
                    sb.append(c);
                }
                // 其他情况下，将当前字符转换为小写，并根据需要添加符号
                else {
                    if (null != preChar && symbol != preChar && !Character.isWhitespace(preChar)) {
                        sb.append(symbol);
                    }
                    sb.append(Character.toLowerCase(c));
                }
            }
            // 如果当前字符不是大写字母
            else {
                // 如果StringBuilder中已有字符，并且最后一个字符是大写，且当前字符不为当前符号，添加符号
                if (sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() - 1)) && symbol != c) {
                    sb.append(symbol);
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
