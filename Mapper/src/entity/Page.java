package entity;

public class Page {

    private int start;
    private int limit;

    public Page(int start, int limit){
        this.start = start;
        this.limit = limit;
    }





    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
