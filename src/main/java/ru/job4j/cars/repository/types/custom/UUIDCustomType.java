package ru.job4j.cars.repository.types.custom;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class UUIDCustomType implements UserType {

    private static final int[] SQL_TYPE = new int[] {Types.VARCHAR};

    @Override
    public int[] sqlTypes() {
        return SQL_TYPE;
    }

    @Override
    public Class returnedClass() {
        return UUID.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        UUID uuid1 = (UUID) o;
        UUID uuid2 = (UUID) o1;
        return Objects.equals(uuid1, uuid2);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) throws HibernateException, SQLException {
        String result = rs.getString(names[0]);
        UUID uuid = null;
        if (result != null) {
            uuid = UUID.fromString(result);
        }
        return uuid;
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (Objects.isNull(value)) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            UUID uuid = (UUID) value;
            String result = uuid.toString();
            ps.setString(index, result);
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
