package com.polymer.application;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestMain {
    public static void main(String[] args) {
        File folder = new File("D:/lib");
        // 检查是否是文件夹
        if (folder.isDirectory()) {
            String[] names = folder.list();  // 只返回名字
            for (String name : names) {
                System.out.println(name);
            }
        }
    }
}
