package com.havedroid.dddsched.Util;

import android.content.Context;

import java.io.*;

public class FileHelper {
    private FileHelper() {
    }

    public static void SaveFile(Context context, String fileName, Serializable content) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(content);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static <T extends Serializable> T LoadFile(Context context, String fileName) {
        FileInputStream fis;
        try {
            fis = context.openFileInput(fileName);

            ObjectInputStream is = new ObjectInputStream(fis);
            T retValue = (T) is.readObject();
            is.close();
            return retValue;
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OptionalDataException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (StreamCorruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


}
