package com.edol.testdomain;

import com.edol.data.type.DBEnum;

/**
 * Created by mind on 8/27/15.
 */
public enum TTEnum implements DBEnum {
    TEST_1(1),

    TEST_2(2),;

    private int value;

    TTEnum(int value) {
        this.value = value;
    }

    @Override
    public int getIntValue() {
        return value;
    }
}
