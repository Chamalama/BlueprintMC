package mike.blueprint.util;

import java.io.*;

public class ByteUtil {

    public static <V> V deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final ObjectInputStream objectIn = new ObjectInputStream(inputStream);
        return (V) objectIn.readObject();
    }

    public static byte[] serialize(Object object) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        return byteArrayOutputStream.toByteArray();
    }

}
