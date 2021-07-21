package com.example.mpdomaci22;

public class ListViewBean {


        String source;
        String score;

        public ListViewBean() {
        }

        public ListViewBean(String source,String score) {
            super();
            this.source = source;
            this.score = score;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

}
