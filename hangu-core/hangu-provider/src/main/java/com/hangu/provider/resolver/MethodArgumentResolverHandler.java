package com.hangu.provider.resolver;

import com.hangu.common.entity.HttpServletRequest;
import com.hangu.common.entity.HttpServletResponse;
import com.hangu.common.enums.ErrorCodeEnum;
import com.hangu.common.exception.RpcInvokerException;
import com.hangu.provider.resolver.impl.FormDataMethodArgumentResolver;
import com.hangu.provider.resolver.impl.JsonMethodArgumentResolver;
import com.hangu.provider.resolver.impl.PrimitiveMethodArgumentResolver;
import com.hangu.provider.resolver.impl.RequestResponseMethodArgumentResolver;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.ConversionService;

/**
 * @author wuzhenhong
 * @date 2024/2/8 14:53
 */
public class MethodArgumentResolverHandler {

    public static final MethodArgumentResolverHandler DEFAULT_RESOLVER = new MethodArgumentResolverHandler();

    private ConversionService conversionService;
    private List<MethodArgumentResolver> resolverList;

    public MethodArgumentResolverHandler() {
        this.init();
    }

    public MethodArgumentResolverHandler(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.init();
    }

    private void init() {
        this.resolverList = new ArrayList<>();
        this.addDefaultResolvers();
    }

    public Object resolver(Parameter parameter, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        for (MethodArgumentResolver resolver : resolverList) {
            if (resolver.support(parameter, httpServletRequest, httpServletResponse)) {
                return resolver.resolver(parameter, httpServletRequest, httpServletResponse);
            }
        }
        throw new RpcInvokerException(ErrorCodeEnum.FAILURE.getCode(),
            String.format("不能解析类型为【%s】，名字为【%s】的请求参数！", parameter.getType().getName(),
                parameter.getName()));
    }

    private void addDefaultResolvers() {
        this.resolverList.add(new RequestResponseMethodArgumentResolver());
        this.resolverList.add(new PrimitiveMethodArgumentResolver(this.conversionService));
        this.resolverList.add(new JsonMethodArgumentResolver());
        this.resolverList.add(new FormDataMethodArgumentResolver(this.conversionService));
    }

    /**
     * 添加自定义解析器
     */
    public void addCustomerResolver(MethodArgumentResolver argumentResolver) {
        this.resolverList.add(argumentResolver);
    }
}
