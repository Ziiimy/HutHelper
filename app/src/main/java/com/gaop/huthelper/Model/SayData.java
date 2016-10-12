package com.gaop.huthelper.Model;

import java.util.List;

/**
 * Created by gaop on 16-9-10.
 */
public class SayData {
    /**
     * page_max : 1
     * page_cur : 1
     */

    private InfoBean info;

    private List<Say> posts;

    public List<Say> getPosts() {
        return posts;
    }

    public void setPosts(List<Say> posts) {
        this.posts = posts;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }


    public static class InfoBean {
        private int page_max;
        private String page_cur;

        public int getPage_max() {
            return page_max;
        }

        public void setPage_max(int page_max) {
            this.page_max = page_max;
        }

        public String getPage_cur() {
            return page_cur;
        }

        public void setPage_cur(String page_cur) {
            this.page_cur = page_cur;
        }
    }
}
