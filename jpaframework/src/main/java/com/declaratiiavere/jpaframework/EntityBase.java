package com.declaratiiavere.jpaframework;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.*;

/**
 * Base class for all JPA entities.
 *
 * @author Razvan Dani
 */
public abstract class EntityBase implements Serializable {
    /**
     * Performs a deep clone of all the attributes. All atributes of the transfer object have to be Serializable.
     *
     * @return The cloned object
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        Object deepClonedObject;

        try {
            ByteArrayOutputStream serializationOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializationOutputStream);
            objectOutputStream.writeObject(this);
            ByteArrayInputStream deserializationInputStream = new ByteArrayInputStream(serializationOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(deserializationInputStream);
            deepClonedObject = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();

            throw new CloneNotSupportedException(e.getMessage());
        }

        return deepClonedObject;
    }

}
