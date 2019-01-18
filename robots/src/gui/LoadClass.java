package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class LoadClass extends ClassLoader {
    public HashMap<String,Class> classes =new HashMap<>();//ключ - имя класса, значение - объект класса Class
    public final String path;

    public LoadClass(String path) {
        this.path = path;

        File folder = new File(path);//место где будут мои классы лежать
        File[] arrayOfFiles = folder.listFiles();
        if (arrayOfFiles.length != 0) {
            for (File file : arrayOfFiles) {
                byte[] bytes;
                try {
                    bytes = new byte[(int)file.length()];
                    FileInputStream inputStream=new FileInputStream(file);//по имени файла получает байткод, а по байткоду загружает класс
                    try{
                        inputStream.read(bytes,0,bytes.length);
                    }
                    finally {
                        inputStream.close();
                    }
                    Class cls = defineClass(null, bytes, 0, bytes.length);
                    classes.put(file.getName(), cls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
