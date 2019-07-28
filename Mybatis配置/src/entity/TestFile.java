package entity;

import org.apache.ibatis.io.Resources;

import java.io.InputStream;

public class TestFile {

    private Integer id;
    private byte[] content;

    public TestFile(){}
    public TestFile(String fileName){
        try(
                InputStream is = Resources.getResourceAsStream(fileName);
        ) {
            content = new byte[is.available()];
            is.read(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
