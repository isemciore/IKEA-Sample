package com.erik.ikeashoppinglist.demo.misc_com_fmt;

public class Search {
    private String respository_type;

    private String search_term;

    public Search() {
    }

    public Search(String respository_type, String search_term) {
        this.respository_type = respository_type;
        this.search_term = search_term;
    }

    public String getRespository_type() {
        return respository_type;
    }

    public void setRespository_type(String respository_type) {
        this.respository_type = respository_type;
    }

    public String getSearch_term() {
        return search_term;
    }

    public void setSearch_term(String search_term) {
        this.search_term = search_term;
    }
}
