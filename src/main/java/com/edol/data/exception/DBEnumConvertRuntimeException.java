/**
 * 版权声明  版权所有 违者必究
 * 版本号  1.0
 * 修订记录:
 * 1)更改者Lucky
 * 时　间：2016/06/15 13:30
 * 描　述：创建
 */
package com.edol.data.exception;

/**
 * <pre>
 * 功能说明
 * </pre>
 * <p>
 * <br>
 * JDK版本:1.6 或更高
 *
 * @author Lucky
 * @version 1.0
 * @since 1.0
 */
public class DBEnumConvertRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -6774501922165042769L;

    private String message;

    public DBEnumConvertRuntimeException(String message) {
        super(message);

        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
