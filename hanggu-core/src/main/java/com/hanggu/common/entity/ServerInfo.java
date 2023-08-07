package com.hanggu.common.entity;

import java.util.Objects;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2023/8/4 17:30
 */
@Data
public class ServerInfo {

    private String groupName;

    private String interfaceName;

    private String version;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServerInfo)) {
            return false;
        }
        ServerInfo other = (ServerInfo) obj;
        return Objects.equals(this.groupName, other.getGroupName())
            &&
            Objects.equals(this.interfaceName, other.getInterfaceName())
            &&
            Objects.equals(this.version, other.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.groupName, this.interfaceName, this.version);
    }

}
