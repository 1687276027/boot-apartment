package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

//自定义converter，完成枚举类型向string类型转换
@Component
public class StringToItemTypeConverter implements Converter<String, ItemType> {
    @Override
    public ItemType convert(String source) {

        //获取所有的ItemType枚举类型
        ItemType[] values = ItemType.values();
        for (ItemType itemType : values) {
            if (itemType.getCode().equals(Integer.valueOf(source))) {
                return itemType;
            }
        }

        throw new IllegalArgumentException("code非法");
    }
}
