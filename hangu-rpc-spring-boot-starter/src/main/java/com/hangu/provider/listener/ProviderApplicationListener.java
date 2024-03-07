package com.hangu.provider.listener;

import com.hangu.common.entity.RegistryInfo;
import com.hangu.common.manager.HanguRpcManager;
import com.hangu.common.properties.HanguProperties;
import com.hangu.common.registry.RegistryService;
import com.hangu.common.util.CommonUtils;
import com.hangu.provider.annotation.HanguService;
import com.hangu.provider.invoker.RpcInvoker;
import com.hangu.provider.manager.LocalServiceManager;
import com.hangu.provider.manager.NettyServerSingleManager;
import com.hangu.provider.resolver.MethodArgumentResolverHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @date 2023/7/31 14:24
 */
public class ProviderApplicationListener implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    @Autowired
    private HanguProperties hanguProperties;

    @Autowired(required = false)
    private MethodArgumentResolverHandler resolverHandler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();

        Map<String, Object> beanNameMapServiceMap = applicationContext.getBeansWithAnnotation(HanguService.class);
        if (CollectionUtils.isEmpty(beanNameMapServiceMap)) {
            return;
        }
        RegistryService registryService = applicationContext.getBean(RegistryService.class);
        HanguRpcManager.openServer(hanguProperties);
        beanNameMapServiceMap.forEach((beanName, service) -> {
            HanguService hanguService = AnnotationUtils.getAnnotation(service.getClass(), HanguService.class);
            String groupName = hanguService.groupName();
            String interfaceName = hanguService.interfaceName();
            List<String> interfaceNameList = new ArrayList<>();
            if (Objects.isNull(interfaceName) || interfaceName.trim().isEmpty()) {
                Class<?>[] interfaces = service.getClass().getInterfaces();
                if (Objects.isNull(interfaces) || interfaces.length == 0) {
                    interfaceNameList.add(service.getClass().getName());
                } else {
                    Arrays.stream(interfaces).forEach(i -> {
                        interfaceNameList.add(i.getName());
                    });
                }
            } else {
                interfaceNameList.add(interfaceName);
            }
            String version = hanguService.version();
            RpcInvoker rpcInvoker = new RpcInvoker(service, resolverHandler);
            interfaceNameList.stream().forEach(intName -> {
                String key = CommonUtils.createServiceKey(groupName, intName, version);
                // export service
                // step 1.0 本地暴露
                LocalServiceManager.register(key, rpcInvoker);

                // step 2.0 远程暴露
                RegistryInfo registryInfo = new RegistryInfo();
                registryInfo.setGroupName(groupName);
                registryInfo.setInterfaceName(intName);
                registryInfo.setVersion(version);
                registryInfo.setHostInfo(NettyServerSingleManager.getLocalHost());
                registryService.register(registryInfo);
            });
        });
    }

    @Override
    public void destroy() throws Exception {
        HanguRpcManager.closeServer();
    }
}
