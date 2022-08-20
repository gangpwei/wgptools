package bacardi;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 配置对象
 * @author gangpeng.wgp
 * @date 2019/01/17 上午10:27
 */

public class Cache {

    public static final String LIST_RESOURCE_DO = "LIST_RESOURCE_DO";
    public static final String LIST_RESOURCE_DO_GROUP = "LIST_RESOURCE_DO_GROUP";
    public static final String LIST_OPTION_DO = "LIST_OPTION_DO";
    public static final String MAP_RESOURCE_DO = "MAP_RESOURCE_DO";
    public static final String MAP_OPTION_DO = "MAP_OPTION_DO";

    public static final String MAP = "MAP";

    public static final String INT = "INT";
    public static final String STRING = "STRING";

    public static final String KEY_STR = "KEY_STR";
    public static final String KEY_CLASS = "KEY_CLASS";

    /**
     * 配置第一个字段类型，字符串还是类常量
     * 例如：resourceManager.getResItem(PrivilegeConstant.CLOT_APP_ROLE, roleName)
     * keyType = KEY_CLASS
     *
     */
    private String keyType;

    /**
     * 配置第一个字段完整值
     * 例如：resourceManager.getResItem(PrivilegeConstant.CLOT_APP_ROLE, roleName)
     * keyValue = com.ali.b2b.crm.base.privilege.PrivilegeConstant.CLOT_APP_ROLE
     */
    private String keyValue;

    /**
     * 配置第一个字段的类名
     * 例如：resourceManager.getResItem(PrivilegeConstant.CLOT_APP_ROLE, roleName)
     * keyClass = com.ali.b2b.crm.base.privilege.PrivilegeConstant
     */
    private String keyClass;

    /**
     * 配置第一个字段的字段名
     * 例如：resourceManager.getResItem(PrivilegeConstant.CLOT_APP_ROLE, roleName)
     * keyField = CLOT_APP_ROLE
     */
    private String keyField;

    /**
     * 配置第一个字段的实际值
     * 例如：resourceManager.getResItem(PrivilegeConstant.CLOT_APP_ROLE, roleName)
     * realKey = "APP_ROLE"
     */
    private String realKey;

    /**
     * 第二个参数，只有常量类型用到，INT, STRING
     */
    private String keyTypeParam2;

    private String keyValueParam2;

    private String keyClassParam2;

    private String keyFieldParam2;

    private String realKeyParam2;

    /**
     * 配置的值类型，取值范围：LIST_RESOURCE_DO, LIST_OPTION_DO, LIST_RESOURCE_DO_GROUP, MAP_RESOURCE_DO, MAP_OPTION_DO
     */
    private String valueType;

    /**
     * 引用配置的所有文件
     */
    private List<String> files;

    /**
     * 配置的具体值
     */
    private Object cacheData;

    /**
     * 配置是否有效
     */
    private boolean valid = true;

    /**
     * 配置值是否为空
     */
    private boolean empty = false;

    /**
     * 值的类名
     */
    private String valueClass;



    public Cache() {
    }

    public Cache(String keyType, String keyValue, String valueType) {
        this.keyType = keyType;
        this.keyValue = keyValue;
        this.valueType = valueType;
    }

    public Cache(String keyType, String keyValue, String keyClass, String keyField, String valueType) {
        this.keyType = keyType;
        this.keyValue = keyValue;
        this.keyClass = keyClass;
        this.keyField = keyField;
        this.valueType = valueType;
    }

    public Cache(String keyType, String keyValue, String keyClass, String keyField, String valueType, String realKey, String file) {
        this.keyType = keyType;
        this.keyValue = keyValue;
        this.keyClass = keyClass;
        this.keyField = keyField;
        this.valueType = valueType;
        this.realKey = realKey;
        addFile(file);
    }

    public String getRealKey() {
        return realKey;
    }

    public void setRealKey(String realKey) {
        this.realKey = realKey;
    }



    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(String keyClass) {
        this.keyClass = keyClass;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public Object getCacheData() {
        return cacheData;
    }

    public void setCacheData(Object cacheData) {
        this.cacheData = cacheData;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }

    /**
     * 是否为常量配置
     * @return
     */
    @JsonIgnore
    public boolean isConstant(){
        if(INT.equals(getValueType()) || STRING.equals(getValueType())){
            return true;
        }
        return false;
    }

    /**
     * 在配置中心中使用的字段名
     * @return
     */
    @JsonIgnore
    public  String getFieldName() {

        if(!isConstant()){
            if(KEY_CLASS.equals(getKeyType())){
                return getKeyField();
            }else if(KEY_STR.equals(getKeyType())) {
                String str = getRealKey();
                String field = "";
                String[] strArray = str.split(".");
                if(strArray.length >1){
                    field = strArray[0].toLowerCase();
                    for (int i = 1; i < strArray.length; i++) {
                        String temp = strArray[i].toLowerCase();
                        field = Character.toLowerCase(temp.charAt(0)) + temp.substring(1, temp.length());
                    }
                    return field;
                }
                return getKeyValue();
                //return Character.toLowerCase(str.charAt(0)) + str.substring(1, str.length());
            }
        }else{
            if(KEY_CLASS.equals(getKeyTypeParam2())){
                return getKeyFieldParam2();
            }else if(KEY_STR.equals(getKeyTypeParam2())) {
                String str = getRealKeyParam2();
                String field = "";
                String[] strArray = str.split(".");
                if(strArray.length >1){
                    field = strArray[0].toLowerCase();
                    for (int i = 1; i < strArray.length; i++) {
                        String temp = strArray[i].toLowerCase();
                        field = Character.toLowerCase(temp.charAt(0)) + temp.substring(1, temp.length());
                    }
                    return field;
                }
                return getKeyValueParam2();
                //return Character.toLowerCase(str.charAt(0)) + str.substring(1, str.length());
            }
        }
        return null;
    }

    @JsonIgnore
    public String getKey(){
        if(!isConstant()){
            return getRealKey();
        }else{
            return getRealKeyParam2();
        }

    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void addFile(String file){
        if(files == null){
            files = new ArrayList<String>();
        }
        files.add(file);
    }

    public String getKeyTypeParam2() {
        return keyTypeParam2;
    }

    public void setKeyTypeParam2(String keyTypeParam2) {
        this.keyTypeParam2 = keyTypeParam2;
    }

    public String getKeyValueParam2() {
        return keyValueParam2;
    }

    public void setKeyValueParam2(String keyValueParam2) {
        this.keyValueParam2 = keyValueParam2;
    }

    public String getKeyClassParam2() {
        return keyClassParam2;
    }

    public void setKeyClassParam2(String keyClassParam2) {
        this.keyClassParam2 = keyClassParam2;
    }

    public String getKeyFieldParam2() {
        return keyFieldParam2;
    }

    public void setKeyFieldParam2(String keyFieldParam2) {
        this.keyFieldParam2 = keyFieldParam2;
    }

    public String getRealKeyParam2() {
        return realKeyParam2;
    }

    public void setRealKeyParam2(String realKeyParam2) {
        this.realKeyParam2 = realKeyParam2;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Cache{");
        sb.append("keyType='").append(keyType).append('\'');
        sb.append(", keyValue='").append(keyValue).append('\'');
        sb.append(", keyClass='").append(keyClass).append('\'');
        sb.append(", keyField='").append(keyField).append('\'');
        sb.append(", valueType='").append(valueType).append('\'');
        sb.append(", realKey='").append(realKey).append('\'');
        sb.append(", files=").append(files);
        sb.append(", cacheData=").append(cacheData);
        sb.append(", valid=").append(valid);
        sb.append(", empty=").append(empty);
        sb.append(", valueClass='").append(valueClass).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Cache)) { return false; }

        Cache cache = (Cache)o;

        if (keyType != null ? !keyType.equals(cache.keyType) : cache.keyType != null) { return false; }
        if (keyValue != null ? !keyValue.equals(cache.keyValue) : cache.keyValue != null) { return false; }
        if (keyClass != null ? !keyClass.equals(cache.keyClass) : cache.keyClass != null) { return false; }
        if (keyField != null ? !keyField.equals(cache.keyField) : cache.keyField != null) { return false; }
        if (keyTypeParam2 != null ? !keyTypeParam2.equals(cache.keyTypeParam2) : cache.keyTypeParam2 != null) { return false; }
        if (keyValueParam2 != null ? !keyValueParam2.equals(cache.keyValueParam2) : cache.keyValueParam2 != null) { return false; }
        if (keyClassParam2 != null ? !keyClassParam2.equals(cache.keyClassParam2) : cache.keyClassParam2 != null) { return false; }
        if (keyFieldParam2 != null ? !keyFieldParam2.equals(cache.keyFieldParam2) : cache.keyFieldParam2 != null) { return false; }
        return valueType != null ? valueType.equals(cache.valueType) : cache.valueType == null;
    }

    @Override
    public int hashCode() {
        int result = keyType != null ? keyType.hashCode() : 0;
        result = 31 * result + (keyValue != null ? keyValue.hashCode() : 0);
        result = 31 * result + (keyClass != null ? keyClass.hashCode() : 0);
        result = 31 * result + (keyField != null ? keyField.hashCode() : 0);
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (realKey != null ? realKey.hashCode() : 0);
        return result;
    }


}
