package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

//完成String向BaseEnum转换，当String需要向BaseEnum的子类进行转换时，
//ConverterFactory会自动调用getConverter方法，并将需要转换的子类传入，此时可通过字节码加载所有的枚举常量进行比较
@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        //通过字节码加载出所有的枚举常量
        return new Converter<String, T>() {
            @Override
            public T convert(String source) {
                T[] enumConstants = targetType.getEnumConstants();
                for (T enumConstant : enumConstants) {
                    if (enumConstant.getCode().equals(Integer.parseInt(source))) {
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("code:" + source + "非法");
            }
        };
    }
}
