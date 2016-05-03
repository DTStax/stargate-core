package com.tuplejump.stargate.util;

import com.datastax.driver.core.Row;

import java.util.*;

public class Record {

    private Map recordDefinition = new HashMap<String, String>();
    private Map<String, Object> record = new HashMap<String, Object>();

    public Record(String[] fields, String[] fieldTypes, Object[] values) {
        if (fields.length == values.length) {
            for (int i = 0; i < fields.length; i++) {
                recordDefinition.put(fields[i].toLowerCase(), fieldTypes[i]);
                record.put(fields[i].toLowerCase(), values[i]);
            }
        }
    }

    public Record(Row row, String indexCol) {
        row.getColumnDefinitions().iterator().forEachRemaining(field -> {
            String col = field.getName();
            record.put(col.toLowerCase(), row.getObject(col));
        });
        record.remove(indexCol);
    }

    public String getInsertString() {
        Iterator it = record.entrySet().iterator();
        List<String> fieldList = new ArrayList<String>();
        List<Object> valueList = new ArrayList<Object>();
        List<String> types = new ArrayList<String>();
        while (it.hasNext()) {
            Map.Entry map = (Map.Entry) it.next();
            fieldList.add((String) map.getKey());
            valueList.add(map.getValue());
            types.add((String) recordDefinition.get(map.getKey()));
        }
        return "(" + getFieldString(fieldList) + ")values(" + getValueString(valueList, types) + ");";
    }

    private String getFieldString(List<String> list) {
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            result.append(s);
            result.append(",");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

    private String getValueString(List<Object> list, List<String> recordDefinition) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Object s : list) {
            if (s == null) result.append("null,");
            else {
                switch (recordDefinition.get(i)) {
                    case "varchar":
                    case "text":
                        result.append("'" + String.valueOf(s) + "'");
                        break;
                    default:
                        result.append(String.valueOf(s));
                }
                result.append(",");
            }
            i++;
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

    public Map getRecord() {
        return record;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Record.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Record other = (Record) obj;
        if ((this.record == null) ? (other.record != null) : !this.record.equals(other.record)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return record.hashCode();
    }

    @Override
    public String toString() {
        return record.toString();
    }
}