package com.hanggu.common.serialization.factory;

import com.hanggu.common.serialization.SerialInput;
import com.hanggu.common.serialization.SerialInputFactory;
import com.hanggu.common.serialization.SerialOutput;
import com.hanggu.common.serialization.SerialOutputFactory;
import com.hanggu.common.serialization.impl.Hessian2SerialInput;
import com.hanggu.common.serialization.impl.Hessian2SerialOutput;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author wuzhenhong
 * @date 2023/8/1 16:31
 */
public class Hessian2SerialInputFactory implements SerialInputFactory {

    @Override
    public SerialInput createSerialization(InputStream inputStream) {

        Hessian2SerialInput input = new Hessian2SerialInput(inputStream);
        return input;
    }
}