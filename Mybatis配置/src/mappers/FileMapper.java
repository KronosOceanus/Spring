package mappers;

import entity.TestFile;

public interface FileMapper {
    TestFile getFile(Integer id);
    int insertFile(TestFile testFile);
}
