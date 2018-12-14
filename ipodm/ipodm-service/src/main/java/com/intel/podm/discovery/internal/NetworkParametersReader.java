/*
 * Copyright (c) 2015-2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.podm.discovery.internal;

import static com.intel.podm.common.types.Health.OK;
import static com.intel.podm.common.types.State.ENABLED;
import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.list;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intel.podm.common.types.net.MacAddress;
import com.intel.podm.config.network.dto.EthernetInterfaceDto;
import com.intel.podm.config.network.dto.NetworkIpV4AddressDto;
import com.intel.podm.config.network.dto.VlanEthernetInterfaceDto;

//@Dependent
@Component
//@SuppressWarnings({"checkstyle:MethodCount"})
class NetworkParametersReader {

    private static final String VLAN_SEPARATOR = ".";
    private static final String VLAN_SEPARATOR_REGEX = "\\.";

    private static final Logger logger = LoggerFactory.getLogger(NetworkParametersReader.class);

    public Iterable<EthernetInterfaceDto> discoverEnabledSystemNetworkInterfaces() {
        Map<String, EthernetInterfaceDto> networkInterfacesMap = new HashMap<>();
        List<EthernetInterfaceDto> vlanEthernetInterfaceDtos = new ArrayList<>();
        List<NetworkInterface> networkInterfaces;

        try {
            networkInterfaces = list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                if (shouldBeOmitted(networkInterface)) { //忽略loopback网卡
                    continue;
                }
                //根据networkInterface是否为vlan，将其分别放入networkInterfacesMap，或者vlanEthernetInterfaceDtos中
                processNetworkInterface(networkInterfacesMap, vlanEthernetInterfaceDtos, networkInterface);
            }
            //给vlanEthernetInterfaceDtos里面的对象添加vlan和IPV4地址
            mapVlansToNetworkInterfaces(networkInterfacesMap, vlanEthernetInterfaceDtos);
        } catch (SocketException e) {
            logger.error("Could not discover system network interfaces", e);
        }

        return networkInterfacesMap.values();
    }

    private void processNetworkInterface(Map<String, EthernetInterfaceDto> networkInterfacesMap,
                                         List<EthernetInterfaceDto> vlanEthernetInterfaceDtos,
                                         NetworkInterface networkInterface) {
    	//把networkInterface转化为dto对象
        EthernetInterfaceDto ethernetInterfaceDto = getNetworkInterfaceDto(networkInterface);

        if (isVlan(networkInterface)) { //根据名称是否有"."判断其是否是vlan
            vlanEthernetInterfaceDtos.add(ethernetInterfaceDto);
        } else {
        	//否则放入map当中去
            EthernetInterfaceDto oldEthernetInterfaceDto = networkInterfacesMap.put(ethernetInterfaceDto.getName(), ethernetInterfaceDto);
            if (oldEthernetInterfaceDto != null) {
                logger.warn("Duplicated network interface: {}", oldEthernetInterfaceDto.getName());
            }
        }
    }

    private void mapVlansToNetworkInterfaces(Map<String, EthernetInterfaceDto> networkInterfacesMap, List<EthernetInterfaceDto> vlanNetworkInterfaces) {
        for (EthernetInterfaceDto vlanNetworkInterface : vlanNetworkInterfaces) {
            String networkInterfaceName = getNetworkInterfaceNameForVlanNetworkInterface(vlanNetworkInterface);

            EthernetInterfaceDto ethernetInterfaceDto = networkInterfacesMap.get(networkInterfaceName);
            if (ethernetInterfaceDto == null) {
                logger.warn("Network interface {} for Vlan network interface {} not found", networkInterfaceName, vlanNetworkInterface.getName());
            } else {
                VlanEthernetInterfaceDto vlanEthernetInterfaceDto = getVlanNetworkInterfaceDto(vlanNetworkInterface);
                ethernetInterfaceDto.getVlans().add(vlanEthernetInterfaceDto);
                ethernetInterfaceDto.getIpV4Addresses().addAll(vlanNetworkInterface.getIpV4Addresses());
            }
        }
    }

    private EthernetInterfaceDto getNetworkInterfaceDto(NetworkInterface networkInterface) {
        EthernetInterfaceDto interfaceDto = new EthernetInterfaceDto();
        interfaceDto.setName(networkInterface.getName());
        interfaceDto.setDescription(networkInterface.getDisplayName());

        interfaceDto.setVlanEnable(isVlan(networkInterface));
        interfaceDto.setVlanId(getVlanIdForNetworkInterface(networkInterface));

        interfaceDto.setFrameSize(getMtu(networkInterface));
        interfaceDto.setMacAddress(getMacAddress(networkInterface));
        interfaceDto.setIpV4Addresses(getInetAddresses(networkInterface));
        interfaceDto.setHealth(OK);
        interfaceDto.setState(ENABLED);
        return interfaceDto;
    }

    private VlanEthernetInterfaceDto getVlanNetworkInterfaceDto(EthernetInterfaceDto ethernetInterfaceDto) {
        VlanEthernetInterfaceDto vlanEthernetInterfaceDto = new VlanEthernetInterfaceDto();
        vlanEthernetInterfaceDto.setName(ethernetInterfaceDto.getName());
        vlanEthernetInterfaceDto.setDescription(ethernetInterfaceDto.getDescription());
        vlanEthernetInterfaceDto.setTagged(true);
        vlanEthernetInterfaceDto.setVlanEnable(true);
        vlanEthernetInterfaceDto.setVlanId(ethernetInterfaceDto.getVlanId());

        return vlanEthernetInterfaceDto;
    }

    private boolean shouldBeOmitted(NetworkInterface networkInterface) {
        try {
            return networkInterface.isLoopback();
        } catch (SocketException e) {
            logger.error(format("Network interface identification failed for Network Interface: %s", networkInterface.getName()), e);
        }

        return false;
    }

    private List<NetworkIpV4AddressDto> getInetAddresses(NetworkInterface networkInterface) {
        List<NetworkIpV4AddressDto> inetAddressesDtos = new ArrayList<>();
        List<InetAddress> inetAddresses = list(networkInterface.getInetAddresses());


        for (InetAddress inetAddress : inetAddresses) {
            if (inetAddress instanceof Inet4Address) {
                NetworkIpV4AddressDto addressDto = new NetworkIpV4AddressDto();
                addressDto.setAddress(inetAddress.getHostAddress());
                addressDto.setGateway(null);
                addressDto.setSubnetMask(null);
                inetAddressesDtos.add(addressDto);
            }
        }

        return inetAddressesDtos;
    }

    private MacAddress getMacAddress(NetworkInterface networkInterface) {
        byte[] hardwareAddress = null;

        try {
            hardwareAddress = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            logger.error(format("MAC Address reading failed for Network Interface: %s", networkInterface.getName()), e);
        }

        if (hardwareAddress == null) {
            return null;
        }

        return new MacAddress(hardwareAddress);
    }

    private Integer getMtu(NetworkInterface networkInterface) {
        try {
            return networkInterface.getMTU();
        } catch (SocketException e) {
            logger.error(format("MTU reading failed for network interface: %s", networkInterface.getName()), e);
            return null;
        }
    }

    private boolean isVlan(NetworkInterface networkInterface) {
        return networkInterface.getName().contains(VLAN_SEPARATOR);
    }

    private Integer getVlanIdForNetworkInterface(NetworkInterface networkInterface) {
        String[] parts = networkInterface.getName().split(VLAN_SEPARATOR_REGEX);

        if (parts.length > 1) {
            try {
                return valueOf(parts[1]);
            } catch (NumberFormatException e) {
                logger.error(format("VlanId parsing failed for network interface: %s", networkInterface.getName()), e);
                return null;
            }
        }

        return null;
    }

    private String getNetworkInterfaceNameForVlanNetworkInterface(EthernetInterfaceDto ethernetInterfaceDto) {
        String[] parts = ethernetInterfaceDto.getName().split(VLAN_SEPARATOR_REGEX);

        if (parts.length > 1) {
            return parts[0];
        } else {
            return null;
        }
    }
}
