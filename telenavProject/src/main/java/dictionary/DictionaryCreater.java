package dictionary;

import java.io.File;
import java.io.IOException;

public interface DictionaryCreater {
    void originateDictionary(File inputFile, File outputFile);
    void incrementalupdateDictionary(File inputFile, File outputFile);
}
