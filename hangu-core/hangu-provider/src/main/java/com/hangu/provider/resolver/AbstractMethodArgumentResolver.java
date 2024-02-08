package com.hangu.provider.resolver;

import com.hangu.common.entity.HttpServletRequest;
import com.hangu.provider.binder.WebDataBinder;
import java.lang.reflect.Parameter;
import java.util.Objects;
import org.springframework.core.convert.ConversionService;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:31
 */
public abstract class AbstractMethodArgumentResolver implements MethodArgumentResolver {

    protected ConversionService conversionService;

    public AbstractMethodArgumentResolver(ConversionService conversionService) {
        if(Objects.isNull(conversionService)) {
            this.conversionService = WebDataBinder.CONVERSION_SERVICE;
        } else {
            this.conversionService = conversionService;
        }
    }
}
