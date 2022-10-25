package ru.job4j.cars.repository.types.custom;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class StringArrayCustomType implements UserType {

    private static final int[] SQL_ARRAY_TYPE = new int[] {Types.ARRAY};

    @Override
    public int[] sqlTypes() {
        return SQL_ARRAY_TYPE;
    }

    @Override
    public Class returnedClass() {
        return UUID[].class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        UUID[] array = (UUID[]) o;
        UUID[] array1 = (UUID[]) o1;
        return Arrays.equals(array, array1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        UUID[] resultArray = null;
        if (array != null) {
            String[] stringArray = (String[]) array.getArray();
            resultArray = new UUID[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                resultArray[i] = UUID.fromString(stringArray[i]);
            }
        }
        return resultArray;
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (Objects.isNull(value)) {
            ps.setNull(index, Types.ARRAY);
        } else {
            UUID[] uuidArray = (UUID[]) value;
            String[] stringArray = new String[uuidArray.length];
            for (int i = 0; i < uuidArray.length; i++) {
                stringArray[i] = uuidArray[i].toString();
            }
            Array sqlArray = ps.getConnection().createArrayOf("VARCHAR", stringArray);
            ps.setArray(index, sqlArray);
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return o;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return (Serializable) o;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return serializable;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
