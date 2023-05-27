package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        // 使用try（开启流的语句）{其它语句}catch（）{}的方式，在编译的时候会自动在后面加上finally进行流的关闭
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败： " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SensitiveFilter f = new SensitiveFilter();
        String[] arr = new String[]{"asdfg","acvbt","cfgdb","cfg","cfgat"};
        for (String s : arr) {
            f.addKeyword(s);
        }
        String asdfg = f.filter("cfgd");
        System.out.println(asdfg);
        System.out.println(f.root);

    }
    // 将一个敏感词添加到前缀树
    // 当前字符： 字符串中正在进行匹配的字符
    // 当前节点： 前缀树中正在进行匹配的节点【初始时为根节点】
    // 思路：对于一个字符串，如果当前字符不是当前节点 子节点中的字符，那么，就应该创建一个节点值为当前字符的节点，然后放到当前节点下，作为当前节点
    //                   如果当前字符是当前节点 子节点中的字符， 那么，就获取当前字符所在的子节点，并作为当前节点
    // 结束：将当前节点的isKeyWord设置为true，说明从根节点到当前字符的路径字符串是一个关键词【敏感词】

    private void addKeyword(String keyword) {
        if(keyword == null || keyword.length() == 0)return;
        char[] chars = keyword.toCharArray();
        TrieNode cur = this.root;
        for (char c : chars) {
            TrieNode subNode = cur.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                cur.addSubNode(c, subNode);
            }
            cur = subNode;
        }
        cur.setKeyWordEnd(true);
    }


    // 当前节点：前缀树上正在进行匹配的节点作为当前节点
    // start ，end 当前正在前缀树上进行匹配的字符串的开始位置和当前位置

    // 思路：当前节点如果和至少一个字符匹配而且当前节点的isKeyWord为ture，说明是敏感词，直接替换，进入缓冲区，然后更新当前节点值为
    // 已经匹配的字符的下一个位置：end + 1
    // 其它情况，匹配字符串到结尾还没有一个节点isKeyWord为ture，或者从树根节点开始就没有子节点和串匹配，那么就加入缓冲区。

    //
    public String myfilter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针
        TrieNode tCur = null;
        // 起始位置
        int start = 0;
        // 终止位置
        int end = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        int len = text.length();
        while (start < len) {
            tCur = root;
            TrieNode subNode = tCur.getSubNode(text.charAt(start));
            if (subNode != null) {
                end = start;
                while(end < len && subNode != null && !subNode.isKeyWordEnd){
                    if (++end < len) {
                        subNode = subNode.getSubNode(text.charAt(end));
                    }else break;

                }
                if (end == len || subNode == null) {
                    sb.append(text.charAt(start));
                    start++;
                }else {
                    sb.append(REPLACEMENT);
                    start = ++end ;
                }
            }else{
                sb.append(text.charAt(start));
                start++;
            }
        }

        return sb.toString();
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                // 开头的符号直接加入，没有影响，中间的符号如果夹杂着敏感词，要被过滤掉
                if(tempNode == root){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = root;
            } else if (tempNode.isKeyWordEnd) {
                // 发现敏感词，将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = root;
            }else {
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2e80~0x9fff  是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }
    // 前缀树
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeyWordEnd = false;

        // 子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
