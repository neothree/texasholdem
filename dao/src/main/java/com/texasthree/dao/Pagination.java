package com.texasthree.dao;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author: neo
 * @create: 2022-09-28 12:46
 */
public class Pagination<T> {
    /**
     * 第 number 页
     */
    private int number = 0;
    /**
     * 每页大小
     */
    private int size = 30;
    /**
     * 数据
     */
    private List<T> content;
    /**
     * 总页数
     */
    private int totalPages;
    /**
     * 总数据
     */
    private long totalElements;

    public Pagination() {
    }

    public Pagination(int number, int size) {
        this.number = number;
        this.size = size;
    }

    public Pagination(Page<T> v) {
        this.number = v.getNumber();
        this.size = v.getSize();
        this.content = v.getContent();
        this.totalPages = v.getTotalPages();
        this.totalElements = v.getTotalElements();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
